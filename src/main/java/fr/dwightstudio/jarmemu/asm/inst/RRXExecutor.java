package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class RRXExecutor implements InstructionExecutor<Register, Register, Object, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Register arg2, Object arg3, Object arg4) {
        int i = Integer.rotateRight(arg2.getData(), 1);
        boolean c = ((i >> 31) & 1) == 1;
        if (stateContainer.cpsr.getC()) {
            i |= (1 << 31); // set a bit to 1
        } else {
            i &= ~(1 << 31); // set a bit to 0
        }
        arg1.setData(i);

        if (updateFlags) {
            stateContainer.cpsr.setN(arg1.getData() < 0);
            stateContainer.cpsr.setZ(arg1.getData() == 0);
            stateContainer.cpsr.setC(c);
        }
    }
}
