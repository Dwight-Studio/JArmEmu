package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.args.ShiftParser;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import fr.dwightstudio.jarmemu.util.MathUtils;

public class ADCExecutor implements InstructionExecutor<Register, Register, Integer, ShiftParser.ShiftFunction> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Register arg2, Integer arg3, ShiftParser.ShiftFunction arg4) {
        //TODO: Faire l'instruction ADC
        int carry = stateContainer.cpsr.getC() ? 1 : 0;
        int i1 = arg4.apply(arg3) + carry;

        arg1.setData(arg2.getData() + i1); // arg1 = arg2 + (arg4 SHIFT arg3)

        updateFlags(stateContainer, updateFlags, arg1, arg2, i1);
    }

}
