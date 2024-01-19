package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class BFIExecutor implements InstructionExecutor<Register, Register, Integer, Integer> {
    @Override
    public void execute(StateContainer stateContainer, boolean forceExecution, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Register arg2, Integer arg3, Integer arg4) {
        if (arg3 < 0 || arg3 > 31) throw new SyntaxASMException("BFI's first argument must be between 0 and 31 (both included)");
        if (arg4 < 1 || arg4 > (32 - arg3)) throw new SyntaxASMException("BFI's second argument must be between 1 and 32 minus the first argument");
        if (arg4 == 32) {
            arg1.setData(arg2.getData());
        } else {
            int valueToInsert = arg2.getData() & ((1 << arg4) - 1);
            arg1.setData((arg1.getData() & ~(((1 << arg4) -1) << arg3)) | (valueToInsert << arg3));
        }
    }
}
