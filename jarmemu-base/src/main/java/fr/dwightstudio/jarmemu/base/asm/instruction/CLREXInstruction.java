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
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.NotImplementedASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import fr.dwightstudio.jarmemu.base.asm.modifier.ModifierParameter;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class CLREXInstruction extends ParsedInstruction<Object, Object, Object, Object> {
    public CLREXInstruction(Modifier modifier, ParsedArgument<Object> arg1, ParsedArgument<Object> arg2, ParsedArgument<Object> arg3, ParsedArgument<Object> arg4) throws ASMException {
        super(modifier, arg1, arg2, arg3, arg4);
        throw new NotImplementedASMException(JArmEmuApplication.formatMessage("%exception.instruction.notImplemented", "CLREX"));
    }

    public CLREXInstruction(Modifier modifier, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(modifier, arg1, arg2, arg3, arg4);
        throw new NotImplementedASMException(JArmEmuApplication.formatMessage("%exception.instruction.notImplemented", "CLREX"));
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<Object>> getParsedArg1Class() {
        return NullArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<Object>> getParsedArg2Class() {
        return NullArgument.class;
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
        return (0b1111 << 28) + (1 << 26) + (1 << 24) + (0b111 << 20) + (0xFF << 12) + 0b11111;
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, Object arg1, Object arg2, Object arg3, Object arg4) throws ExecutionASMException {

    }

    @Override
    protected void verify(StateContainer stateContainer, Object arg1, Object arg2, Object arg3, Object arg4) throws ASMException {

    }
}
