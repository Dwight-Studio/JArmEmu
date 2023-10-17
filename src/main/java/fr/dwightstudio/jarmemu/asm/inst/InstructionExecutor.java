package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.StateContainer;

public interface InstructionExecutor {

    public void execute(StateContainer stateContainer, int arg1, int arg2, int arg3, int arg4);

}
