package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class ASRExecutor implements InstructionExecutor<Register, Register, Integer, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean forceExecution, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Register arg2, Integer arg3, Object arg4) {
        int oldValue = arg2.getData() >> (arg3 - 1);
        arg1.setData(arg2.getData() >> arg3);

        if (updateFlags) {
            stateContainer.cpsr.setN(arg1.getData() < 0);
            stateContainer.cpsr.setZ(arg1.getData() == 0);
            stateContainer.cpsr.setC((oldValue & 1) == 1);
        }
    }
}