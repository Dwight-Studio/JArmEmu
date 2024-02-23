package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.argument.NullArgument;
import fr.dwightstudio.jarmemu.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.asm.argument.RegisterArgument;
import fr.dwightstudio.jarmemu.asm.argument.RotatedImmediateOrRegisterArgument;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.MemoryAccessMisalignedASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class LSRInstruction extends ParsedInstruction<Register, Register, Integer, Object> {
    public LSRInstruction(boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws BadArgumentASMException {
        super(updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    @Override
    protected Class<? extends ParsedArgument<Register>> getParsedArg0Class() {
        return RegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Register>> getParsedArg1Class() {
        return RegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Integer>> getParsedArg2Class() {
        return RotatedImmediateOrRegisterArgument.class;
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
        return true;
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean forceExecution, Register arg1, Register arg2, Integer arg3, Object arg4) throws ASMException {
        int oldValue = arg2.getData();
        arg1.setData(arg2.getData() >>> arg3);

        if (updateFlags) {
            stateContainer.getCPSR().setN(false);
            stateContainer.getCPSR().setZ(arg1.getData() == 0);
            stateContainer.getCPSR().setC((oldValue & (1 << (arg3 - 1))) != 0);
        }
    }
}
