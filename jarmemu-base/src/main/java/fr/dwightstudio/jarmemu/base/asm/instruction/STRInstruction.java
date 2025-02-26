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
import fr.dwightstudio.jarmemu.base.asm.exception.*;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.DataMode;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import fr.dwightstudio.jarmemu.base.asm.modifier.ModifierParameter;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.RegisterOrImmediate;
import fr.dwightstudio.jarmemu.base.sim.entity.ShiftFunction;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class STRInstruction extends ParsedInstruction<Register, AddressArgument.UpdatableInteger, RegisterOrImmediate, ShiftFunction> {
    public STRInstruction(Modifier modifier, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(modifier, arg1, arg2, arg3, arg4);
    }

    public STRInstruction(Modifier modifier, ParsedArgument<Register> arg1, ParsedArgument<AddressArgument.UpdatableInteger> arg2, ParsedArgument<RegisterOrImmediate> arg3, ParsedArgument<ShiftFunction> arg4) {
        super(modifier, arg1, arg2, arg3, arg4);
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<Register>> getParsedArg1Class() {
        return RegisterArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<AddressArgument.UpdatableInteger>> getParsedArg2Class() {
        return AddressArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<RegisterOrImmediate>> getParsedArg3Class() {
        return PostOffsetArgument.class;
    }

    @Override
    @NotNull
    public Class<? extends ParsedArgument<ShiftFunction>> getParsedArg4Class() {
        return ShiftArgument.class;
    }

    @Override
    public @NotNull Set<Class<? extends Enum<? extends ModifierParameter>>> getModifierParameterClasses() {
        return Set.of(DataMode.class, Condition.class);
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
        if (this.modifier.dataMode() == DataMode.H) {
            return InstructionCodeUtils.singleMemoryAccessSHB(stateContainer, this, true);
        } else {
            return InstructionCodeUtils.singleMemoryAccess(stateContainer, this, true, null, pos);
        }
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, Register arg1, AddressArgument.UpdatableInteger arg2, RegisterOrImmediate arg3, ShiftFunction arg4) throws ExecutionASMException {
        int i1 = arg4.apply(arg3);
        int address = arg2.toInt();

        if (!ignoreExceptions) {
            int dataLength;

            switch (modifier.dataMode()) {
                case H -> dataLength = 2;
                case B -> dataLength = 1;
                case null, default -> dataLength = 4;
            }

            if (address % dataLength != 0) {
                arg2.cancelUpdate();
                throw new MemoryAccessMisalignedASMException();
            }
            if (address < stateContainer.getWritableDataAddress() && address >= stateContainer.getProgramAddress()) {
                arg2.cancelUpdate();
                throw new IllegalDataWritingASMException();
            }
        }

        switch (modifier.dataMode()){
            case null -> stateContainer.getMemory().checkedPutWord(address, arg1.getData());
            case H -> stateContainer.getMemory().checkedPutHalf(address, (short) arg1.getData());
            case B -> stateContainer.getMemory().checkedPutByte(address, (byte) arg1.getData());
        }

        arg2.update(i1);
    }

    @Override
    protected void verify(StateContainer stateContainer, Register arg1, AddressArgument.UpdatableInteger arg2, RegisterOrImmediate arg3, ShiftFunction arg4) throws SyntaxASMException {
        arg4.check(arg3);

        if (arg3.intValue() != 0 && !arg2.canUpdate()) {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.instruction.indexing"));
        }
        if (this.arg2 != null) {
            if (this.modifier.dataMode() == DataMode.H && ((AddressArgument) this.arg2).getMode() == AddressType.SHIFTED_REGISTER_OFFSET) {
                throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.instruction.shifting"));
            }
        }
    }
}
