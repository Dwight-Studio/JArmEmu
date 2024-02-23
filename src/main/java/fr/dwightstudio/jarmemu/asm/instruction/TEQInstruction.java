package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.argument.*;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class TEQInstruction extends ParsedInstruction<Register, Integer, ShiftArgument.ShiftFunction, Object> {
    public TEQInstruction(boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws BadArgumentASMException {
        super(updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    @Override
    protected Class<? extends ParsedArgument<Register>> getParsedArg0Class() {
        return RegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Integer>> getParsedArg1Class() {
        return RotatedImmediateOrRegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<ShiftArgument.ShiftFunction>> getParsedArg2Class() {
        return ShiftArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Object>> getParsedArg3Class() {
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
    protected void execute(StateContainer stateContainer, boolean forceExecution, Register arg1, Integer arg2, ShiftArgument.ShiftFunction arg3, Object arg4) throws ASMException {
        int i1 = arg3.apply(arg2);

        int result = arg1.getData() ^ i1; // result = arg1 ^ (arg3 SHIFT arg2)

        stateContainer.getCPSR().setN(result < 0);
        stateContainer.getCPSR().setZ(result == 0);
    }
}
