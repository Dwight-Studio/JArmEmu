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

import fr.dwightstudio.jarmemu.asm.argument.AddressArgument;
import fr.dwightstudio.jarmemu.asm.argument.ShiftArgument;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static fr.dwightstudio.jarmemu.asm.instruction.DataMode.BYTE;
import static fr.dwightstudio.jarmemu.asm.instruction.DataMode.HALF_WORD;
import static org.junit.jupiter.api.Assertions.assertEquals;

class STRInstructionTest extends InstructionTest<Register, AddressArgument.UpdatableInteger, Integer, ShiftArgument.ShiftFunction> {
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
        execute(stateContainer, false, false, null, null, r3, new AddressArgument.UpdatableInteger(r0.getData(), stateContainer, false, false, null), 0, shift());
        assertEquals(54, stateContainer.getMemory().getWord(100));
        execute(stateContainer, false, false, HALF_WORD, null, r3, new AddressArgument.UpdatableInteger(r1.getData(), stateContainer, false, false, null), 0, shift());
        assertEquals(54, stateContainer.getMemory().getHalf(104));
        execute(stateContainer, false, false, BYTE, null, r3, new AddressArgument.UpdatableInteger(r2.getData(), stateContainer, false, false, null), 0, shift());
        assertEquals(54, stateContainer.getMemory().getByte(106));
    }
}