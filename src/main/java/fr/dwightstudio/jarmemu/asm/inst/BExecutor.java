package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.StateContainer;

public class BExecutor implements InstructionExecutor<Integer, Object, Object, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Integer arg1, Object arg2, Object arg3, Object arg4) {
        //TODO: Faire l'instruction B
        throw new IllegalStateException("Instruction B not implemented");
    }
}
