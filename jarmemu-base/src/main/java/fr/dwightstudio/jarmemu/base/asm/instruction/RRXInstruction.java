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
import fr.dwightstudio.jarmemu.base.asm.argument.OptionalRegister;
import fr.dwightstudio.jarmemu.base.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.RegisterArgument;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import fr.dwightstudio.jarmemu.base.asm.modifier.ModifierParameter;
import fr.dwightstudio.jarmemu.base.asm.modifier.UpdateFlags;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.base.util.RegisterUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class RRXInstruction extends ParsedInstruction<Register, Register, Object, Object> {
    public RRXInstruction(Modifier modifier, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(modifier, arg1, arg2, arg3, arg4);
    }

    public RRXInstruction(Modifier modifier, ParsedArgument<Register> arg1, ParsedArgument<Register> arg2, ParsedArgument<Object> arg3, ParsedArgument<Object> arg4) {
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
    public Class<? extends ParsedArgument<Object>> getParsedArg3Class() {
        return NullArgument.class;
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
        int cond = this.modifier.condition().getCode();

        int Rd = ((RegisterArgument) this.arg1).getRegisterNumber();
        int Rm = ((RegisterArgument) this.arg2).getRegisterNumber();

        int updateFlags = (this.modifier.doUpdateFlags() ? 1 : 0);
        return (cond << 28) + (0b1101 << 21) + (updateFlags << 20) + (Rd << 12) + (0b11 << 5) + Rm;
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, Register arg1, Register arg2, Object arg3, Object arg4) throws ExecutionASMException {
        int i = Integer.rotateRight(arg2.getData(), 1);
        boolean c = ((i >> 31) & 1) == 1;
        if (stateContainer.getCPSR().getC()) {
            i |= (1 << 31); // set a bit to 1
        } else {
            i &= ~(1 << 31); // set a bit to 0
        }
        arg1.setData(i);

        if (modifier.doUpdateFlags()) {
            stateContainer.getCPSR().setN(arg1.getData() < 0);
            stateContainer.getCPSR().setZ(arg1.getData() == 0);
            stateContainer.getCPSR().setC(c);
        }
    }

    @Override
    protected void verify(StateContainer stateContainer, Register arg1, Register arg2, Object arg3, Object arg4) {

    }
}
