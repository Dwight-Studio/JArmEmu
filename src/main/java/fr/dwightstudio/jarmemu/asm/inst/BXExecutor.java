package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class BXExecutor implements InstructionExecutor<Register, Object, Object, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Object arg2, Object arg3, Object arg4) {
        stateContainer.registers[15].setData(arg1.getData()); // PC = arg1
        stateContainer.cpsr.setT(arg1.get(0));
    }
}
