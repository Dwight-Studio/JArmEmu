package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class BFCExecutor implements InstructionExecutor<Register, Integer, Integer, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean forceExecution, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Integer arg2, Integer arg3, Object arg4) {
        int lsb = arg2;
        if (lsb < 0 || lsb > 31) throw new SyntaxASMException("BFC's first argument must be between 0 and 31 (both included)");
        int msb = lsb + arg3 - 1;
        if (msb < 1 || msb > (32 - lsb)) throw new SyntaxASMException("BFC's second argument must be between 1 and 32 minus the first argument");
        StringBuilder mask = new StringBuilder();
        mask.repeat('1', 32-(msb+1));
        mask.repeat('0', msb-lsb+1);
        mask.repeat('1', lsb);
        arg1.setData((int) (arg1.getData() & Long.parseLong(mask.toString(), 2)));
    }
}
