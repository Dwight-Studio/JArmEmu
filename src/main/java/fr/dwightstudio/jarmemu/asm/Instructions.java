package fr.dwightstudio.jarmemu.asm;

import fr.dwightstudio.jarmemu.asm.args.*;
import fr.dwightstudio.jarmemu.asm.inst.InstructionExecutor;
import fr.dwightstudio.jarmemu.sim.StateContainer;

import static fr.dwightstudio.jarmemu.asm.args.ArgumentParsers.*;
import static fr.dwightstudio.jarmemu.asm.inst.InstructionExecutors.*;

public enum Instructions {

    LDR(LDR_EXECUTOR, REGISTER, ADRRESS, NULL, NULL);

    private final ArgumentParser[] args;
    private final InstructionExecutor executor;

    Instructions(InstructionExecutor executor, ArgumentParser arg1, ArgumentParser arg2, ArgumentParser arg3, ArgumentParser arg4) {
        this.args = new ArgumentParser[] {arg1, arg2, arg3, arg4};
        this.executor = executor;
    }

    public ArgumentParser getArgParser(int i) {
        return args[i];
    }

    public void execute(StateContainer stateContainer, int arg1, int arg2, int arg3, int arg4) {
        executor.execute(stateContainer, arg1, arg2, arg3, arg4);
    }
}
