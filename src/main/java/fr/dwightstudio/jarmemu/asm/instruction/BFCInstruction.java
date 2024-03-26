package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.Condition;
import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.argument.ImmediateArgument;
import fr.dwightstudio.jarmemu.asm.argument.NullArgument;
import fr.dwightstudio.jarmemu.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.asm.argument.RegisterArgument;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class BFCInstruction extends ParsedInstruction<Register, Integer, Integer, Object> {
    public BFCInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws BadArgumentASMException {
        super(condition, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    @Override
    protected Class<? extends ParsedArgument<Register>> getParsedArg1Class() {
        return RegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Integer>> getParsedArg2Class() {
        return ImmediateArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Integer>> getParsedArg3Class() {
        return ImmediateArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Object>> getParsedArg4Class() {
        return NullArgument.class;
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
    protected void execute(StateContainer stateContainer, boolean forceExecution, Register arg1, Integer arg2, Integer arg3, Object arg4) throws ASMException {
        if (arg2 < 0 || arg2 > 31) throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.instruction.firstArgBF"));
        if (arg3 < 1 || arg3 > (32 - arg2)) throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.instruction.secondArgBF"));
        if (arg3 == 32) {
            arg1.setData(0);
        } else {
            arg1.setData((arg1.getData()) & ~(((1 << arg3) - 1) << arg2));
        }
    }
}
