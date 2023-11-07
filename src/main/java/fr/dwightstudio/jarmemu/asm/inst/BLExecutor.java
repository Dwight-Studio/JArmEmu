package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.exceptions.StuckExecutionASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class BLExecutor implements InstructionExecutor<Integer, Object, Object, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Integer arg1, Object arg2, Object arg3, Object arg4) {
        if (arg1.equals(stateContainer.registers[15].getData())) throw new StuckExecutionASMException();
        stateContainer.registers[14].setData(stateContainer.registers[15].getData() + 4); // LR = PC + 4
        stateContainer.registers[15].setData(arg1); // PC = arg1
    }
}
