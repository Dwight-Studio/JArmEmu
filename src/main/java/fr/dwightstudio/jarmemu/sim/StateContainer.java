package fr.dwightstudio.jarmemu.sim;

import java.util.HashMap;

public class StateContainer {

    // ASM
    public final HashMap<String, Integer> symbols; // HashMap des symbols càd des labels et des variables ASM (du .data)

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
        symbols = new HashMap<>();

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
        for (int i = 0 ; i < REGISTER_NUMBER ; i ++) {
            registers[i] = new Register();
        }
    }

    public void clearMemory() {
        for (int i = 0 ; i < MEMORY_SIZE ; i ++) {
            memory[i] = (byte) 0;
        }
    }
}
