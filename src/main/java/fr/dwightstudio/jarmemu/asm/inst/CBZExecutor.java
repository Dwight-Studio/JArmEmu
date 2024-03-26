package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class CBZExecutor implements InstructionExecutor<Register, Integer, Object, Object>{
    @Override
    public void execute(StateContainer stateContainer, boolean forceExecution, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Integer arg2, Object arg3, Object arg4) {
        int value = arg1.getData();
        if (value == 0) {
            stateContainer.getPC().setData(arg2);
        }
    }
}
