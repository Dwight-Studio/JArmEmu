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

import fr.dwightstudio.jarmemu.base.asm.argument.NullArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.RegisterAddressArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.RegisterArgument;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.DeprecatedASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.DataMode;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import fr.dwightstudio.jarmemu.base.asm.modifier.ModifierParameter;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.base.util.SequencedSetUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.SequencedSet;

public class SWPInstruction extends ParsedInstruction<Register, Register, Integer, Object> {
    public SWPInstruction(Modifier modifier, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(modifier, arg1, arg2, arg3, arg4);
    }

    public SWPInstruction(Modifier modifier, ParsedArgument<Register> arg1, ParsedArgument<Register> arg2, ParsedArgument<Integer> arg3, ParsedArgument<Object> arg4) throws ASMException {
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
        return RegisterAddressArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<Object>> getParsedArg4Class() {
        return NullArgument.class;
    }

    @Override
    @NotNull
    public SequencedSet<Class<? extends Enum<? extends ModifierParameter>>>getModifierParameterClasses() {
        return SequencedSetUtils.of(Condition.class);
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
    public int getMemoryCode(StateContainer stateContainer) {
        int cond = this.modifier.condition().getCode();
        int B = (this.modifier.dataMode() == DataMode.B) ? 1 : 0;
        int Rn = ((RegisterAddressArgument) this.arg3).getRegisterNumber();
        int Rd = ((RegisterArgument) this.arg1).getRegisterNumber();
        int Rm = ((RegisterArgument) this.arg2).getRegisterNumber();
        return (cond << 28) + (0b0001 << 24) + (B << 22) + (Rn << 16) + (Rd << 12) + (0b1001 << 4) + Rm;
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, Register arg1, Register arg2, Integer arg3, Object arg4) throws ExecutionASMException {
        if (modifier.dataMode() == DataMode.B) {
            arg1.setData(stateContainer.getMemory().getByte(arg3));
            stateContainer.getMemory().putByte(arg3, (byte) arg2.getData());
        } else {
            arg1.setData(stateContainer.getMemory().getWord(arg3));
            stateContainer.getMemory().putWord(arg3, arg2.getData());
        }

    }

    @Override
    protected void verify(StateContainer stateContainer, Register arg1, Register arg2, Integer arg3, Object arg4) throws SyntaxASMException {
        if (modifier.dataMode() == DataMode.H) {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.instruction.halfword"));
        }
    }
}
