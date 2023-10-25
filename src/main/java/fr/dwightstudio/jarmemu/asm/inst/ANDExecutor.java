package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.args.ShiftParser;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;

public class ANDExecutor implements InstructionExecutor<Register, Register, Integer, ShiftParser.ShiftFunction> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Register arg2, Integer arg3, ShiftParser.ShiftFunction arg4) {
        int i1 = arg4.apply(arg3);
        arg1.setData(arg2.getData() & i1);

        if (updateFlags) {
            stateContainer.cpsr.setN(arg1.getData() < 0);
            stateContainer.cpsr.setZ(arg1.getData() == 0);
        }
    }
}
