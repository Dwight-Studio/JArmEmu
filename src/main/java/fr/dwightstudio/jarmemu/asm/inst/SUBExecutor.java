package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;

import java.util.function.Function;

public class SUBExecutor implements InstructionExecutor<Register, Register, Integer, Function<Integer, Integer>> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Register arg2, Integer arg3, Function<Integer, Integer> arg4) {
        //TODO: Faire l'instruction SUB
        throw new IllegalStateException("Instruction SUB not implemented");
    }
}
