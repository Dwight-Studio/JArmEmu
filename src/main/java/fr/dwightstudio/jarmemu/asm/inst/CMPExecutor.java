package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;

import java.util.function.Function;

public class CMPExecutor implements InstructionExecutor<Register, Integer, Function<Integer, Integer>, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Integer arg2, Function<Integer, Integer> arg3, Object arg4) {
        //TODO: Faire l'instruction CMP
        throw new IllegalStateException("Instruction CMP not implemented");
    }
}
