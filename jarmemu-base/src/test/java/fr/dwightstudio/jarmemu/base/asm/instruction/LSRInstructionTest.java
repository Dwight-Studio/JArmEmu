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
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LSRInstructionTest extends InstructionTest<Register, Register, Integer, Object> {
    LSRInstructionTest() {
        super(LSRInstruction.class);
    }

    @Test
    public void simpleLsrTest() throws ASMException {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        r0.setData(25);
        r1.setData(-25);
        execute(stateContainer, false, false, null, null, r0, r0, 3, null);
        assertEquals(3, r0.getData());
        execute(stateContainer, false, false, null, null, r1, r1, 4, null);
        assertEquals(268435454, r1.getData());
        execute(stateContainer, false, false, null, null, r1, r1, 27, null);
        assertEquals(1, r1.getData());
        execute(stateContainer, false, false, null, null, r1, r1, 1, null);
        assertEquals(0, r1.getData());
    }

    @Test
    public void flagsTest() throws ASMException {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        r0.setData(-25);
        r1.setData(25);
        execute(stateContainer, false, true, null, null, r1, r1, 3, null);
        assertEquals(3, r1.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        execute(stateContainer, false, true, null, null, r0, r0, 4, null);
        assertEquals(268435454, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        execute(stateContainer, false, true, null, null, r0, r0, 27, null);
        assertEquals(1, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        execute(stateContainer, false, true, null, null, r0, r0, 1, null);
        assertEquals(0, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        execute(stateContainer, false, true, null, null, r0, r0, 1, null);
        assertEquals(0, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
    }
}