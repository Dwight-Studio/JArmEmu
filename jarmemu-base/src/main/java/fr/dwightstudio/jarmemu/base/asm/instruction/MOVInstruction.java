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

import fr.dwightstudio.jarmemu.base.asm.Instruction;
import fr.dwightstudio.jarmemu.base.asm.argument.*;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import fr.dwightstudio.jarmemu.base.asm.modifier.ModifierParameter;
import fr.dwightstudio.jarmemu.base.asm.modifier.UpdateFlags;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.RegisterOrImmediate;
import fr.dwightstudio.jarmemu.base.sim.entity.ShiftFunction;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.base.util.RegisterUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Set;


public class MOVInstruction extends ParsedInstruction<Register, RegisterOrImmediate, ShiftFunction, Object> {

    private ParsedInstruction<?, ?, ?, ?> shiftInstruction;

    public MOVInstruction(Modifier modifier, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(modifier, arg1, arg2, arg3, arg4);
    }

    public MOVInstruction(Modifier modifier, ParsedArgument<Register> arg1, ParsedArgument<RegisterOrImmediate> arg2, ParsedArgument<ShiftFunction> arg3, ParsedArgument<Object> arg4) {
        super(modifier, arg1, arg2, arg3, arg4);
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<Register>> getParsedArg1Class() {
        return RegisterArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<RegisterOrImmediate>> getParsedArg2Class() {
        return RotatedImmediateOrRegisterArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<ShiftFunction>> getParsedArg3Class() {
        return ShiftArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<Object>> getParsedArg4Class() {
        return NullArgument.class;
    }

    @Override
    public @NotNull Set<Class<? extends Enum<? extends ModifierParameter>>> getModifierParameterClasses() {
        return Set.of(UpdateFlags.class, Condition.class);
    }

    @Override
    public boolean doModifyPC() {
        return !modifier.doUpdateFlags() && (((OptionalRegister) arg1).getRegisterNumber() == RegisterUtils.PC.getN());
    }

    @Override
    public boolean isWorkingRegisterCompatible() {
        return false;
    }

    @Override
    public int getMemoryCode(StateContainer stateContainer, int pos) {
        return InstructionCodeUtils.getDataProcessingCodeAlternative(stateContainer, this, 0b1101, 0, false);
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, Register arg1, RegisterOrImmediate arg2, ShiftFunction arg3, Object arg4) throws ExecutionASMException {
        int i1 = arg3.apply(arg2);

        arg1.setData(i1); // arg1 = (arg3 SHIFT arg2)

        if(modifier.doUpdateFlags()){
            stateContainer.getCPSR().setN(arg1.getData() < 0);
            stateContainer.getCPSR().setZ(arg1.getData() == 0);
        }
    }

    @Override
    protected void verify(StateContainer stateContainer, Register arg1, RegisterOrImmediate arg2, ShiftFunction arg3, Object arg4) throws SyntaxASMException {
        arg3.check(arg2);
    }

    public boolean isShiftInstruction() {
        return this.arg3.getOriginalString() != null;
    }

    public ParsedInstruction<?, ?, ?, ?> getShiftInstruction() throws ASMException {
        if (shiftInstruction == null) generateShiftInstruction();
        return shiftInstruction;
    }

    private void generateShiftInstruction() throws ASMException {
        String argString = this.arg3.getOriginalString();
        try {
            String nArg1 = arg1.getOriginalString();
            String nArg2 = arg2.getOriginalString();
            String nArg3 = argString.substring(3);
            String nArg4 = arg4.getOriginalString();

            if (nArg1 != null) nArg1 = nArg1.isBlank() ? null : nArg1;
            if (nArg2 != null) nArg2 = nArg2.isBlank() ? null : nArg2;
            if (nArg3 != null) nArg3 = nArg3.isBlank() ? null : nArg3;
            if (nArg4 != null) nArg4 = nArg4.isBlank() ? null : nArg4;

            shiftInstruction = Instruction.valueOf(argString.substring(0, 3).toUpperCase()).create(modifier, nArg1, nArg2, nArg3, nArg4).withLineNumber(this.getLineNumber()).withFile(this.getFile());
        } catch (IllegalArgumentException e) {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.invalidShift", argString));
        }
    }
}
