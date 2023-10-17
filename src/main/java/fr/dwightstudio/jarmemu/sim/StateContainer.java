package fr.dwightstudio.jarmemu.sim;

public class StateContainer {

    // Registers
    public static final int REGISTER_NUMBER = 16;
    public final Register[] registers;

    // Memory
    public static final int MEMORY_CHUNK_NUMBER = 256;
    public static final int MEMORY_CHUNK_SIZE = 4;
    public static final int MEMORY_SIZE = MEMORY_CHUNK_NUMBER * MEMORY_CHUNK_SIZE;
    public final Byte[] memory;

    public StateContainer() {

        // Initializing registers
        this.registers = new Register[REGISTER_NUMBER];
        clearRegisters();

        // Initializing memory
        this.memory = new Byte[MEMORY_SIZE];
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
