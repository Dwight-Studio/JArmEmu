package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class RORExecutor implements InstructionExecutor<Register, Register, Integer, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Register arg2, Integer arg3, Object arg4) {
        arg1.setData(Integer.rotateRight(arg2.getData(), arg3));

        if (updateFlags) {
            stateContainer.cpsr.setN(arg1.getData() < 0);
            stateContainer.cpsr.setZ(arg1.getData() == 0);
            //TODO: Update carry flag
        }
    }
}
