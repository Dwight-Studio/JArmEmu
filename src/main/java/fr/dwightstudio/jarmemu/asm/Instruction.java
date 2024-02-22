package fr.dwightstudio.jarmemu.asm;

import fr.dwightstudio.jarmemu.asm.instruction.*;

public enum Instruction {

    // Arithmetic
    ADD(ADDInstruction.class),
    SUB(SUBInstruction.class),
    RSB(RSBInstruction.class),
    ADC(ADCInstruction.class),
    SBC(SBCInstruction.class),
    RSC(RSCInstruction.class),
    MUL(MULInstruction.class),
    MLA(MLAInstruction.class),
    MLS(MLSInstruction.class),
    UMULL(UMULLInstruction.class),
    UMLAL(UMLALInstruction.class),
    SMULL(SMULLInstruction.class),
    SMLAL(SMLALInstruction.class)

    // Bitwise logic

    // Shifting

    // Comparison

    // Data movement

    // Memory access

    // Branching

    // Others
    ;

    private Class<? extends ParsedInstruction<?, ?, ?, ?>> instructionClass;

    Instruction(Class<? extends ParsedInstruction<?, ?, ?, ?>> instructionClass) {
        this.instructionClass = instructionClass;
    }

    public ParsedInstruction<?, ?, ?, ?> create(boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) {
        try {
            return this.instructionClass.getDeclaredConstructor(Boolean.class, DataMode.class, UpdateMode.class, String.class, String.class, String.class, String.class).newInstance(updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
