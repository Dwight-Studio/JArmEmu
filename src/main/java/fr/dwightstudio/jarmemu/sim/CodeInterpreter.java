package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.asm.args.AddressParser;
import fr.dwightstudio.jarmemu.asm.args.RegisterWithUpdateParser;
import fr.dwightstudio.jarmemu.JArmEmuApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CodeInterpreter {
    private static final Logger logger = Logger.getLogger(CodeInterpreter.class.getName());

    private final JArmEmuApplication application;
    protected StateContainer stateContainer;
    protected HashMap<Integer, ParsedInstruction> instructions;
    private int currentLine;
    private int lastLine;
    private boolean atTheEnd;

    public CodeInterpreter(JArmEmuApplication application) {
        this.application = application;
        this.currentLine = -1;
        this.atTheEnd = false;
    }

    /**
     * Charge des instructions parsés dans l'exécuteur
     * @param instructions les instructions parsés
     */
    public void load(HashMap<Integer, ParsedInstruction> instructions) {
        this.instructions = instructions;
        currentLine = 0;
        lastLine = getLastLine();
        this.atTheEnd = false;
    }

    /**
     * Verifie toutes les instructions
     * @return les erreurs si il y en a
     */
    public AssemblyError[] verifyAll() {
        ArrayList<AssemblyError> rtn = new ArrayList<>();

        for (Map.Entry<Integer, ParsedInstruction> inst : instructions.entrySet()) {
            AssemblyError e = inst.getValue().verify(inst.getKey());
            if (e != null) rtn.add(e);
        }

        return rtn.toArray(AssemblyError[]::new);
    }

    /**
     * Avance la lecture sur la prochaine ligne d'assembleur
     *
     * @return la prochaine ligne
     */
    public int nextLine() {
        if (!hasNextLine()) return lastLine;
        currentLine++;
        if (!instructions.containsKey(currentLine)) nextLine();
        return currentLine;
    }

    /**
     * Execute le code se trouvant sur la ligne courante
     */
    public synchronized void executeCurrentLine() {
        // Remise à zéro des drapeaux de ligne des parsers
        AddressParser.reset(this.stateContainer);
        RegisterWithUpdateParser.reset(this.stateContainer);

        if (instructions.containsKey(currentLine)) {
            ParsedInstruction instruction = instructions.get(currentLine);
            instruction.execute(stateContainer);
        } else {
            logger.log(Level.SEVERE, "Executing non-existant instruction of line " + currentLine);
        }

        this.atTheEnd = !hasNextLine();
    }

    /**
     * Réinitialise l'état actuel du simulateur
     */
    public void resetState() {
        this.stateContainer = new StateContainer();
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
     * @return le numéro de la dernière ligne
     */
    public int getLastLine() {
        final int[] line = {0};
        instructions.keySet().forEach(i -> line[0] = Math.max(i, line[0]));
        return line[0];
    }

    /**
     * @return vrai si la dernière ligne exécutée était la dernière
     */
    public boolean isAtTheEnd() {
        return atTheEnd;
    }
}
