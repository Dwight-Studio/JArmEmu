package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class MLSExecutor implements InstructionExecutor<Register, Register, Register, Register> {
    @Override
    public void execute(StateContainer stateContainer, boolean forceExecution, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Register arg2, Register arg3, Register arg4) {
        arg1.setData(arg4.getData() - arg3.getData() * arg2.getData()); // arg1 = arg4 - (arg3 * arg2)
    }
}