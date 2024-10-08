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

import fr.dwightstudio.jarmemu.base.asm.argument.*;
import fr.dwightstudio.jarmemu.base.asm.directive.WordDirective;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.DataMode;
import fr.dwightstudio.jarmemu.base.asm.modifier.UpdateMode;
import fr.dwightstudio.jarmemu.base.sim.entity.*;

public class InstructionCodeUtils {

    public static int getDataProcessingCode(StateContainer stateContainer, ParsedInstruction<Register, Register, RegisterOrImmediate, ShiftFunction> parsedInstruction, int opcode) {
        int cond = parsedInstruction.modifier.condition().getCode();

        int isImmediateOp = 0;
        int Rn = ((RegisterArgument) parsedInstruction.arg2).getRegisterNumber();
        int Rd = ((RegisterArgument) parsedInstruction.arg1).getRegisterNumber();
        int Op2 = 0;
        try {
            if (parsedInstruction.arg3.getValue(stateContainer).isRegister()) {
                // Op2 is a register
                Op2 = ((RotatedImmediateOrRegisterArgument) parsedInstruction.arg3).getRegisterNumber();
                if (((ShiftArgument) parsedInstruction.arg4).getType() != null) {
                    Op2 += ((ShiftArgument) parsedInstruction.arg4).getType().getCode() << 5;
                    if (((ShiftArgument) parsedInstruction.arg4).getType() != Shift.RRX) {
                        if (((ShiftArgument) parsedInstruction.arg4).getArgument().isRegister()) {
                            Op2 += 1 << 4;
                            Op2 += ((ShiftArgument) parsedInstruction.arg4).getArgument().getRegisterNumber() << 8;
                        } else {
                            Op2 += ((ShiftArgument) parsedInstruction.arg4).getArgument().getValue(stateContainer).intValue() << 7;
                        }
                    }
                }
            } else {
                // Op2 is an immediate
                isImmediateOp = 1;
                Op2 = ((((RotatedImmediateOrRegisterArgument) parsedInstruction.arg3).getRotationValue() / 2) << 8) + ((RotatedImmediateOrRegisterArgument) parsedInstruction.arg3).getOriginalValue();
            }
        } catch (ExecutionASMException ignored) {}

        int updateFlags = parsedInstruction.modifier.doUpdateFlags() ? 1 : 0;

        return (cond << 28) + (isImmediateOp << 25) + (opcode << 21) + (updateFlags << 20) + (Rn << 16) + (Rd << 12) + Op2;
    }

    public static int getDataProcessingCodeAlternative(StateContainer stateContainer, ParsedInstruction<Register, RegisterOrImmediate, ShiftFunction, Object> parsedInstruction, int opcode, int updateFlag, boolean isRn) {
        int cond = parsedInstruction.modifier.condition().getCode();

        int isImmediateOp = 0;
        int R = ((RegisterArgument) parsedInstruction.arg1).getRegisterNumber();
        int Op2 = 0;
        try {
            if (parsedInstruction.arg2.getValue(stateContainer).isRegister()) {
                // Op2 is a register
                Op2 = ((OptionalRegister) parsedInstruction.arg2).getRegisterNumber();
                if (((ShiftArgument) parsedInstruction.arg3).getType() != null) {
                    Op2 += ((ShiftArgument) parsedInstruction.arg3).getType().getCode() << 5;
                    if (((ShiftArgument) parsedInstruction.arg3).getType() != Shift.RRX) {
                        if (((ShiftArgument) parsedInstruction.arg3).getArgument().isRegister()) {
                            Op2 += 1 << 4;
                                Op2 += ((ShiftArgument) parsedInstruction.arg3).getArgument().getRegisterNumber() << 8;
                        } else {
                            Op2 += ((ShiftArgument) parsedInstruction.arg3).getArgument().getValue(stateContainer).intValue() << 7;
                        }
                    }

                }
            } else {
                // Op2 is an immediate
                isImmediateOp = 1;
                Op2 = ((((RotatedImmediateOrRegisterArgument) parsedInstruction.arg2).getRotationValue() / 2) << 8) + ((RotatedImmediateOrRegisterArgument) parsedInstruction.arg2).getOriginalValue();
            }
        } catch (ExecutionASMException ignored) {}

        int updateFlags = (parsedInstruction.modifier.doUpdateFlags() ? 1 : 0) | updateFlag;
        int shifting = isRn ? 16 : 12;
        return (cond << 28) + (isImmediateOp << 25) + (opcode << 21) + (updateFlags << 20) + (R << shifting) + Op2;
    }

    public static int getShift(StateContainer stateContainer, ParsedInstruction<Register, Register, RegisterOrImmediate, Object> parsedInstruction, Shift shift) {
        int cond = parsedInstruction.modifier.condition().getCode();

        int Rd = ((RegisterArgument) parsedInstruction.arg1).getRegisterNumber();
        int Rn = ((RegisterArgument) parsedInstruction.arg2).getRegisterNumber();
        int Op2 = 0;
        Op2 += shift.getCode() << 5;
        if (shift != Shift.RRX) {
            if (((ImmediateOrRegisterArgument) parsedInstruction.arg3).isRegister()) {
                Op2 += 1 << 4;
                Op2 += ((ImmediateOrRegisterArgument) parsedInstruction.arg3).getRegisterNumber() << 8;
            } else {
                Op2 += ((ImmediateOrRegisterArgument) parsedInstruction.arg3).getValue(stateContainer).intValue() << 7;
            }
        }

        int updateFlags = (parsedInstruction.modifier.doUpdateFlags() ? 1 : 0);
        return (cond << 28) + (0b1101 << 21) + (updateFlags << 20) + (Rd << 12) + Op2 + Rn;
    }

    public static int getMultiplyLong(ParsedInstruction<Register, Register, Register, Register> parsedInstruction, boolean doAccumulate, boolean isSigned) {
        int cond = parsedInstruction.modifier.condition().getCode();

        int RdLo = ((RegisterArgument) parsedInstruction.arg1).getRegisterNumber();
        int RdHi = ((RegisterArgument) parsedInstruction.arg2).getRegisterNumber();
        int Rn = ((RegisterArgument) parsedInstruction.arg3).getRegisterNumber();
        int Rm = ((RegisterArgument) parsedInstruction.arg4).getRegisterNumber();

        int signed = isSigned ? 1 : 0;
        int accumulate = doAccumulate ? 1 : 0;
        int updateFlags = parsedInstruction.modifier.doUpdateFlags() ? 1 : 0;

        return (cond << 28) + (1 << 23) + (signed << 22) + (accumulate << 21) + (updateFlags << 20) + (RdHi << 16) + (RdLo << 12) + (Rm << 8) + (0b1001 << 4) + Rn;
    }

    public static int getBranch(ParsedInstruction<Integer, Object, Object, Object> parsedInstruction, boolean doLink) {
        int cond = parsedInstruction.modifier.condition().getCode();
        int link = doLink ? 1 : 0;
        int offset = ((LabelArgument) parsedInstruction.arg1).getValue() / 4 - 2;
        offset &= 0xFFFFFF;

        return (cond << 28) + (0b101 << 25) + (link << 24) + offset;
    }

    public static int singleMemoryAccess(StateContainer stateContainer, ParsedInstruction<Register, AddressArgument.UpdatableInteger, RegisterOrImmediate, ShiftFunction> parsedInstruction, boolean isStr, WordDirective dir, int pos) {
        int cond = parsedInstruction.modifier.condition().getCode();

        int I = 0;
        int P = 0;
        int U = 0;
        int B = (parsedInstruction.modifier.dataMode() == DataMode.B) ? 1 : 0;
        int W = ((AddressArgument) parsedInstruction.arg2).doesUpdateNow() ? 1 : 0;
        int L = isStr ? 0 : 1;
        int Rn = 0;
        int Rd = ((RegisterArgument) parsedInstruction.arg1).getRegisterNumber();
        int Offset = 0;
        int Shift = 0;

        switch (((AddressArgument) parsedInstruction.arg2).getMode()) {
            case SIMPLE_REGISTER -> {
                Rn = ((AddressArgument) parsedInstruction.arg2).getAddressRegisterArgument().getRegisterNumber();
                U = 1;
                if (parsedInstruction.arg3.getOriginalString() != null) {
                    try {
                        if (parsedInstruction.arg3.getValue(stateContainer).isRegister()) {
                            I = 1;
                            Offset = ((PostOffsetArgument) parsedInstruction.arg3).getRegisterArgument().getRegisterNumber();
                            U = ((PostOffsetArgument) parsedInstruction.arg3).isNegative() ? 0 : 1;
                        } else {
                            Offset = parsedInstruction.arg3.getValue(stateContainer).intValue();
                            U = (Offset < 0) ? 0 : 1;
                        }
                        if (U == 0) Offset = Math.abs(Offset);
                    } catch (ExecutionASMException ignored) {}
                } else {
                    P = 1;
                }
                if (parsedInstruction.arg4.getOriginalString() != null) {
                    Shift = ((ShiftArgument) parsedInstruction.arg4).getType().getCode() + ((((ShiftArgument) parsedInstruction.arg4).getArgument().getValue(stateContainer).intValue() & 0x1F) << 2);
                }
            }
            case IMMEDIATE_OFFSET -> {
                Rn = ((AddressArgument) parsedInstruction.arg2).getAddressRegisterArgument().getRegisterNumber();
                P = 1;
                Offset = ((AddressArgument) parsedInstruction.arg2).getOffsetImmediateArgument().getValue(stateContainer);
                U = (Offset < 0) ? 0 : 1;
                if (U == 0) Offset = Math.abs(Offset);
            }
            case REGISTER_OFFSET -> {
                I = 1;
                Rn = ((AddressArgument) parsedInstruction.arg2).getAddressRegisterArgument().getRegisterNumber();
                P = 1;
                Offset = ((AddressArgument) parsedInstruction.arg2).getOffsetRegisterArgument().getRegisterNumber();
                U = ((AddressArgument) parsedInstruction.arg2).isNegative() ? 0 : 1;
                if (U == 0) Offset = Math.abs(Offset);
            }
            case SHIFTED_REGISTER_OFFSET -> {
                I = 1;
                Rn = ((AddressArgument) parsedInstruction.arg2).getAddressRegisterArgument().getRegisterNumber();
                P = 1;
                Offset = ((AddressArgument) parsedInstruction.arg2).getOffsetRegisterArgument().getRegisterNumber();
                U = ((AddressArgument) parsedInstruction.arg2).isNegative() ? 0 : 1;
                if (U == 0) Offset = Math.abs(Offset) & 0xF;
                Offset += ((AddressArgument) parsedInstruction.arg2).getShiftArgument().getType().getCode() << 5;
                Offset += ((AddressArgument) parsedInstruction.arg2).getShiftArgument().getArgument().getValue(stateContainer).intValue() << 7;
            }
            case PSEUDO_INSTRUCTION -> {
                P = 1;
                U = 1;
                Rn = 0b1111;
                Offset = (dir.getLastPos().getPos() - 8) - pos;
            }
        }



        return (cond << 28) + (1 << 26) + (I << 25) + (P << 24) + (U << 23) + (B << 22) + (W << 21) + (L << 20) + (Rn << 16) + (Rd << 12) + ((Shift & 0x7F) << 5) + (Offset & 0xFFF);
    }

    public static int singleMemoryAccessSHB(StateContainer stateContainer, ParsedInstruction<Register, AddressArgument.UpdatableInteger, RegisterOrImmediate, ShiftFunction> parsedInstruction, boolean isStr) {
        int cond = parsedInstruction.modifier.condition().getCode();

        int P = 0;
        int U = 0;
        int I = 0;
        int W = ((AddressArgument) parsedInstruction.arg2).doesUpdateNow() ? 1 : 0;
        int L = isStr ? 0 : 1;
        int Rn = 0;
        int Rd = ((RegisterArgument) parsedInstruction.arg1).getRegisterNumber();
        int S = parsedInstruction.modifier.doUpdateFlags() ? 1 : 0;
        int H = parsedInstruction.modifier.dataMode() == DataMode.H ? 1 : 0;
        int Rm = 0;
        int Offset = 0;

        switch (((AddressArgument) parsedInstruction.arg2).getMode()) {
            case SIMPLE_REGISTER -> {
                I = 1;
                Rn = ((AddressArgument) parsedInstruction.arg2).getAddressRegisterArgument().getRegisterNumber();
                U = 1;
                if (parsedInstruction.arg3.getOriginalString() != null) {
                    try {
                        if (parsedInstruction.arg3.getValue(stateContainer).isRegister()) {
                            Offset = ((PostOffsetArgument) parsedInstruction.arg3).getRegisterArgument().getRegisterNumber();
                            U = ((PostOffsetArgument) parsedInstruction.arg3).isNegative() ? 0 : 1;
                        } else {
                            Offset = parsedInstruction.arg3.getValue(stateContainer).intValue();
                            U = (Offset < 0) ? 0 : 1;
                        }
                        if (U == 0) Offset = Math.abs(Offset);
                    } catch (ExecutionASMException ignored) {}
                } else {
                    P = 1;
                }
            }
            case IMMEDIATE_OFFSET -> {
                I = 1;
                Rn = ((AddressArgument) parsedInstruction.arg2).getAddressRegisterArgument().getRegisterNumber();
                P = 1;
                try {
                    Offset = parsedInstruction.arg2.getValue(stateContainer).toInt();
                    U = (Offset < 0) ? 0 : 1;
                    if (U == 0) Offset = Math.abs(Offset);
                } catch (ASMException ignored) {}
            }
            case REGISTER_OFFSET -> {
                Rn = ((AddressArgument) parsedInstruction.arg2).getAddressRegisterArgument().getRegisterNumber();
                P = 1;
                Offset = ((AddressArgument) parsedInstruction.arg2).getOffsetRegisterArgument().getRegisterNumber();
                U = ((AddressArgument) parsedInstruction.arg2).isNegative() ? 0 : 1;
                if (U == 0) Offset = Math.abs(Offset);
            }
        }

        return (cond << 28) + (P << 24) + (U << 23) + (I << 22) + (W << 21) + (L << 20) + (Rn << 16) + (Rd << 12) + ((Offset >> 4) << 8)+ (1 << 7) + (S << 6) + (H << 5) + (1 << 4) + (Offset & 0xF) + Rm;
    }

    public static int blockDataTransfer(ParsedInstruction<UpdatableRegister, Register[], Object, Object> parsedInstruction, boolean doLoad) {
        int cond = parsedInstruction.modifier.condition().getCode();

        int P = 0, U;
        if (parsedInstruction.modifier.updateMode() == null) {
            U = 1;
        } else {
            if (!doLoad) {
                P = (parsedInstruction.modifier.updateMode() == UpdateMode.DB) || (UpdateMode.IB == parsedInstruction.modifier.updateMode()) || (parsedInstruction.modifier.updateMode() == UpdateMode.FD) || (UpdateMode.FA == parsedInstruction.modifier.updateMode()) ? 1 : 0;
                U = (parsedInstruction.modifier.updateMode() == UpdateMode.IA) || (UpdateMode.IB == parsedInstruction.modifier.updateMode()) || (parsedInstruction.modifier.updateMode() == UpdateMode.FA) || (UpdateMode.EA == parsedInstruction.modifier.updateMode()) ? 1 : 0;
            } else {
                P = (parsedInstruction.modifier.updateMode() == UpdateMode.ED) || (parsedInstruction.modifier.updateMode() == UpdateMode.EA) || (parsedInstruction.modifier.updateMode() == UpdateMode.DB) || (parsedInstruction.modifier.updateMode() == UpdateMode.IB) ? 1 : 0;
                U = (parsedInstruction.modifier.updateMode() == UpdateMode.FD) || (parsedInstruction.modifier.updateMode() == UpdateMode.ED) || (parsedInstruction.modifier.updateMode() == UpdateMode.IA) || (parsedInstruction.modifier.updateMode() == UpdateMode.IB) ? 1 : 0;
            }
        }
        int W = ((RegisterWithUpdateArgument) parsedInstruction.arg1).doesUpdate() ? 1 : 0;
        int L = doLoad ? 1 : 0;
        int Rn = ((RegisterWithUpdateArgument) parsedInstruction.arg1).getRegisterNumber();
        int regList = 0;
        for (RegisterArgument reg:((RegisterArrayArgument) parsedInstruction.arg2).getArguments()) {
            regList += 1 << reg.getRegisterNumber();
        }

        return (cond << 28) + (1 << 27) + (P << 24) + (U << 23) + (W << 21) + (L << 20) + (Rn << 16) + regList;
    }
}
