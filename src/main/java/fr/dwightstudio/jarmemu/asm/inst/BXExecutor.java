package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;

public class BXExecutor implements InstructionExecutor<Register, Object, Object, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Object arg2, Object arg3, Object arg4) {
        //TODO: Faire l'instruction BX
        throw new IllegalStateException("Instruction BX not implemented");
    }
}
