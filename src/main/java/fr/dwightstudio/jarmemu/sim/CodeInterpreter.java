package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.asm.Directive;
import fr.dwightstudio.jarmemu.sim.exceptions.StuckExecutionASMException;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.parse.args.AddressParser;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.*;
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParsers;
import fr.dwightstudio.jarmemu.util.RegisterUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CodeInterpreter {
    private static final Logger logger = Logger.getLogger(CodeInterpreter.class.getName());

    protected StateContainer stateContainer;
    protected HashMap<Integer, ParsedObject> parsedObjects;
    protected ArrayList<Integer> instructionPositions;
    private int currentLine;
    private int lastLine;
    private boolean atTheEnd;
    private ParsedInstruction lastExecuted;
    private int lastExecutedLine;
    private boolean jumped;

    public CodeInterpreter() {
        this.currentLine = -1;
        this.atTheEnd = false;
        jumped = false;
    }

    /**
     * Charge des instructions parsées dans l'exécuteur
     *
     * @param sourceParser le parseur de source utilisé
     */
    public SyntaxASMException load(SourceParser sourceParser) {

        try {
            parsedObjects = sourceParser.parse();
        } catch (SyntaxASMException exception) {
            return exception;
        }

        instructionPositions = computeInstructionsPositions();
        currentLine = 0;
        lastLine = getLastLine();
        this.atTheEnd = false;
        lastExecutedLine = -1;

        return null;
    }

    /**
     * Vérifie toutes les ParsedInstructions et ParsedDirective
     *
     * @return les erreurs si il y en a
     * @apiNote Doit être appelé après un resetState
     */
    public SyntaxASMException[] verifyAll() {
        ArrayList<SyntaxASMException> rtn = new ArrayList<>();

        for (Map.Entry<Integer, ParsedObject> inst : parsedObjects.entrySet()) {
            SyntaxASMException e = inst.getValue().verify(inst.getKey(), () -> new StateContainer(stateContainer));
            if (e != null) rtn.add(e);
        }

        int line = -1;
        try {
            if (stateContainer.getGlobal() != null) {
                ArgumentParsers.LABEL.parse(stateContainer, stateContainer.getGlobal());
            }
        } catch (SyntaxASMException exception) {

            for (Map.Entry<Integer, ParsedObject> inst : parsedObjects.entrySet()) {
                if (inst.getValue() instanceof ParsedDirective directive) {
                    if (directive.getDirective() == Directive.GLOBAL) {
                        line = Math.max(line, inst.getKey());
                    }
                } else if (inst.getValue() instanceof ParsedDirectivePack pack) {
                    for (ParsedObject parsedObject : pack.getContent()) {
                        if (inst.getValue() instanceof ParsedDirective directive) {
                            if (directive.getDirective() == Directive.GLOBAL) {
                                line = Math.max(line, inst.getKey());
                            }
                        }
                    }
                }
            }

            rtn.add(exception.with(line));
        }

        return rtn.toArray(SyntaxASMException[]::new);
    }

    /**
     * Applique toutes les directives, remplace les constantes, etc...
     */
    private void applyDirectives() {
        int off = getLastLine() + 1;
        int pos = 0;

        // Suppression des directives générées
        parsedObjects.replaceAll((k, v) -> {
            if (v instanceof ParsedDirective directive) {
                if (directive.isGenerated()) return null;
            }
            return v;
        });

        // Application de toutes les directives
        for (Map.Entry<Integer, ParsedObject> inst : parsedObjects.entrySet()) {
            try {
                if (inst.getValue() instanceof ParsedDirective parsedDirective) {
                    if (!parsedDirective.isGenerated()) {
                        pos = Math.max(parsedDirective.apply(stateContainer, pos), pos);
                    }
                } else if (inst.getValue() instanceof ParsedDirectivePack parsedDirectivePack) {
                    if (!parsedDirectivePack.isGenerated()) {
                        pos = Math.max(parsedDirectivePack.apply(stateContainer, pos), pos);
                    }
                } else if (inst.getValue() instanceof ParsedDirectiveLabel label) {
                    label.register(stateContainer, pos);
                }
            } catch (SyntaxASMException e) {
                throw e.with(inst.getKey());
            }
        }

        HashMap<Integer, ParsedObject> temp = new HashMap<>();

        // Génération des directives à l'aide des pseudo-opérations '='
        for (Map.Entry<Integer, ParsedObject> inst : parsedObjects.entrySet()) {
            if (inst.getValue() instanceof ParsedInstruction parsedInstruction) {
                ParsedDirectivePack pack = parsedInstruction.convertValueToDirective(stateContainer);
                if (!pack.isEmpty()) {
                    temp.put(off, pack.close());
                    off++;
                }
            }
        }

        parsedObjects.putAll(temp);

        // Application des directives générée
        for (Map.Entry<Integer, ParsedObject> inst : parsedObjects.entrySet()) {
            try {
                if (inst.getValue() instanceof ParsedDirective parsedDirective) {
                    if (parsedDirective.isGenerated()) {
                        pos = parsedDirective.apply(stateContainer, pos);
                    }
                } else if (inst.getValue() instanceof ParsedDirectivePack parsedDirectivePack) {
                    if (parsedDirectivePack.isGenerated()) {
                        pos = parsedDirectivePack.apply(stateContainer, pos);
                    }
                }
            } catch (SyntaxASMException e) {
                throw e.with(inst.getKey());
            }
        }
    }

    /**
     * Enregistre les labels dans le conteur d'états
     */
    public void registerLabels() {
        for (Map.Entry<Integer, ParsedObject> inst : parsedObjects.entrySet()) {
            if (inst.getValue() instanceof ParsedLabel label) {
                label.register(stateContainer);
            }
        }

        stateContainer.labels.put("_END", getLineByte(getLastLine()) + 4);
    }

    /**
     * Avance la lecture sur la prochaine ligne d'assembleur
     *
     * @return la prochaine ligne
     */
    public int nextLine() {
        currentLine = getNextLine();
        return currentLine;
    }

    /**
     * Retourne la prochaine ligne sans faire avancer l'exécution
     *
     * @return la prochaine ligne
     */
    public int getNextLine() {
        if (!hasNextLine()) {
            logger.info("Unable to find next line: program marked as having reached the end");
            atTheEnd = true;
            return currentLine;
        } else return getNextLineR(currentLine + 1);
    }

    private int getNextLineR(int c) {
        if (!parsedObjects.containsKey(c) || !(parsedObjects.get(c) instanceof ParsedInstruction))
            return getNextLineR(c + 1);
        else return c;
    }

    public int getLastExecutedLine() {
        return lastExecutedLine;
    }

    /**
     * Execute le code se trouvant sur la ligne courante
     */
    public synchronized void executeCurrentLine() {
        jumped = false;

        // Remise à zéro des drapeaux de ligne des parseurs
        AddressParser.reset(this.stateContainer);

        int oldPC = getCurrentLineFromPC();

        if (parsedObjects.containsKey(currentLine)) {
            ParsedObject parsedObject = parsedObjects.get(currentLine);

            if (parsedObject instanceof ParsedInstruction instruction) {
                try {
                    instruction.execute(stateContainer);
                } catch (StuckExecutionASMException exception) {
                    this.atTheEnd = true;
                    jumped = true;
                    return;
                }
                this.lastExecuted = instruction;
                this.lastExecutedLine = currentLine;
            }
        } else {
            logger.log(Level.SEVERE, "Executing non-existant instruction of line " + currentLine);
        }

        int newPC = getCurrentLineFromPC();

        if (oldPC != newPC) {
            setCurrentLineFromPC();
            jumped = true;
        } else {
            nextLineToPC();
        }

        this.atTheEnd = !hasNextLine();
    }

    /**
     * Réinitialise l'état actuel du simulateur
     */
    public void resetState(int stackAddress, int symbolsAddress) {
        this.stateContainer = new StateContainer(stackAddress, symbolsAddress);
        applyDirectives();
        registerLabels();
    }

    /**
     * Revient à la première ligne
     */
    public void restart() {
        currentLine = 0;
        lastLine = getLastLine();
        this.atTheEnd = false;
        lastExecutedLine = -1;

        if (stateContainer.getGlobal() != null) {
            try {
                stateContainer.registers[RegisterUtils.PC.getN()].setData(stateContainer.labels.get(stateContainer.getGlobal()));
            } catch (Exception e) {
                stateContainer.registers[RegisterUtils.PC.getN()].setData(0);
            }
            setCurrentLineFromPC();
        }
    }

    /**
     * Vérifie que la ligne suivante existe.
     *
     * @return vrai si il y a une ligne, faux sinon
     */
    public boolean hasNextLine() {
        return lastLine > currentLine && !atTheEnd;
    }

    /**
     * @return le nombre de lignes
     */
    public int getLineCount() {
        return parsedObjects.size();
    }

    /**
     * @return le nombre de lignes
     */
    public int getInstructionCount() {
        if (parsedObjects == null) return 0;

        int rtn = 0;
        for (ParsedObject parsedObject : parsedObjects.values()) {
            if (parsedObject instanceof ParsedInstruction) rtn++;
        }
        return rtn;
    }

    /**
     * @return le numéro de la dernière ligne
     */
    public int getLastLine() {
        final int[] line = {0};
        parsedObjects.keySet().forEach(i -> line[0] = Math.max(parsedObjects.get(i) instanceof ParsedInstruction ? i : 0, line[0]));
        return line[0];
    }

    /**
     * @return vrai si la dernière ligne exécutée était la dernière
     */
    public boolean isAtTheEnd() {
        return atTheEnd;
    }

    /**
     * @return le numéro de la ligne courante
     */
    public int getCurrentLine() {
        return currentLine;
    }

    private int getLineByte(int line) {
        int pos = -1;
        for (int i = 0; i < instructionPositions.size(); i++) {
            if (instructionPositions.get(i) == line) {
                pos = i;
                break;
            }
        }
        if (pos == -1) logger.severe("Unable to found current byte (current line is " + currentLine + ")");
        return RegisterUtils.lineToPC(pos);
    }

    private int getCurrentLineFromPC() {
        int pos = RegisterUtils.PCToLine(stateContainer.registers[RegisterUtils.PC.getN()].getData());
        try {
            return instructionPositions.get(pos) - 1;
        } catch (IndexOutOfBoundsException exception) {
            logger.info("Unable to fetch current line from PC, end of file is considered reached");
            return getLastLine() + 1;
        }
    }

    protected void setCurrentByteToPC() {
        stateContainer.registers[RegisterUtils.PC.getN()].setData(getLineByte(currentLine));
    }

    private void nextLineToPC() {
        stateContainer.registers[RegisterUtils.PC.getN()].add(4);
    }

    protected void setCurrentLineFromPC() {
        currentLine = getCurrentLineFromPC();
    }

    /**
     * Calcul la position des instructions dans la mémoire du programme (pour utiliser avec PC)
     *
     * @return une liste des positions
     */
    private ArrayList<Integer> computeInstructionsPositions() {
        ArrayList<Integer> rtn = new ArrayList<>();

        for (Map.Entry<Integer, ParsedObject> entry : parsedObjects.entrySet()) {
            if (entry.getValue() instanceof ParsedInstruction) {
                rtn.add(entry.getKey());
            }
        }

        return rtn;
    }

    public ParsedInstruction getLastExecuted() {
        return lastExecuted;
    }

    public boolean hasJumped() {
        return jumped;
    }
}
