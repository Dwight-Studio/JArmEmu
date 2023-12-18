package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class ADRExecutor implements InstructionExecutor<Register, Integer, Object, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean forceExecution, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Integer arg2, Object arg3, Object arg4) {
        arg1.setData(arg2);
        if (updateFlags) {
            throw new SyntaxASMException("Can't use S on ADR instruction");
        }
    }
}
