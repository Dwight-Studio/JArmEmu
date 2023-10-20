package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.args.*;

public class InstructionExecutors {

    // Arithmetic
    public static final InstructionExecutor ADD_EXECUTOR = new ADDExecutor();
    public static final InstructionExecutor SUB_EXECUTOR = new SUBExecutor();
    public static final InstructionExecutor RSB_EXECUTOR = new RSBExecutor();
    public static final InstructionExecutor ADC_EXECUTOR = new ADCExecutor();
    public static final InstructionExecutor SBC_EXECUTOR = new SBCExecutor();
    public static final InstructionExecutor RSC_EXECUTOR = new RSCExecutor();
    public static final InstructionExecutor MUL_EXECUTOR = new MULExecutor();
    public static final InstructionExecutor MLA_EXECUTOR = new MLAExecutor();
    public static final InstructionExecutor UMULL_EXECUTOR = new UMULLExecutor();
    public static final InstructionExecutor UMLAL_EXECUTOR = new UMLALExecutor();
    public static final InstructionExecutor SMULL_EXECUTOR = new SMULLExecutor();
    public static final InstructionExecutor SMLAL_EXECUTOR = new SMLALExecutor();

    // Bitwise logic
    public static final InstructionExecutor AND_EXECUTOR = new ANDExecutor();
    public static final InstructionExecutor ORR_EXECUTOR = new ORRExecutor();
    public static final InstructionExecutor EOR_EXECUTOR = new EORExecutor();
    public static final InstructionExecutor BIC_EXECUTOR = new BICExecutor();

    // Comparison
    public static final InstructionExecutor CMP_EXECUTOR = new CMPExecutor();
    public static final InstructionExecutor CMN_EXECUTOR = new CMNExecutor();
    public static final InstructionExecutor TST_EXECUTOR = new TSTExecutor();
    public static final InstructionExecutor TEQ_EXECUTOR = new TEQExecutor();

    // Data movement
    public static final InstructionExecutor MOV_EXECUTOR = new MOVExecutor();
    public static final InstructionExecutor MVN_EXECUTOR = new MVNExecutor();

    // Memory access
    public static final InstructionExecutor LDR_EXECUTOR = new LDRExecutor();
    public static final InstructionExecutor STR_EXECUTOR = new STRExecutor();
    public static final InstructionExecutor LDM_EXECUTOR = new LDMExecutor();
    public static final InstructionExecutor STM_EXECUTOR = new STMExecutor();
    public static final InstructionExecutor SWP_EXECUTOR = new SWPExecutor();

    // Branching
    public static final InstructionExecutor B_EXECUTOR = new BExecutor();
    public static final InstructionExecutor BL_EXECUTOR = new BLExecutor();
    public static final InstructionExecutor BX_EXECUTOR = new BXExecutor();
    public static final InstructionExecutor SWI_EXECUTOR = new SWIExecutor();

}
