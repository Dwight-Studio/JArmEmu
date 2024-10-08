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

import fr.dwightstudio.jarmemu.base.asm.argument.ImmediateArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.RegisterArgument;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import fr.dwightstudio.jarmemu.base.asm.modifier.ModifierParameter;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BFIInstruction extends ParsedInstruction<Register, Register, Integer, Integer> {
    public BFIInstruction(Modifier modifier, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(modifier, arg1, arg2, arg3, arg4);
    }

    public BFIInstruction(Modifier modifier, ParsedArgument<Register> arg1, ParsedArgument<Register> arg2, ParsedArgument<Integer> arg3, ParsedArgument<Integer> arg4) {
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
    public Class<? extends ParsedArgument<Integer>> getParsedArg3Class() {
        return ImmediateArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<Integer>> getParsedArg4Class() {
        return ImmediateArgument.class;
    }

    @Override
    public @NotNull Set<Class<? extends Enum<? extends ModifierParameter>>> getModifierParameterClasses() {
        return Set.of(Condition.class);
    }

    @Override
    public boolean doModifyPC() {
        return false;
    }

    @Override
    public boolean isWorkingRegisterCompatible() {
        return false;
    }

    @Override
    public int getMemoryCode(StateContainer stateContainer, int pos) {
        int cond = this.modifier.condition().getCode();

        int Rd = ((RegisterArgument) this.arg1).getRegisterNumber();
        int Rn = ((RegisterArgument) this.arg2).getRegisterNumber();
        int lsb = ((ImmediateArgument) this.arg3).getValue(stateContainer);
        int width = ((ImmediateArgument) this.arg4).getValue(stateContainer);
        int msb = lsb + width - 1;

        return (cond << 28) + (0b11111 << 22) + (msb << 16) + (Rd << 12) + (lsb << 7) + (1 << 4) + Rn;
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, Register arg1, Register arg2, Integer arg3, Integer arg4) throws ExecutionASMException {
        if (arg4 == 32) {
            arg1.setData(arg2.getData());
        } else {
            int valueToInsert = arg2.getData() & ((1 << arg4) - 1);
            arg1.setData((arg1.getData() & ~(((1 << arg4) -1) << arg3)) | (valueToInsert << arg3));
        }
    }

    @Override
    protected void verify(StateContainer stateContainer, Register arg1, Register arg2, Integer arg3, Integer arg4) throws ASMException {
        if (arg3 < 0 || arg3 > 31) throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.instruction.firstArgBF"));
        if (arg4 < 1 || arg4 > (32 - arg3)) throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.instruction.secondArgBF"));
    }
}
