package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;

public class SMULLExecutor implements InstructionExecutor<Register, Register, Register, Register> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Register arg2, Register arg3, Register arg4) {
        //TODO: Faire l'instruction SMULL
        throw new IllegalStateException("Instruction SMULL not implemented");
    }
}
