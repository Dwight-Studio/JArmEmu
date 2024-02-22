/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2024 Dwight Studio
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.dwightstudio.jarmemu.asm;

import fr.dwightstudio.jarmemu.oasm.inst.InstructionExecutor;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParser;

import java.util.logging.Level;
import java.util.logging.Logger;

import static fr.dwightstudio.jarmemu.oasm.inst.InstructionExecutors.*;
import static fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParsers.*;

public enum OInstruction {

    // Bitwise logic
    AND(AND_EXECUTOR, false, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT),
    ORR(ORR_EXECUTOR, false, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT),
    EOR(EOR_EXECUTOR, false, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT),
    BIC(BIC_EXECUTOR, false, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT),
    BFC(BFC_EXECUTOR, false, false, REGISTER, IMM, IMM, NULL),
    BFI(BFI_EXECUTOR, false, false, REGISTER, REGISTER, IMM, IMM),

    //Shifter
    LSL(LSL_EXECUTOR, false, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, NULL),
    LSR(LSR_EXECUTOR, false, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, NULL),
    ASR(ASR_EXECUTOR, false, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, NULL),
    ROR(ROR_EXECUTOR, false, true, REGISTER, REGISTER, ROTATED_IMM_OR_REGISTER, NULL),
    RRX(RRX_EXECUTOR, false, false, REGISTER, REGISTER, NULL, NULL),

    // Comparison
    CMP(CMP_EXECUTOR, false, false, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT, NULL),
    CMN(CMN_EXECUTOR, false, false, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT, NULL),
    TST(TST_EXECUTOR, false, false, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT, NULL),
    TEQ(TEQ_EXECUTOR, false, false, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT, NULL),

    // Data movement
    MOV(MOV_EXECUTOR, false, false, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT, NULL),
    MVN(MVN_EXECUTOR, false, false, REGISTER, ROTATED_IMM_OR_REGISTER, SHIFT, NULL),

    // Memory access
    ADR(ADR_EXECUTOR, false, false, REGISTER, LABEL, NULL, NULL),
    LDR(LDR_EXECUTOR, false, false, REGISTER, ADDRESS, IMM_OR_REGISTER, SHIFT),
    STR(STR_EXECUTOR, false, false, REGISTER, ADDRESS, IMM_OR_REGISTER, SHIFT),
    LDM(LDM_EXECUTOR, false, false, REGISTER_WITH_UPDATE, REGISTER_ARRAY, NULL, NULL),
    STM(STM_EXECUTOR, false, false, REGISTER_WITH_UPDATE, REGISTER_ARRAY, NULL, NULL),
    SWP(SWP_EXECUTOR, false, false, REGISTER, REGISTER, REGISTER_ADDRESS, NULL),

    // Branching
    B(B_EXECUTOR, true, false, LABEL, NULL, NULL, NULL),
    BL(BL_EXECUTOR, true, false, LABEL, NULL, NULL, NULL),
    BLX(BLX_EXECUTOR, true, false, REGISTER, NULL, NULL, NULL),
    BX(BX_EXECUTOR, true, false, REGISTER, NULL, NULL, NULL),
    SWI(SWI_EXECUTOR, false, false, CODE, NULL, NULL, NULL),

    // Others
    BKPT(BKPT_EXECUTOR, false, false, IMM_OR_REGISTER, NULL, NULL, NULL);

    private final ArgumentParser[] args;
    private final InstructionExecutor executor;
    private final boolean domReg;
    private final boolean modifyPC;
    private final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * Crée d'une entrée d'instruction
     *
     * @param executor L'exécuteur de l'instruction
     * @param modifyPC Drapeau qui indique si une modification du PC va être effectuée
     * @param domReg   Drapeau qui indique si le premier argument peut-être omis
     * @param arg1     L'analyseur pour le premier argument
     * @param arg2     L'analyseur pour le deuxième argument
     * @param arg3     L'analyseur pour le troisième argument
     * @param arg4     L'analyseur pour le quatrième argument
     */
    <A,B,C,D> OInstruction(InstructionExecutor<A,B,C,D> executor,
                           boolean modifyPC,
                           boolean domReg,
                           ArgumentParser<A> arg1,
                           ArgumentParser<B> arg2,
                           ArgumentParser<C> arg3,
                           ArgumentParser<D> arg4) {
        this.args = new ArgumentParser<?>[] {arg1, arg2, arg3, arg4};
        this.executor = executor;
        this.domReg = domReg;
        this.modifyPC = modifyPC;
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
     * @param forceExecution ignore les erreurs d'exécution non bloquantes
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
                                    boolean forceExecution,
                                    Condition condition,
                                    boolean updateFlags,
                                    DataMode dataMode,
                                    UpdateMode updateMode,
                                    A arg1, B arg2, C arg3, D arg4) {

        if (condition.eval(stateContainer)) {
            executor.execute(stateContainer, forceExecution, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
        } else if (this.doModifyPC()) {
            stateContainer.getPC().add(4);
        }
    }

    public boolean hasDomReg() {
        return domReg;
    }

    public boolean doModifyPC() {
        return modifyPC;
    }
}
