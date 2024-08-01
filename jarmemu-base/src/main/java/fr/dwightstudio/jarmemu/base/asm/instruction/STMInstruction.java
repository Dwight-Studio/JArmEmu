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
import fr.dwightstudio.jarmemu.base.asm.argument.RegisterArrayArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.RegisterWithUpdateArgument;
import fr.dwightstudio.jarmemu.base.asm.exception.*;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import fr.dwightstudio.jarmemu.base.asm.modifier.ModifierParameter;
import fr.dwightstudio.jarmemu.base.asm.modifier.UpdateMode;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.base.sim.entity.UpdatableRegister;
import fr.dwightstudio.jarmemu.base.util.SequencedSetUtils;
import org.jetbrains.annotations.NotNull;

import java.util.SequencedSet;

public class STMInstruction extends ParsedInstruction<UpdatableRegister, Register[], Object, Object> {
    public STMInstruction(Modifier modifier, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(modifier,  arg1, arg2, arg3, arg4);
    }

    public STMInstruction(Modifier modifier, ParsedArgument<UpdatableRegister> arg1, ParsedArgument<Register[]> arg2, ParsedArgument<Object> arg3, ParsedArgument<Object> arg4) {
        super(modifier,  arg1, arg2, arg3, arg4);
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<UpdatableRegister>> getParsedArg1Class() {
        return RegisterWithUpdateArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<Register[]>> getParsedArg2Class() {
        return RegisterArrayArgument.class;
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
    @NotNull
    public SequencedSet<Class<? extends Enum<? extends ModifierParameter>>>getModifierParameterClasses() {
        return SequencedSetUtils.of(Condition.class, UpdateMode.class);
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
        return 0;
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, UpdatableRegister arg1, Register[] arg2, Object arg3, Object arg4) throws ExecutionASMException {
        int length = arg2.length;
        int value = 0;
        int address = arg1.getData();

        if (!ignoreExceptions) {
            int dataLength = 4;
            if (address % dataLength != 0) throw new MemoryAccessMisalignedASMException();
        }

        switch (modifier.updateMode()) {
            case FD, DB -> {
                for (int i = 0; i < length; i++) {
                    address = arg1.getData() - 4 * (i + 1);
                    stateContainer.getMemory().putWord(address, arg2[length - i - 1].getData());
                    if (address < stateContainer.getWritableDataAddress() && address >= stateContainer.getProgramAddress()) throw new IllegalDataWritingASMException();
                }
                value = - 4 * length;
            }
            case FA, IB -> {
                for (int i = 0; i < length; i++) {
                    address = arg1.getData() + 4 * (i + 1);
                    stateContainer.getMemory().putWord(address, arg2[length - i - 1].getData());
                    if (address < stateContainer.getWritableDataAddress() && address >= stateContainer.getProgramAddress()) throw new IllegalDataWritingASMException();
                }
                value = 4 * length;
            }
            case ED, DA -> {
                for (int i = 0; i < length; i++) {
                    address = arg1.getData() - 4 * i;
                    stateContainer.getMemory().putWord(address, arg2[length - i - 1].getData());
                    if (address < stateContainer.getWritableDataAddress() && address >= stateContainer.getProgramAddress()) throw new IllegalDataWritingASMException();
                }
                value = - 4 * length;
            }
            case EA, IA -> {
                for (int i = 0; i < length; i++) {
                    address = arg1.getData() + 4 * i;
                    stateContainer.getMemory().putWord(address, arg2[length - i - 1].getData());
                    if (address < stateContainer.getWritableDataAddress() && address >= stateContainer.getProgramAddress()) throw new IllegalDataWritingASMException();
                }
                value = 4 * length;
            }
        }
        arg1.update(value);
    }

    @Override
    protected void verify(StateContainer stateContainer, UpdatableRegister arg1, Register[] arg2, Object arg3, Object arg4) throws SyntaxASMException {
        if (modifier.updateMode() == null) throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.instruction.missingUpdateMode"));
    }
}
