package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.util.MathUtils;

public class MULExecutor implements InstructionExecutor<Register, Register, Register, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Register arg2, Register arg3, Object arg4) {
        //TODO: Faire l'instruction MUL
        arg1.setData(arg2.getData() * arg3.getData()); // arg1 = arg2 * arg3

        if (updateFlags) {
            stateContainer.cpsr.setN(arg1.getData() < 0);
            stateContainer.cpsr.setZ(arg1.getData() == 0);
            stateContainer.cpsr.setC(false);
            stateContainer.cpsr.setV(MathUtils.hasOverflowMul(arg2.getData(), arg3.getData()));
        }
    }
}
