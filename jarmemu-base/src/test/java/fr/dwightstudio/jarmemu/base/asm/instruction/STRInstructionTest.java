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

import fr.dwightstudio.jarmemu.base.asm.argument.AddressArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.ImmediateOrRegisterArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.ShiftArgument;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.DataMode;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class STRInstructionTest extends InstructionTest<Register, AddressArgument.UpdatableInteger, ImmediateOrRegisterArgument.RegisterOrImmediate, ShiftArgument.ShiftFunction> {
    STRInstructionTest() {
        super(STRInstruction.class);
    }

    @Test
    public void simpleStrTest() throws ASMException {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        Register r3 = stateContainer.getRegister(3);
        r0.setData(100);
        r1.setData(104);
        r2.setData(106);
        r3.setData(54);
        legacyExecute(stateContainer, false, false, null, null, r3, new AddressArgument.UpdatableInteger(r0.getData(), stateContainer, false, false, null), new ImmediateOrRegisterArgument.RegisterOrImmediate(0), shift());
        assertEquals(54, stateContainer.getMemory().getWord(100));
        legacyExecute(stateContainer, false, false, DataMode.H, null, r3, new AddressArgument.UpdatableInteger(r1.getData(), stateContainer, false, false, null), new ImmediateOrRegisterArgument.RegisterOrImmediate(0), shift());
        assertEquals(54, stateContainer.getMemory().getHalf(104));
        legacyExecute(stateContainer, false, false, DataMode.B, null, r3, new AddressArgument.UpdatableInteger(r2.getData(), stateContainer, false, false, null), new ImmediateOrRegisterArgument.RegisterOrImmediate(0), shift());
        assertEquals(54, stateContainer.getMemory().getByte(106));
    }
}