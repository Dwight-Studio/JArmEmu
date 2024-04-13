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
import fr.dwightstudio.jarmemu.asm.argument.RotatedImmediateOrRegisterArgument;
import fr.dwightstudio.jarmemu.asm.argument.ShiftArgument;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.sim.entity.Register;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.util.MathUtils;
import org.jetbrains.annotations.NotNull;

public class RSBInstruction extends ParsedInstruction<Register, Register, Integer, ShiftArgument.ShiftFunction> {
    public RSBInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) throws ASMException {
        super(condition, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }

    public RSBInstruction(Condition condition, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, ParsedArgument<Register> arg1, ParsedArgument<Register> arg2, ParsedArgument<Integer> arg3, ParsedArgument<ShiftArgument.ShiftFunction> arg4) {
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
    protected @NotNull Class<? extends ParsedArgument<Integer>> getParsedArg3Class() {
        return RotatedImmediateOrRegisterArgument.class;
    }

    @Override
    protected @NotNull Class<? extends ParsedArgument<ShiftArgument.ShiftFunction>> getParsedArg4Class() {
        return ShiftArgument.class;
    }

    @Override
    public boolean doModifyPC() {
        return false;
    }

    @Override
    public boolean hasWorkingRegister() {
        return true;
    }

    @Override
    protected void execute(StateContainer stateContainer, boolean ignoreExceptions, Register arg1, Register arg2, Integer arg3, ShiftArgument.ShiftFunction arg4) throws ExecutionASMException {
        int i1 = arg4.apply(arg3);

        arg1.setData(i1 - arg2.getData()); // arg1 = (arg4 SHIFT arg3) - arg2

        if (updateFlags){
            stateContainer.getCPSR().setN(arg1.getData() < 0);
            stateContainer.getCPSR().setZ(arg1.getData() == 0);
            stateContainer.getCPSR().setC(MathUtils.hasCarry(i1, -arg2.getData()));
            stateContainer.getCPSR().setV(MathUtils.hasOverflow(i1, -arg2.getData()));
        }
    }

    @Override
    protected void verify(StateContainer stateContainer, Register arg1, Register arg2, Integer arg3, ShiftArgument.ShiftFunction arg4) {

    }
}
