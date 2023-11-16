package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class UMULLExecutor implements InstructionExecutor<Register, Register, Register, Register> {
    @Override
    public void execute(StateContainer stateContainer, boolean forceExecution, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Register arg2, Register arg3, Register arg4) {
        long r3 = arg3.getData() & 0xFFFFFFFFL;
        long r4 = arg4.getData() & 0xFFFFFFFFL;
        long result = r3 * r4;  // result = (unsigned) arg3 * (unsigned) arg4
        arg1.setData((int) (result));   // arg1 = result[31..0]
        arg2.setData((int) (result >>> 32)); // arg1 = result[63..32]

        if (updateFlags) {
            stateContainer.cpsr.setN(arg2.getData() < 0);
            stateContainer.cpsr.setZ(arg1.getData() == 0 && arg2.getData() == 0);
        }
    }
}
