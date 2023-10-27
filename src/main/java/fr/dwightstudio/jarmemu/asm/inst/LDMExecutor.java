package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.args.RegisterWithUpdateParser;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class LDMExecutor implements InstructionExecutor<RegisterWithUpdateParser.UpdatableRegister, Register[], Object, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, RegisterWithUpdateParser.UpdatableRegister arg1, Register[] arg2, Object arg3, Object arg4) {
        //TODO: Faire l'instruction LDM
        throw new IllegalStateException("Instruction LDM not implemented");
    }
}
