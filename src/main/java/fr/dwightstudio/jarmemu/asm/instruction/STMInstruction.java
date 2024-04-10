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

package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.Condition;
import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.argument.NullArgument;
import fr.dwightstudio.jarmemu.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.asm.argument.RegisterArrayArgument;
import fr.dwightstudio.jarmemu.asm.argument.RegisterWithUpdateArgument;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.IllegalDataWritingASMException;
import fr.dwightstudio.jarmemu.asm.exception.MemoryAccessMisalignedASMException;
import fr.dwightstudio.jarmemu.sim.entity.Register;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

public class STMInstruction extends ParsedInstruction<RegisterWithUpdateArgument.UpdatableRegister, Register[], Object, Object> {
    public STMInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(condition, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    public STMInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, ParsedArgument<RegisterWithUpdateArgument.UpdatableRegister> arg1, ParsedArgument<Register[]> arg2, ParsedArgument<Object> arg3, ParsedArgument<Object> arg4) {
        super(condition, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    @Override
    protected Class<? extends ParsedArgument<RegisterWithUpdateArgument.UpdatableRegister>> getParsedArg1Class() {
        return RegisterWithUpdateArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Register[]>> getParsedArg2Class() {
        return RegisterArrayArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Object>> getParsedArg3Class() {
        return NullArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Object>> getParsedArg4Class() {
        return NullArgument.class;
    }

    @Override
    public boolean doModifyPC() {
        return false;
    }

    @Override
    public boolean hasWorkingRegister() {
        return false;
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, RegisterWithUpdateArgument.UpdatableRegister arg1, Register[] arg2, Object arg3, Object arg4) throws ExecutionASMException {
        int length = arg2.length;
        int value = 0;
        int address = arg1.getData();

        if (!ignoreExceptions) {
            int dataLength = 4;
            if (address % dataLength != 0) throw new MemoryAccessMisalignedASMException();
        }

        switch (updateMode) {
            case FD, DB -> {
                for (int i = 0; i < length; i++) {
                    address = arg1.getData() - 4 * (i + 1);
                    stateContainer.getMemory().putWord(address, arg2[length - i - 1].getData());
                    if (address < stateContainer.getLastAddressRORange() && address >= stateContainer.getSymbolsAddress()) throw new IllegalDataWritingASMException();
                }
                value = - 4 * length;
            }
            case FA, IB -> {
                for (int i = 0; i < length; i++) {
                    address = arg1.getData() + 4 * (i + 1);
                    stateContainer.getMemory().putWord(address, arg2[length - i - 1].getData());
                    if (address < stateContainer.getLastAddressRORange() && address >= stateContainer.getSymbolsAddress()) throw new IllegalDataWritingASMException();
                }
                value = 4 * length;
            }
            case ED, DA -> {
                for (int i = 0; i < length; i++) {
                    address = arg1.getData() - 4 * i;
                    stateContainer.getMemory().putWord(address, arg2[length - i - 1].getData());
                    if (address < stateContainer.getLastAddressRORange() && address >= stateContainer.getSymbolsAddress()) throw new IllegalDataWritingASMException();
                }
                value = - 4 * length;
            }
            case EA, IA -> {
                for (int i = 0; i < length; i++) {
                    address = arg1.getData() + 4 * i;
                    stateContainer.getMemory().putWord(address, arg2[length - i - 1].getData());
                    if (address < stateContainer.getLastAddressRORange() && address >= stateContainer.getSymbolsAddress()) throw new IllegalDataWritingASMException();
                }
                value = 4 * length;
            }
        }
        arg1.update(value);
    }

    @Override
    protected void verify(StateContainer stateContainer, RegisterWithUpdateArgument.UpdatableRegister arg1, Register[] arg2, Object arg3, Object arg4) {

    }
}
