package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.argument.*;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.MemoryAccessMisalignedASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class LDRInstruction extends ParsedInstruction<Register, AddressArgument.UpdatableInteger, Integer, ShiftArgument.ShiftFunction> {
    public LDRInstruction(boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws BadArgumentASMException {
        super(updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    @Override
    protected Class<? extends ParsedArgument<Register>> getParsedArg0Class() {
        return RegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<AddressArgument.UpdatableInteger>> getParsedArg1Class() {
        return AddressArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Integer>> getParsedArg2Class() {
        return ImmediateOrRegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<ShiftArgument.ShiftFunction>> getParsedArg3Class() {
        return ShiftArgument.class;
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
    protected void execute(StateContainer stateContainer, boolean forceExecution, Register arg1, AddressArgument.UpdatableInteger arg2, Integer arg3, ShiftArgument.ShiftFunction arg4) throws ASMException {
        int i1 = arg4.apply(arg3);
        int address = arg2.toInt() + i1;

        if (!forceExecution) {
            int dataLength;

            switch (dataMode) {
                case HALF_WORD -> dataLength = 2;
                case BYTE -> dataLength = 1;
                case null, default -> dataLength = 4;
            }

            if (address % dataLength != 0) throw new MemoryAccessMisalignedASMException();
        }

        switch (dataMode) {
            case null -> arg1.setData(stateContainer.getMemory().getWord(address));
            case HALF_WORD -> arg1.setData(stateContainer.getMemory().getHalf(address));
            case BYTE -> arg1.setData(stateContainer.getMemory().getByte(address));
        }

        arg2.update();
    }
}
