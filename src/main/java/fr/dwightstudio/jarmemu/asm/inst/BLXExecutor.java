package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.exceptions.StuckExecutionASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class BLXExecutor implements InstructionExecutor<Register, Object, Object, Object>{
    @Override
    public void execute(StateContainer stateContainer, boolean forceExecution, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Object arg2, Object arg3, Object arg4) {
        if (arg1.equals(stateContainer.getPC())) throw new StuckExecutionASMException();
        stateContainer.getLR().setData(stateContainer.getPC().getData() + 4);
        stateContainer.getPC().setData(arg1.getData()); // PC = arg1
        stateContainer.getCPSR().setT(arg1.get(0));
        stateContainer.branch();
    }
}
