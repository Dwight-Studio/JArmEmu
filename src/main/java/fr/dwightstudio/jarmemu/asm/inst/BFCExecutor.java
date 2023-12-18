package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class BFCExecutor implements InstructionExecutor<Register, Integer, Integer, Object> {
    //TODO: ajouter plus de tests
    @Override
    public void execute(StateContainer stateContainer, boolean forceExecution, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Integer arg2, Integer arg3, Object arg4) {
        if (arg2 < 0 || arg2 > 31) throw new SyntaxASMException("BFC's first argument must be between 0 and 31 (both included)");
        if (arg3 < 1 || arg3 > (32 - arg2)) throw new SyntaxASMException("BFC's second argument must be between 1 and 32 minus the first argument");
        arg1.setData((arg1.getData()) & ~(((1 << arg3) - 1) << arg2));
    }
}
