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
import fr.dwightstudio.jarmemu.asm.argument.ParsedArgument;
import fr.dwightstudio.jarmemu.asm.argument.RegisterArgument;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.sim.entity.Register;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;
import org.jetbrains.annotations.NotNull;

public class UMULLInstruction extends ParsedInstruction<Register, Register, Register, Register> {

    public UMULLInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(condition, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    public UMULLInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, ParsedArgument<Register> arg1, ParsedArgument<Register> arg2, ParsedArgument<Register> arg3, ParsedArgument<Register> arg4) {
        super(condition, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    @Override
    protected @NotNull Class<? extends ParsedArgument<Register>> getParsedArg1Class() {
        return RegisterArgument.class;
    }

    @Override
    protected @NotNull Class<? extends ParsedArgument<Register>> getParsedArg2Class() {
        return RegisterArgument.class;
    }

    @Override
    protected @NotNull Class<? extends ParsedArgument<Register>> getParsedArg3Class() {
        return RegisterArgument.class;
    }

    @Override
    protected @NotNull Class<? extends ParsedArgument<Register>> getParsedArg4Class() {
        return RegisterArgument.class;
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
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, Register arg1, Register arg2, Register arg3, Register arg4) throws ExecutionASMException {
        long r3 = arg3.getData() & 0xFFFFFFFFL;
        long r4 = arg4.getData() & 0xFFFFFFFFL;
        long result = r3 * r4;  // result = (unsigned) arg3 * (unsigned) arg4
        arg1.setData((int) (result));   // arg1 = result[31..0]
        arg2.setData((int) (result >>> 32)); // arg1 = result[63..32]

        if (updateFlags) {
            stateContainer.getCPSR().setN(arg2.getData() < 0);
            stateContainer.getCPSR().setZ(arg1.getData() == 0 && arg2.getData() == 0);
        }
    }

    @Override
    protected void verify(StateContainer stateContainer, Register arg1, Register arg2, Register arg3, Register arg4) {

    }
}
