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

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.DataMode;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SWPInstructionTest extends InstructionTest<Register, Register, Integer, Object> {

    SWPInstructionTest() {
        super(SWPInstruction.class);
    }

    @Test
    public void simpleSwpTest() throws ASMException {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        Register r3 = stateContainer.getRegister(3);
        r1.setData(10);
        r2.setData(20);
        r3.setData(200);
        stateContainer.getMemory().putWord(100, 25);
        stateContainer.getMemory().putWord(200, 50);
        stateContainer.getMemory().putWord(300, 75);
        assertEquals(10, r1.getData());
        legacyExecute(stateContainer, false, false, null, null, r1, r2, r3.getData(), null);
        assertEquals(50, r1.getData());
        assertEquals(20, stateContainer.getMemory().getWord(200));
        stateContainer.getMemory().putByte(0x9000, (byte) 0xAB);
        r0.setData(0x9000);
        r1.setData(0x56783412);
        legacyExecute(stateContainer, false, false, DataMode.B, null, r1, r1, r0.getData(), null);
        assertEquals(0x9000, r0.getData());
        assertEquals((byte) 0xAB, r1.getData());
    }
}