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
import fr.dwightstudio.jarmemu.asm.argument.*;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.IllegalDataWritingASMException;
import fr.dwightstudio.jarmemu.asm.exception.MemoryAccessMisalignedASMException;
import fr.dwightstudio.jarmemu.sim.entity.Register;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

public class STRInstruction extends ParsedInstruction<Register, AddressArgument.UpdatableInteger, Integer, ShiftArgument.ShiftFunction> {
    public STRInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(condition, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    @Override
    protected Class<? extends ParsedArgument<Register>> getParsedArg1Class() {
        return RegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<AddressArgument.UpdatableInteger>> getParsedArg2Class() {
        return AddressArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Integer>> getParsedArg3Class() {
        return ImmediateOrRegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<ShiftArgument.ShiftFunction>> getParsedArg4Class() {
        return ShiftArgument.class;
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
    protected void execute(StateContainer stateContainer, boolean forceExecution, Register arg1, AddressArgument.UpdatableInteger arg2, Integer arg3, ShiftArgument.ShiftFunction arg4) throws ExecutionASMException {
        int i1 = arg4.apply(arg3);
        int address = arg2.toInt() + i1;

        if (!forceExecution) {
            int dataLength;

            switch (dataMode) {
                case HALF_WORD -> dataLength = 2;
                case BYTE -> dataLength = 1;
                case null, default -> dataLength = 4;
            }

            if (address % dataLength != 0) throw new MemoryAccessMisalignedASMException();
            if (address < stateContainer.getLastAddressRORange() && address >= stateContainer.getSymbolsAddress()) throw new IllegalDataWritingASMException();
        }

        switch (dataMode){
            case null -> stateContainer.getMemory().putWord(address, arg1.getData());
            case HALF_WORD -> stateContainer.getMemory().putHalf(address, (short) arg1.getData());
            case BYTE -> stateContainer.getMemory().putByte(address, (byte) arg1.getData());
        }

        arg2.update();
    }

    @Override
    protected void verify(StateContainer stateContainer, Register arg1, AddressArgument.UpdatableInteger arg2, Integer arg3, ShiftArgument.ShiftFunction arg4) {

    }
}
