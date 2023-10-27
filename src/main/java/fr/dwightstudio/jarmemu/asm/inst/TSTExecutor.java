package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.args.ShiftParser;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class TSTExecutor implements InstructionExecutor<Register, Integer, ShiftParser.ShiftFunction, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Integer arg2, ShiftParser.ShiftFunction arg3, Object arg4) {
        int i1 = arg3.apply(arg2);
        int result = arg1.getData() & i1; // result = arg1 & (arg3 SHIFT arg2)

        stateContainer.cpsr.setN(result < 0);
        stateContainer.cpsr.setZ(result == 0);
    }
}
