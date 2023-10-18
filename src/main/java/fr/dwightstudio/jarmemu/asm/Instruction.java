package fr.dwightstudio.jarmemu.asm;

import fr.dwightstudio.jarmemu.asm.args.*;
import fr.dwightstudio.jarmemu.asm.inst.InstructionExecutor;
import fr.dwightstudio.jarmemu.sim.StateContainer;

import java.util.logging.Level;
import java.util.logging.Logger;

import static fr.dwightstudio.jarmemu.asm.args.ArgumentParsers.*;
import static fr.dwightstudio.jarmemu.asm.inst.InstructionExecutors.*;

public enum Instruction {

    LDR(LDR_EXECUTOR, REGISTER, ADRRESS, NULL, NULL);

    private final ArgumentParser[] args;
    private final InstructionExecutor executor;
    private final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * Création d'une entrée d'instruction
     * @param executor L'exécuteur de l'instruction
     * @param arg1 L'analyseur pour le premier argument
     * @param arg2 L'analyseur pour le deuxième argument
     * @param arg3 L'analyseur pour le troisième argument
     * @param arg4 L'analyseur pour le quatrième argument
     */
    Instruction(InstructionExecutor executor, ArgumentParser arg1, ArgumentParser arg2, ArgumentParser arg3, ArgumentParser arg4) {
        this.args = new ArgumentParser[] {arg1, arg2, arg3, arg4};
        this.executor = executor;
        logger.log(Level.FINE, "Registering instruction " + this.name()
                + " with " + arg1.getClass().getName()
                + ", " + arg2.getClass().getName()
                + ", " + arg3.getClass().getName()
                + ", " + arg4.getClass().getName());
    }

    public ArgumentParser getArgParser(int i) {
        return args[i];
    }

    public InstructionExecutor getExecutor() {
        return executor;
    }
}
