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
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import fr.dwightstudio.jarmemu.base.asm.modifier.ModifierParameter;
import fr.dwightstudio.jarmemu.base.asm.modifier.UpdateFlags;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.RegisterOrImmediate;
import fr.dwightstudio.jarmemu.base.sim.entity.ShiftFunction;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.base.util.MathUtils;
import fr.dwightstudio.jarmemu.base.util.RegisterUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class RSBInstruction extends ParsedInstruction<Register, Register, RegisterOrImmediate, ShiftFunction> {
    public RSBInstruction(Modifier modifier, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(modifier, arg1, arg2, arg3, arg4);
    }

    public RSBInstruction(Modifier modifier, ParsedArgument<Register> arg1, ParsedArgument<Register> arg2, ParsedArgument<RegisterOrImmediate> arg3, ParsedArgument<ShiftFunction> arg4) {
        super(modifier, arg1, arg2, arg3, arg4);
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<Register>> getParsedArg1Class() {
        return RegisterArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<Register>> getParsedArg2Class() {
        return RegisterArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<RegisterOrImmediate>> getParsedArg3Class() {
        return RotatedImmediateOrRegisterArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<ShiftFunction>> getParsedArg4Class() {
        return ShiftArgument.class;
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
        return true;
    }

    @Override
    public int getMemoryCode(StateContainer stateContainer, int pos) {
        return InstructionCodeUtils.getDataProcessingCode(stateContainer, this, 0b0011);
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, Register arg1, Register arg2, RegisterOrImmediate arg3, ShiftFunction arg4) throws ExecutionASMException {
        int i1 = arg4.apply(arg3);

        arg1.setData(i1 - arg2.getData()); // arg1 = (arg4 SHIFT arg3) - arg2

        if (modifier.doUpdateFlags()){
            stateContainer.getCPSR().setN(arg1.getData() < 0);
            stateContainer.getCPSR().setZ(arg1.getData() == 0);
            stateContainer.getCPSR().setC(MathUtils.hasCarry(i1, -arg2.getData()));
            stateContainer.getCPSR().setV(MathUtils.hasOverflow(i1, -arg2.getData()));
        }
    }

    @Override
    protected void verify(StateContainer stateContainer, Register arg1, Register arg2, RegisterOrImmediate arg3, ShiftFunction arg4) throws SyntaxASMException {
        arg4.check(arg3);
    }
}
