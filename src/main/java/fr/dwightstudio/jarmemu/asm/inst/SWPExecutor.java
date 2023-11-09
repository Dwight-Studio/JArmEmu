package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class SWPExecutor implements InstructionExecutor<Register, Register, Integer, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Register arg2, Integer arg3, Object arg4) {
        throw new SyntaxASMException("SWP instruction is deprecated");
    }
}
