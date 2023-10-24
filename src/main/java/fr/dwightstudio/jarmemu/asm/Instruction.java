package fr.dwightstudio.jarmemu.asm;

import fr.dwightstudio.jarmemu.asm.args.ArgumentParser;
import fr.dwightstudio.jarmemu.asm.inst.InstructionExecutor;
import fr.dwightstudio.jarmemu.sim.StateContainer;

import java.util.logging.Level;
import java.util.logging.Logger;

import static fr.dwightstudio.jarmemu.asm.args.ArgumentParsers.*;
import static fr.dwightstudio.jarmemu.asm.inst.InstructionExecutors.*;

public enum Instruction {

    // Arithmetic
    ADD(ADD_EXECUTOR, REGISTER, REGISTER, VALUE_8_OR_REGISTER, SHIFT),
    SUB(SUB_EXECUTOR, REGISTER, REGISTER, VALUE_8_OR_REGISTER, SHIFT),
    RSB(RSB_EXECUTOR, REGISTER, REGISTER, VALUE_8_OR_REGISTER, SHIFT),
    ADC(ADC_EXECUTOR, REGISTER, REGISTER, VALUE_8_OR_REGISTER, SHIFT),
    SBC(SBC_EXECUTOR, REGISTER, REGISTER, VALUE_8_OR_REGISTER, SHIFT),
    RSC(RSC_EXECUTOR, REGISTER, REGISTER, VALUE_8_OR_REGISTER, SHIFT),
    MUL(MUL_EXECUTOR, REGISTER, REGISTER, REGISTER, NULL),
    MLA(MLA_EXECUTOR, REGISTER, REGISTER, REGISTER, NULL),
    UMULL(UMULL_EXECUTOR, REGISTER, REGISTER, REGISTER, REGISTER),
    UMLAL(UMLAL_EXECUTOR, REGISTER, REGISTER, REGISTER, REGISTER),
    SMULL(SMULL_EXECUTOR, REGISTER, REGISTER, REGISTER, REGISTER),
    SMLAL(SMLAL_EXECUTOR, REGISTER, REGISTER, REGISTER, REGISTER),

    // Bitwise logic
    AND(AND_EXECUTOR, REGISTER, REGISTER, VALUE_8_OR_REGISTER, SHIFT),
    ORR(ORR_EXECUTOR, REGISTER, REGISTER, VALUE_8_OR_REGISTER, SHIFT),
    EOR(EOR_EXECUTOR, REGISTER, REGISTER, VALUE_8_OR_REGISTER, SHIFT),
    BIC(BIC_EXECUTOR, REGISTER, REGISTER, VALUE_8_OR_REGISTER, SHIFT),

    // Comparison
    CMP(CMP_EXECUTOR, REGISTER, VALUE_8_OR_REGISTER, SHIFT, NULL),
    CMN(CMN_EXECUTOR, REGISTER, VALUE_8_OR_REGISTER, SHIFT, NULL),
    TST(TST_EXECUTOR, REGISTER, VALUE_8_OR_REGISTER, SHIFT, NULL),
    TEQ(TEQ_EXECUTOR, REGISTER, VALUE_8_OR_REGISTER, SHIFT, NULL),

    // Data movement
    MOV(MOV_EXECUTOR, REGISTER, VALUE_8_OR_REGISTER, SHIFT, NULL),
    MVN(MVN_EXECUTOR, REGISTER, VALUE_8_OR_REGISTER, SHIFT, NULL),

    // Memory access
    LDR(LDR_EXECUTOR, REGISTER, ADDRESS, VALUE_8_OR_REGISTER, SHIFT),
    STR(STR_EXECUTOR, REGISTER, ADDRESS, VALUE_8_OR_REGISTER, SHIFT),
    LDM(LDM_EXECUTOR, REGISTER_WITH_UPDATE, REGISTER_ARRAY, NULL, NULL),
    STM(STM_EXECUTOR, REGISTER_WITH_UPDATE, REGISTER_ARRAY, NULL, NULL),
    SWP(SWP_EXECUTOR, REGISTER, REGISTER, REGISTER_ADDRESS, NULL),

    // Branching
    B(B_EXECUTOR, LABEL, NULL, NULL, NULL),
    BL(BL_EXECUTOR, LABEL, NULL, NULL, NULL),
    BX(BX_EXECUTOR, REGISTER, NULL, NULL, NULL),
    SWI(SWI_EXECUTOR, LABEL, NULL, NULL, NULL);

    private final ArgumentParser[] args;
    private final InstructionExecutor executor;
    private final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * Crée d'une entrée d'instruction
     * @param executor L'exécuteur de l'instruction
     * @param arg1 L'analyseur pour le premier argument
     * @param arg2 L'analyseur pour le deuxième argument
     * @param arg3 L'analyseur pour le troisième argument
     * @param arg4 L'analyseur pour le quatrième argument
     */
    <A,B,C,D> Instruction(InstructionExecutor<A,B,C,D> executor, ArgumentParser<A> arg1, ArgumentParser<B> arg2, ArgumentParser<C> arg3, ArgumentParser<D> arg4) {
        this.args = new ArgumentParser<?>[] {arg1, arg2, arg3, arg4};
        this.executor = executor;
        logger.log(Level.FINE, "Registering instruction " + this.name()
                + " with " + arg1.getClass().getName()
                + ", " + arg2.getClass().getName()
                + ", " + arg3.getClass().getName()
                + ", " + arg4.getClass().getName());
    }

    public ArgumentParser[] getArgParsers() {
        return args;
    }

    /**
     * Execution de l'instruction sous condition
     * @param stateContainer Le conteneur d'état sur lequel effectuer l'exécution
     * @param condition La condition à verifier
     * @param updateFlags Doit-on mettre à jour les flags
     * @param dataMode Type de donnée (Byte, HalfWord, Word) si applicable
     * @param updateMode Mode de mise à jour
     * @param arg1 Le premier argument
     * @param arg2 Le deuxième argument
     * @param arg3 Le troisième argument
     * @param arg4 Le quatrième argument
     */
    public <A,B,C,D> void execute(StateContainer stateContainer,
                                    Condition condition,
                                    boolean updateFlags,
                                    DataMode dataMode,
                                    UpdateMode updateMode,
                                    A arg1, B arg2, C arg3, D arg4) {

        if (condition.eval(stateContainer)) executor.execute(stateContainer, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }
}
