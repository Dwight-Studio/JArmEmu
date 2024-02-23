package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.argument.NullArgument;
import fr.dwightstudio.jarmemu.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.asm.argument.RegisterArrayArgument;
import fr.dwightstudio.jarmemu.asm.argument.RegisterWithUpdateArgument;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.MemoryAccessMisalignedASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class LDMInstruction extends ParsedInstruction<RegisterWithUpdateArgument.UpdatableRegister, Register[], Object, Object> {
    public LDMInstruction(boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws BadArgumentASMException {
        super(updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    @Override
    protected Class<? extends ParsedArgument<RegisterWithUpdateArgument.UpdatableRegister>> getParsedArg0Class() {
        return RegisterWithUpdateArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Register[]>> getParsedArg1Class() {
        return RegisterArrayArgument.class;
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
        return false;
    }

    @Override
    public boolean hasWorkingRegister() {
        return false;
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean forceExecution, RegisterWithUpdateArgument.UpdatableRegister arg1, Register[] arg2, Object arg3, Object arg4) throws ASMException {
        int length = arg2.length;
        int value = 0;
        int address = arg1.getData();

        if (!forceExecution) {
            int dataLength = 4;
            if (address % dataLength != 0) throw new MemoryAccessMisalignedASMException();
        }

        switch (updateMode) {
            case FD, DB -> {
                for (int i = 0; i < length; i++) {
                    arg2[i].setData(stateContainer.getMemory().getWord(arg1.getData() + 4 * i));
                }
                value = 4 * length;
            }
            case FA, IB -> {
                for (int i = 0; i < length; i++) {
                    arg2[i].setData(stateContainer.getMemory().getWord(arg1.getData() - 4 * i));
                }
                value = - 4 * length;
            }
            case ED, DA -> {
                for (int i = 0; i < length; i++) {
                    arg2[i].setData(stateContainer.getMemory().getWord(arg1.getData() + 4 * (i + 1)));
                }
                value = 4 * length;
            }
            case EA, IA -> {
                for (int i = 0; i < length; i++) {
                    arg2[i].setData(stateContainer.getMemory().getWord(arg1.getData() - 4 * (i + 1)));
                }
                value = - 4 * length;
            }
        }
        arg1.update(value);
    }
}
