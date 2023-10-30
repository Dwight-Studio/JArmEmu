package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.asm.Instruction;
import fr.dwightstudio.jarmemu.asm.args.AddressParser;
import fr.dwightstudio.jarmemu.asm.args.RegisterWithUpdateParser;
import fr.dwightstudio.jarmemu.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;
import fr.dwightstudio.jarmemu.sim.parse.ParsedInstruction;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.ParsedLabel;
import fr.dwightstudio.jarmemu.sim.parse.ParsedObject;
import fr.dwightstudio.jarmemu.sim.parse.SourceParser;
import fr.dwightstudio.jarmemu.util.RegisterUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CodeInterpreter {
    private static final Logger logger = Logger.getLogger(CodeInterpreter.class.getName());

    private final JArmEmuApplication application;
    protected StateContainer stateContainer;
    protected HashMap<Integer, ParsedObject> instructions;
    private int currentLine;
    private int lastLine;
    private boolean atTheEnd;

    public CodeInterpreter(JArmEmuApplication application) {
        this.application = application;
        this.currentLine = -1;
        this.atTheEnd = false;
    }

    /**
     * Charge des instructions parsées dans l'exécuteur
     * @param sourceParser le parseur de source utilisé
     */
    public void load(SourceParser sourceParser) {
        this.instructions = sourceParser.parse();
        currentLine = 0;
        lastLine = getLastLine();
        this.atTheEnd = false;
    }

    /**
     * Verifie toutes les ParsedInstructions et ParsedPseudoInstructions
     * @return les erreurs si il y en a
     */
    public AssemblyError[] verifyAll() {
        ArrayList<AssemblyError> rtn = new ArrayList<>();

        for (Map.Entry<Integer, ParsedObject> inst : instructions.entrySet()) {
            AssemblyError e = inst.getValue().verify(inst.getKey(), stateContainer.labels.keySet());
            if (e != null) rtn.add(e);
        }

        return rtn.toArray(AssemblyError[]::new);
    }

    /**
     * Enregistre les labels dans le conteur d'états
     */
    public void registerLabels() {
        for (Map.Entry<Integer, ParsedObject> inst : instructions.entrySet()) {
            if (inst.getValue() instanceof ParsedLabel label) {
                label.register(stateContainer);
            }
        }
    }

    /**
     * Avance la lecture sur la prochaine ligne d'assembleur
     *
     * @return la prochaine ligne
     */
    public int nextLine() {
        if (!hasNextLine()) return lastLine;
        currentLine++;
        if (!instructions.containsKey(currentLine) || !(instructions.get(currentLine) instanceof ParsedInstruction)) nextLine();
        setCurrentByteToPC();
        return currentLine;
    }

    /**
     * Execute le code se trouvant sur la ligne courante
     */
    public synchronized void executeCurrentLine() {
        // Remise à zéro des drapeaux de ligne des parseurs
        AddressParser.reset(this.stateContainer);
        RegisterWithUpdateParser.reset(this.stateContainer);

        int oldPC = getCurrentLineFromPC();

        if (instructions.containsKey(currentLine)) {
            ParsedObject parsedObject = instructions.get(currentLine);

            if (parsedObject instanceof ParsedInstruction instruction) {
                instruction.execute(stateContainer);
            }
        } else {
            logger.log(Level.SEVERE, "Executing non-existant instruction of line " + currentLine);
        }

        int newPC = getCurrentLineFromPC();

        if (oldPC != newPC) setCurrentLineFromPC();

        this.atTheEnd = !hasNextLine();
    }

    /**
     * Réinitialise l'état actuel du simulateur
     */
    public void resetState() {
        this.stateContainer = new StateContainer();
        registerLabels();
    }

    /**
     * Revient à la première ligne
     */
    public void restart() {
        this.currentLine = -1;
    }

    /**
     * Vérifie que la ligne suivante existe.
     * @return vrai si il y a une ligne, faux sinon
     */
    public boolean hasNextLine() {
        return lastLine > currentLine;
    }

    /**
     * @return le nombre de lignes
     */
    public int getLineCount() {
        return instructions.size();
    }

    /**
     * @return le nombre de lignes
     */
    public int getInstructionCount() {
        int rtn = 0;
        for (ParsedObject parsedObject : instructions.values()) {
            if (parsedObject instanceof ParsedInstruction) rtn++;
        }
        return rtn;
    }

    /**
     * @return le numéro de la dernière ligne
     */
    public int getLastLine() {
        final int[] line = {0};
        instructions.keySet().forEach(i -> line[0] = Math.max(instructions.get(i) instanceof ParsedInstruction ? i : 0, line[0]));
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

    public int getCurrentByte() {
        return RegisterUtils.lineToPC(currentLine);
    }

    private int getCurrentLineFromPC() {
        return RegisterUtils.PCToLine(stateContainer.registers[RegisterUtils.PC.getN()].getData());
    }

    private void setCurrentByteToPC() {
        stateContainer.registers[RegisterUtils.PC.getN()].setData(getCurrentByte());
    }

    private void setCurrentLineFromPC() {
        currentLine = getCurrentLineFromPC();
    }
}
