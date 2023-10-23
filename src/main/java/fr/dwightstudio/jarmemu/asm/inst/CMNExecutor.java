package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.args.ShiftParser;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;

import java.util.function.Function;

public class CMNExecutor implements InstructionExecutor<Register, Integer, ShiftParser.ShiftFunction, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, Register arg1, Integer arg2, ShiftParser.ShiftFunction arg3, Object arg4) {
        //TODO: Faire l'instruction CMN
        throw new IllegalStateException("Instruction CMN not implemented");
    }
}
