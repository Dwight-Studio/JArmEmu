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
import fr.dwightstudio.jarmemu.asm.argument.ImmediateArgument;
import fr.dwightstudio.jarmemu.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.asm.argument.RegisterArgument;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.entity.Register;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

public class BFIInstruction extends ParsedInstruction<Register, Register, Integer, Integer> {
    public BFIInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(condition, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    @Override
    protected Class<? extends ParsedArgument<Register>> getParsedArg1Class() {
        return RegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Register>> getParsedArg2Class() {
        return RegisterArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Integer>> getParsedArg3Class() {
        return ImmediateArgument.class;
    }

    @Override
    protected Class<? extends ParsedArgument<Integer>> getParsedArg4Class() {
        return ImmediateArgument.class;
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
    protected void execute(StateContainer stateContainer, boolean forceExecution, Register arg1, Register arg2, Integer arg3, Integer arg4) throws ExecutionASMException {
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
