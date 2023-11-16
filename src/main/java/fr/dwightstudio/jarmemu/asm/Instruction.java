package fr.dwightstudio.jarmemu.asm;

import fr.dwightstudio.jarmemu.asm.inst.InstructionExecutor;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParser;

import java.util.logging.Level;
import java.util.logging.Logger;

import static fr.dwightstudio.jarmemu.asm.inst.InstructionExecutors.*;
import static fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParsers.*;

public enum Instruction {

    // Arithmetic
    ADD(ADD_EXECUTOR, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT),
    SUB(SUB_EXECUTOR, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT),
    RSB(RSB_EXECUTOR, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT),
    ADC(ADC_EXECUTOR, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT),
    SBC(SBC_EXECUTOR, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT),
    RSC(RSC_EXECUTOR, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT),
    MUL(MUL_EXECUTOR, true, REGISTER, REGISTER, REGISTER, NULL),
    MLA(MLA_EXECUTOR, false, REGISTER, REGISTER, REGISTER, REGISTER),
    MLS(MLS_EXECUTOR, false, REGISTER, REGISTER, REGISTER, REGISTER),
    UMULL(UMULL_EXECUTOR, false, REGISTER, REGISTER, REGISTER, REGISTER),
    UMLAL(UMLAL_EXECUTOR, false, REGISTER, REGISTER, REGISTER, REGISTER),
    SMULL(SMULL_EXECUTOR, false, REGISTER, REGISTER, REGISTER, REGISTER),
    SMLAL(SMLAL_EXECUTOR, false, REGISTER, REGISTER, REGISTER, REGISTER),

    // Bitwise logic
    AND(AND_EXECUTOR, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT),
    ORR(ORR_EXECUTOR, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT),
    EOR(EOR_EXECUTOR, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT),
    BIC(BIC_EXECUTOR, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT),

    //Shifter
    LSL(LSL_EXECUTOR, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, NULL),
    LSR(LSR_EXECUTOR, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, NULL),
    ASR(ASR_EXECUTOR, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, NULL),
    ROR(ROR_EXECUTOR, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, NULL),
    RRX(RRX_EXECUTOR, false, REGISTER, REGISTER, NULL, NULL),

    // Comparison
    CMP(CMP_EXECUTOR, false, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT, NULL),
    CMN(CMN_EXECUTOR, false, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT, NULL),
    TST(TST_EXECUTOR, false, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT, NULL),
    TEQ(TEQ_EXECUTOR, false, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT, NULL),

    // Data movement
    MOV(MOV_EXECUTOR, false, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT, NULL),
    MVN(MVN_EXECUTOR, false, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT, NULL),

    // Memory access
    LDR(LDR_EXECUTOR, false, REGISTER, ADDRESS, IMM_OR_REGISTER, SHIFT),
    STR(STR_EXECUTOR, false, REGISTER, ADDRESS, IMM_OR_REGISTER, SHIFT),
    LDM(LDM_EXECUTOR, false, REGISTER_WITH_UPDATE, REGISTER_ARRAY, NULL, NULL),
    STM(STM_EXECUTOR, false, REGISTER_WITH_UPDATE, REGISTER_ARRAY, NULL, NULL),
    SWP(SWP_EXECUTOR, false, REGISTER, REGISTER, REGISTER_ADDRESS, NULL),

    // Branching
    B(B_EXECUTOR, false, LABEL, NULL, NULL, NULL),
    BL(BL_EXECUTOR, false, LABEL, NULL, NULL, NULL),
    BX(BX_EXECUTOR, false, REGISTER, NULL, NULL, NULL),
    SWI(SWI_EXECUTOR, false, CODE, NULL, NULL, NULL);

    private final ArgumentParser[] args;
    private final InstructionExecutor executor;
    private final boolean domReg;
    private final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * Crée d'une entrée d'instruction
     *
     * @param executor  L'exécuteur de l'instruction
     * @param domReg Drapeau qui indique si le premier argument peut-être omis
     * @param arg1      L'analyseur pour le premier argument
     * @param arg2      L'analyseur pour le deuxième argument
     * @param arg3      L'analyseur pour le troisième argument
     * @param arg4      L'analyseur pour le quatrième argument
     */
    <A,B,C,D> Instruction(InstructionExecutor<A,B,C,D> executor,
                          boolean domReg, ArgumentParser<A> arg1,
                          ArgumentParser<B> arg2,
                          ArgumentParser<C> arg3,
                          ArgumentParser<D> arg4) {
        this.args = new ArgumentParser<?>[] {arg1, arg2, arg3, arg4};
        this.executor = executor;
        this.domReg = domReg;
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

    public boolean hasDomReg() {
        return domReg;
    }
}
