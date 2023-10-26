package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.asm.args.AddressParser;
import fr.dwightstudio.jarmemu.asm.args.RegisterWithUpdateParser;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CodeInterpreter {
    private final JArmEmuApplication application;
    protected StateContainer stateContainer;
    protected HashMap<Integer, ParsedInstruction> instructions;
    protected int currentLine;

    public CodeInterpreter(JArmEmuApplication application) {
        this.application = application;
        this.currentLine = -1;
    }

    /**
     * Charge des instructions parsés dans l'executeur
     * @param instructions les instructions parsés
     */
    public void load(HashMap<Integer, ParsedInstruction> instructions) {
        this.instructions = instructions;
        currentLine = 0;
    }

    /**
     * Verifie toutes les instructions
     * @return les erreurs si il y en a
     */
    public AssemblyError[] verifyAll() {
        ArrayList<AssemblyError> rtn = new ArrayList<>();

        for (Map.Entry<Integer, ParsedInstruction> inst : instructions.entrySet()) {
            AssemblyError e = inst.getValue().verify(inst.getKey());
        }

        return rtn.toArray(AssemblyError[]::new);
    }

    /**
     * Avance la lecture sur la prochaine ligne d'assembleur
     *
     * @return la prochaine ligne
     */
    public int nextLine() {
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
        this.currentLine = 0;
    }
}
