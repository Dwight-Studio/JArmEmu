package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.Condition;
import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.argument.ImmediateArgument;
import fr.dwightstudio.jarmemu.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.asm.argument.RegisterArgument;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class BFIInstruction extends ParsedInstruction<Register, Register, Integer, Integer> {
    public BFIInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws BadArgumentASMException {
        super(condition, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    @Override
    protected Class<? extends ParsedArgument<Register>> getParsedArg1Class() {
        return RegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Register>> getParsedArg2Class() {
        return RegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Integer>> getParsedArg3Class() {
        return ImmediateArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Integer>> getParsedArg4Class() {
        return ImmediateArgument.class;
    }

    @Override
    public boolean doModifyPC() {
        return false;
    }

    @Override
    public boolean hasWorkingRegister() {
        return false;
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean forceExecution, Register arg1, Register arg2, Integer arg3, Integer arg4) throws ASMException {
        if (arg3 < 0 || arg3 > 31) throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.instruction.firstArgBF"));
        if (arg4 < 1 || arg4 > (32 - arg3)) throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.instruction.secondArgBF"));
        if (arg4 == 32) {
            arg1.setData(arg2.getData());
        } else {
            int valueToInsert = arg2.getData() & ((1 << arg4) - 1);
            arg1.setData((arg1.getData() & ~(((1 << arg4) -1) << arg3)) | (valueToInsert << arg3));
        }
    }
}
