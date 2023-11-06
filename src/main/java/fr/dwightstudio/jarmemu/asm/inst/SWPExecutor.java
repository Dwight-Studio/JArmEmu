package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class SWPExecutor implements InstructionExecutor<Register, Register, Integer, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Register arg2, Integer arg3, Object arg4) {
        switch (dataMode){
            case null -> {
                arg1.setData(stateContainer.memory.getWord(arg3));
                stateContainer.memory.putWord(arg3, arg2.getData());
            }
            case HALF_WORD -> {
                arg1.setData(stateContainer.memory.getHalf(arg3));
                stateContainer.memory.putHalf(arg3, (short) arg2.getData());
            }
            case BYTE -> {
                arg1.setData(stateContainer.memory.getByte(arg3));
                stateContainer.memory.putByte(arg3, (byte) arg2.getData());
            }
        }
    }
}
