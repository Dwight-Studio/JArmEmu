package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.ShiftParser;

public class MOVExecutor implements InstructionExecutor<Register, Integer, ShiftParser.ShiftFunction, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean forceExecution, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Integer arg2, ShiftParser.ShiftFunction arg3, Object arg4) {
        int i1 = arg3.apply(arg2);

        arg1.setData(i1); // arg1 = (arg3 SHIFT arg2)

        if(updateFlags){
            stateContainer.cpsr.setN(arg1.getData() < 0);
            stateContainer.cpsr.setZ(arg1.getData() == 0);
        }
    }
}
