package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.exceptions.StuckExecutionASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class BExecutor implements InstructionExecutor<Integer, Object, Object, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Integer arg1, Object arg2, Object arg3, Object arg4) {
        if (arg1.equals(stateContainer.registers[15].getData())) throw new StuckExecutionASMException();
        stateContainer.registers[15].setData(arg1); // PC = arg1
    }
}
