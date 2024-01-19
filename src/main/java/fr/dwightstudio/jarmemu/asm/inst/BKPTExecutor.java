package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.exceptions.BreakpointASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class BKPTExecutor implements InstructionExecutor<Integer, Object, Object, Object>{
    @Override
    public void execute(StateContainer stateContainer, boolean forceExecution, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Integer arg1, Object arg2, Object arg3, Object arg4) {
        throw new BreakpointASMException(arg1);
    }
}
