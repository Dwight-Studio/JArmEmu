package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.ShiftParser;
import fr.dwightstudio.jarmemu.util.MathUtils;

public class RSBExecutor implements InstructionExecutor<Register, Register, Integer, ShiftParser.ShiftFunction> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Register arg2, Integer arg3, ShiftParser.ShiftFunction arg4) {
        int i1 = arg4.apply(arg3);

        arg1.setData(i1 - arg2.getData()); // arg1 = (arg4 SHIFT arg3) - arg2

        if (updateFlags){
            stateContainer.cpsr.setN(arg1.getData() < 0);
            stateContainer.cpsr.setZ(arg1.getData() == 0);
            stateContainer.cpsr.setC(MathUtils.hasCarry(i1, -arg2.getData()));
            stateContainer.cpsr.setV(MathUtils.hasOverflow(i1, -arg2.getData()));
        }
    }
}
