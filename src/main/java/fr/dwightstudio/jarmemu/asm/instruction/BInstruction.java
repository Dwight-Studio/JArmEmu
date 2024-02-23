package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.argument.LabelArgument;
import fr.dwightstudio.jarmemu.asm.argument.NullArgument;
import fr.dwightstudio.jarmemu.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.StuckExecutionASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class BInstruction extends ParsedInstruction<Integer, Object, Object, Object> {
    public BInstruction(boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws BadArgumentASMException {
        super(updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    @Override
    protected Class<? extends ParsedArgument<Integer>> getParsedArg0Class() {
        return LabelArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Object>> getParsedArg1Class() {
        return NullArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Object>> getParsedArg2Class() {
        return NullArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Object>> getParsedArg3Class() {
        return NullArgument.class;
    }

    @Override
    public boolean doModifyPC() {
        return true;
    }

    @Override
    public boolean hasWorkingRegister() {
        return false;
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean forceExecution, Integer arg1, Object arg2, Object arg3, Object arg4) throws ASMException {
        if (arg1 == stateContainer.getPC().getData()) throw new StuckExecutionASMException();
        stateContainer.getPC().setData(arg1); // PC = arg1
    }
}
