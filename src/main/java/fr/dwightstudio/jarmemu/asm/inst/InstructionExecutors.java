package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.parse.args.AddressParser;
import fr.dwightstudio.jarmemu.sim.parse.args.RegisterWithUpdateParser;
import fr.dwightstudio.jarmemu.sim.parse.args.ShiftParser;

public class InstructionExecutors {

    // Arithmetic
    public static final InstructionExecutor<Register, Register, Integer, ShiftParser.ShiftFunction> ADD_EXECUTOR = new ADDExecutor();
    public static final InstructionExecutor<Register, Register, Integer, ShiftParser.ShiftFunction> SUB_EXECUTOR = new SUBExecutor();
    public static final InstructionExecutor<Register, Register, Integer, ShiftParser.ShiftFunction> RSB_EXECUTOR = new RSBExecutor();
    public static final InstructionExecutor<Register, Register, Integer, ShiftParser.ShiftFunction> ADC_EXECUTOR = new ADCExecutor();
    public static final InstructionExecutor<Register, Register, Integer, ShiftParser.ShiftFunction> SBC_EXECUTOR = new SBCExecutor();
    public static final InstructionExecutor<Register, Register, Integer, ShiftParser.ShiftFunction> RSC_EXECUTOR = new RSCExecutor();
    public static final InstructionExecutor<Register, Register, Register, Object> MUL_EXECUTOR = new MULExecutor();
    public static final InstructionExecutor<Register, Register, Register, Register> MLA_EXECUTOR = new MLAExecutor();
    public static final InstructionExecutor<Register, Register, Register, Register> MLS_EXECUTOR = new MLSExecutor();
    public static final InstructionExecutor<Register, Register, Register, Register> UMULL_EXECUTOR = new UMULLExecutor();
    public static final InstructionExecutor<Register, Register, Register, Register> UMLAL_EXECUTOR = new UMLALExecutor();
    public static final InstructionExecutor<Register, Register, Register, Register> SMULL_EXECUTOR = new SMULLExecutor();
    public static final InstructionExecutor<Register, Register, Register, Register> SMLAL_EXECUTOR = new SMLALExecutor();

    // Bitwise logic
    public static final InstructionExecutor<Register, Register, Integer, ShiftParser.ShiftFunction> AND_EXECUTOR = new ANDExecutor();
    public static final InstructionExecutor<Register, Register, Integer, ShiftParser.ShiftFunction> ORR_EXECUTOR = new ORRExecutor();
    public static final InstructionExecutor<Register, Register, Integer, ShiftParser.ShiftFunction> EOR_EXECUTOR = new EORExecutor();
    public static final InstructionExecutor<Register, Register, Integer, ShiftParser.ShiftFunction> BIC_EXECUTOR = new BICExecutor();

    //Shifter
    public static final InstructionExecutor<Register, Register, Integer, Object> LSL_EXECUTOR = new LSLExecutor();
    public static final InstructionExecutor<Register, Register, Integer, Object> LSR_EXECUTOR = new LSRExecutor();
    public static final InstructionExecutor<Register, Register, Integer, Object> ASR_EXECUTOR = new ASRExecutor();
    public static final InstructionExecutor<Register, Register, Integer, Object> ROR_EXECUTOR = new RORExecutor();
    public static final InstructionExecutor<Register, Register, Object, Object> RRX_EXECUTOR = new RRXExecutor();

    // Comparison
    public static final InstructionExecutor<Register, Integer, ShiftParser.ShiftFunction, Object> CMP_EXECUTOR = new CMPExecutor();
    public static final InstructionExecutor<Register, Integer, ShiftParser.ShiftFunction, Object> CMN_EXECUTOR = new CMNExecutor();
    public static final InstructionExecutor<Register, Integer, ShiftParser.ShiftFunction, Object> TST_EXECUTOR = new TSTExecutor();
    public static final InstructionExecutor<Register, Integer, ShiftParser.ShiftFunction, Object> TEQ_EXECUTOR = new TEQExecutor();

    // Data movement
    public static final InstructionExecutor<Register, Integer, ShiftParser.ShiftFunction, Object> MOV_EXECUTOR = new MOVExecutor();
    public static final InstructionExecutor<Register, Integer, ShiftParser.ShiftFunction, Object> MVN_EXECUTOR = new MVNExecutor();

    // Memory access
    public static final InstructionExecutor<Register, AddressParser.UpdatableInteger, Integer, ShiftParser.ShiftFunction> LDR_EXECUTOR = new LDRExecutor();
    public static final InstructionExecutor<Register, AddressParser.UpdatableInteger, Integer, ShiftParser.ShiftFunction> STR_EXECUTOR = new STRExecutor();
    public static final InstructionExecutor<RegisterWithUpdateParser.UpdatableRegister, Register[], Object, Object> LDM_EXECUTOR = new LDMExecutor();
    public static final InstructionExecutor<RegisterWithUpdateParser.UpdatableRegister, Register[], Object, Object> STM_EXECUTOR = new STMExecutor();
    public static final InstructionExecutor<Register, Register, Integer, Object> SWP_EXECUTOR = new SWPExecutor();

    // Branching
    public static final InstructionExecutor<Integer, Object, Object, Object> B_EXECUTOR = new BExecutor();
    public static final InstructionExecutor<Integer, Object, Object, Object> BL_EXECUTOR = new BLExecutor();
    public static final InstructionExecutor<Register, Object, Object, Object> BX_EXECUTOR = new BXExecutor();
    public static final InstructionExecutor<Integer, Object, Object, Object> SWI_EXECUTOR = new SWIExecutor();

}
