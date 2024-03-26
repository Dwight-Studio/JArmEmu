package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.Condition;
import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.argument.NullArgument;
import fr.dwightstudio.jarmemu.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.asm.argument.RegisterArgument;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class MULInstruction extends ParsedInstruction<Register, Register, Register, Object> {
    public MULInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws BadArgumentASMException {
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
    protected Class<? extends ParsedArgument<Register>> getParsedArg3Class() {
        return RegisterArgument.class;
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
        return true;
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean forceExecution, Register arg1, Register arg2, Register arg3, Object arg4) throws ExecutionASMException {
        arg1.setData(arg2.getData() * arg3.getData()); // arg1 = arg2 * arg3

        if (updateFlags) {
            stateContainer.getCPSR().setN(arg1.getData() < 0);
            stateContainer.getCPSR().setZ(arg1.getData() == 0);
        }
    }
}
