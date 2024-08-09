package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.argument.RegisterArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.RotatedImmediateOrRegisterArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.Shift;
import fr.dwightstudio.jarmemu.base.asm.argument.ShiftArgument;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.RegisterOrImmediate;
import fr.dwightstudio.jarmemu.base.sim.entity.ShiftFunction;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;

public class InstructionCodeUtils {

    public static int getDataProcessingCode(StateContainer stateContainer, ParsedInstruction<Register, Register, RegisterOrImmediate, ShiftFunction> parsedInstruction, int opcode) {
        int cond = parsedInstruction.modifier.condition().getCode();

        int isImmediateOp = 0;
        int Rn = 0;
        int Rd = 0;
        int Op2 = 0;
        try {
            if (parsedInstruction.arg3.getValue(stateContainer).isRegister()) {
                // Op2 is a register
                Op2 = ((RotatedImmediateOrRegisterArgument) parsedInstruction.arg3).getRegisterNumber();
                if (((ShiftArgument) parsedInstruction.arg4).getType() != null) {
                    Op2 += ((ShiftArgument) parsedInstruction.arg4).getType().getCode() << 5;
                    if (((ShiftArgument) parsedInstruction.arg4).getArgument().isRegister()) {
                        Op2 += 1 << 4;
                        if (((ShiftArgument) parsedInstruction.arg4).getType() != Shift.RRX) {
                            Op2 += ((ShiftArgument) parsedInstruction.arg4).getArgument().getRegisterNumber() << 8;
                        }
                    } else {
                        if (((ShiftArgument) parsedInstruction.arg4).getType() != Shift.RRX) {
                            Op2 += ((ShiftArgument) parsedInstruction.arg4).getArgument().getValue(stateContainer).intValue() << 7;
                        }
                    }
                }
            } else {
                // Op2 is an immediate
                isImmediateOp = 1;
                Op2 = ((((RotatedImmediateOrRegisterArgument) parsedInstruction.arg3).getRotationValue() / 2) << 8) + ((RotatedImmediateOrRegisterArgument) parsedInstruction.arg3).getOriginalValue();
            }
            Rn = ((RegisterArgument) parsedInstruction.arg2).getRegisterNumber();
            Rd = ((RegisterArgument) parsedInstruction.arg1).getRegisterNumber();
        } catch (ExecutionASMException ignored) {}

        int updateFlags = parsedInstruction.modifier.doUpdateFlags() ? 1 : 0;

        return (cond << 28) + (isImmediateOp << 25) + (opcode << 21) + (updateFlags << 20) + (Rn << 16) + (Rd << 12) + Op2;
    }

    public static int getDataProcessingCodeAlternative(StateContainer stateContainer, ParsedInstruction<Register, RegisterOrImmediate, ShiftFunction, Object> parsedInstruction, int opcode, int updateFlag, boolean isRn) {
        int cond = parsedInstruction.modifier.condition().getCode();

        int isImmediateOp = 0;
        int R = 0;
        int Op2 = 0;
        try {
            if (parsedInstruction.arg2.getValue(stateContainer).isRegister()) {
                // Op2 is a register
                Op2 = ((RotatedImmediateOrRegisterArgument) parsedInstruction.arg2).getRegisterNumber();
                if (((ShiftArgument) parsedInstruction.arg3).getType() != null) {
                    Op2 += ((ShiftArgument) parsedInstruction.arg3).getType().getCode() << 5;
                    if (((ShiftArgument) parsedInstruction.arg3).getArgument().isRegister()) {
                        Op2 += 1 << 4;
                        if (((ShiftArgument) parsedInstruction.arg3).getType() != Shift.RRX) {
                            Op2 += ((ShiftArgument) parsedInstruction.arg3).getArgument().getRegisterNumber() << 8;
                        }
                    } else {
                        if (((ShiftArgument) parsedInstruction.arg3).getType() != Shift.RRX) {
                            Op2 += ((ShiftArgument) parsedInstruction.arg3).getArgument().getValue(stateContainer).intValue() << 7;
                        }
                    }
                }
            } else {
                // Op2 is an immediate
                isImmediateOp = 1;
                Op2 = ((((RotatedImmediateOrRegisterArgument) parsedInstruction.arg2).getRotationValue() / 2) << 8) + ((RotatedImmediateOrRegisterArgument) parsedInstruction.arg2).getOriginalValue();
            }
            R = ((RegisterArgument) parsedInstruction.arg1).getRegisterNumber();
        } catch (ExecutionASMException ignored) {}

        int updateFlags = (parsedInstruction.modifier.doUpdateFlags() ? 1 : 0) | updateFlag;
        int shifting = isRn ? 16 : 12;
        return (cond << 28) + (isImmediateOp << 25) + (opcode << 21) + (updateFlags << 20) + (R << shifting) + Op2;
    }
}
