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
    SMLAL(SMLALInstruction.class),

    // Bitwise logic
    AND(ANDInstruction.class),
    ORR(ORRInstruction.class),
    EOR(EORInstruction.class),
    BIC(BICInstruction.class),
    BFC(BFCInstruction.class),
    BFI(BFIInstruction.class),

    // Shifting
    LSL(LSLInstruction.class),
    LSR(LSRInstruction.class),
    ASR(ASRInstruction.class),
    ROR(RORInstruction.class),
    RRX(RRXInstruction.class),

    // Comparison
    CMP(CMPInstruction.class),
    CMN(CMPInstruction.class),
    TST(TSTInstruction.class),
    TEQ(TEQInstruction.class),

    // Data movement
    MOV(MOVInstruction.class),
    MVN(MVNInstruction.class),

    // Memory access
    ADR(ADRInstruction.class),
    LDR(LDRInstruction.class),
    STR(STRInstruction.class),
    LDM(LDMInstruction.class),
    SWP(SWPInstruction.class),

    // Branching
    B(BFIInstruction.class),
    BL(BLInstruction.class),
    BLX(BLInstruction.class),
    BX(BXInstruction.class),


    // Others
    BKPT(BKPTInstruction.class);

    private final Class<? extends ParsedInstruction<?, ?, ?, ?>> instructionClass;

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
