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

package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum Instruction {

    // Arithmetic
    ADD(ADDInstruction.class),
    SUB(SUBInstruction.class),
    RSB(RSBInstruction.class),
    ADC(ADCInstruction.class),
    SBC(SBCInstruction.class),
    RSC(RSCInstruction.class),
    MUL(MULInstruction.class),
    MLA(MLAInstruction.class),
    MLS(MLSInstruction.class),
    UMULL(UMULLInstruction.class),
    UMLAL(UMLALInstruction.class),
    SMULL(SMULLInstruction.class),
    SMLAL(SMLALInstruction.class),

    // Bitwise logic
    AND(ANDInstruction.class),
    ORR(ORRInstruction.class),
    EOR(EORInstruction.class),
    BIC(BICInstruction.class),
    BFC(BFCInstruction.class),
    BFI(BFIInstruction.class),
    CLZ(CLZInstruction.class),

    // Shifting
    LSL(LSLInstruction.class),
    LSR(LSRInstruction.class),
    ASR(ASRInstruction.class),
    ROR(RORInstruction.class),
    RRX(RRXInstruction.class),

    // Comparison
    CMP(CMPInstruction.class),
    CMN(CMPInstruction.class),
    TST(TSTInstruction.class),
    TEQ(TEQInstruction.class),
    CBZ(CBZInstruction.class),
    CBNZ(CBNZInstruction.class),

    // Data movement
    MOV(MOVInstruction.class),
    MVN(MVNInstruction.class),
    SWI(SWIInstruction.class),
    BXJ(BXJInstruction.class),

    // Memory access
    ADR(ADRInstruction.class),
    LDR(LDRInstruction.class),
    STR(STRInstruction.class),
    STM(STMInstruction.class),
    LDA(LDAInstruction.class),
    LDAEX(LDAEXInstruction.class),
    LDM(LDMInstruction.class),
    POP(POPInstruction.class),
    PUSH(PUSHInstruction.class),
    SWP(SWPInstruction.class),

    // Branching
    B(BInstruction.class),
    BL(BLInstruction.class),
    BLX(BLXInstruction.class),
    BLXNS(BLXNSInstruction.class),
    BX(BXInstruction.class),
    BXNS(BXNSInstruction.class),

    //Coprocessor-related
    CDP(CDPInstruction.class),
    CDP2(CDP2Instruction.class),
    LDC(LDCInstruction.class),
    LDC2(LDC2Instruction.class),

    // Others
    BKPT(BKPTInstruction.class),
    CLREX(CLREXInstruction.class),
    CRC32(CRC32Instruction.class),
    CRC32C(CRC32CInstruction.class),
    DBG(DBGInstruction.class),
    HLT(HLTInstruction.class),
    NOP(NOPInstruction.class);

    private Constructor<? extends ParsedInstruction<?, ?, ?, ?>> mainConstructor;
    private Constructor<? extends ParsedInstruction<?, ?, ?, ?>> debugConstructor;
    private boolean valid;

    private String arg1Type;
    private String arg2Type;
    private String arg3Type;
    private String arg4Type;
    private boolean workingRegister;

    Instruction(Class<? extends ParsedInstruction<?, ?, ?, ?>> instructionClass) {

        try {
            mainConstructor = instructionClass.getDeclaredConstructor(InstructionModifier.class, String.class, String.class, String.class, String.class);
            debugConstructor = instructionClass.getDeclaredConstructor(InstructionModifier.class, ParsedArgument.class, ParsedArgument.class, ParsedArgument.class, ParsedArgument.class);

            ParsedInstruction<?, ?, ?, ?> instruction = this.create();

            if (instruction != null) {
                arg1Type = instruction.getParsedArg1Class().getSimpleName();
                arg2Type = instruction.getParsedArg2Class().getSimpleName();
                arg3Type = instruction.getParsedArg3Class().getSimpleName();
                arg4Type = instruction.getParsedArg4Class().getSimpleName();
                workingRegister = instruction.hasWorkingRegister();

                valid = true;
            } else {
                valid = false;
            }
        } catch (NoSuchMethodException exception) {
            mainConstructor = null;
            debugConstructor = null;
            valid = false;
        }

        if (!valid) {
            arg1Type = null;
            arg2Type = null;
            arg3Type = null;
            arg4Type = null;
            workingRegister = false;
        }
    }

    public ParsedInstruction<?, ?, ?, ?> create(InstructionModifier modifier, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        try {
            if (valid) return mainConstructor.newInstance(modifier, arg1, arg2, arg3, arg4);
            else return null;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            if (e.getCause() instanceof ASMException ex) throw ex;
            else throw new RuntimeException(e);
        }
    }

    private ParsedInstruction<?, ?, ?, ?> create() {
        try {
            return debugConstructor.newInstance(new InstructionModifier(Condition.AL, false, null, null), null, null, null, null);
        } catch (NullPointerException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {}

        return null;
    }

    public String getArgumentType(int i) {
        if (valid) return switch (i) {
            case 0 -> arg1Type;
            case 1 -> arg2Type;
            case 2 -> arg3Type;
            case 3 -> arg4Type;
            default -> "";
        };
        else return null;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean hasWorkingRegister() {
        return workingRegister;
    }
}
