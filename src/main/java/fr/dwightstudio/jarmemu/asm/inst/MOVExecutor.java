package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.args.ShiftParser;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import fr.dwightstudio.jarmemu.util.MathUtils;

public class MOVExecutor implements InstructionExecutor<Register, Integer, ShiftParser.ShiftFunction, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Integer arg2, ShiftParser.ShiftFunction arg3, Object arg4) {
        int i1 = arg3.apply(arg2);

        arg1.setData(i1);

        if(updateFlags){
            stateContainer.cpsr.setN(arg1.getData() < 0);
            stateContainer.cpsr.setZ(arg1.getData() == 0);
        }
    }
}
