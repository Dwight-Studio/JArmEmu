package fr.dwightstudio.jarmemu.sim.obj;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.HashMap;

public class StateContainer {

    // ASM
    public final HashMap<String, Integer> consts; // HashMap des constantes
    public final HashMap<String, Integer> data; // HashMap des données
    public final HashMap<String, Integer> labels; // HashMap des labels
    private int lastSymbolLocation;


    // Registers
    public static final int REGISTER_NUMBER = 16;
    public final Register[] registers;
    public final PSR cpsr;
    public final PSR spsr;

    // Memory
    public static final int MEMORY_CHUNK_NUMBER = 256;
    public static final int MEMORY_CHUNK_SIZE = 4;
    public static final int MEMORY_SIZE = MEMORY_CHUNK_NUMBER * MEMORY_CHUNK_SIZE;
    public final Byte[] memory;

    public StateContainer() {

        // ASM
        labels = new HashMap<>();
        consts = new HashMap<>();
        data = new HashMap<>();
        lastSymbolLocation = 0;

        // Initializing registers
        this.registers = new Register[REGISTER_NUMBER];
        clearRegisters();

        cpsr = new PSR();
        spsr = new PSR();

        // Initializing memory
        this.memory = new Byte[MEMORY_SIZE];
        clearMemory();
    }

    public void clearRegisters() {
        for (int i = 0; i < REGISTER_NUMBER; i++) {
            registers[i] = new Register();
        }
    }

    public void clearMemory() {
        for (int i = 0; i < MEMORY_SIZE; i++) {
            memory[i] = (byte) 0;
        }
    }

    public int eval(String expString, HashMap<String, Integer> map) {
        try {
            Expression exp = new ExpressionBuilder(expString).variables(map.keySet()).build();
            for (String str : map.keySet()) {
                exp.setVariable(str, (double) map.get(str));
            }
            return (int) exp.evaluate();
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException("Malformed math expression '" + expString + "'");
        }
    }

}
