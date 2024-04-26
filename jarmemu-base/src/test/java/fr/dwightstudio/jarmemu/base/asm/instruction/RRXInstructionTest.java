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

class RRXInstructionTest extends InstructionTest<Register, Register, Object, Object> {
    RRXInstructionTest() {
        super(RRXInstruction.class);
    }

    @Test
    public void simpleRrxTest() throws ASMException {
        Register r0 = stateContainer.getRegister(0);
        r0.setData(25);
        execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(12, r0.getData());
        execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(6, r0.getData());
        execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(3, r0.getData());
        execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(1, r0.getData());
        execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(0, r0.getData());
        r0.setData(25);
        stateContainer.getCPSR().setC(true);
        execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(-2147483636, r0.getData());
        execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(-1073741818, r0.getData());
        execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(-536870909, r0.getData());
        stateContainer.getCPSR().setC(false);
        execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(1879048193, r0.getData());
    }

    @Test
    public void flagsTest() throws ASMException {
        Register r0 = stateContainer.getRegister(0);
        r0.setData(25);
        stateContainer.getCPSR().setC(true);
        execute(stateContainer, false, true, null, null, r0, r0, null, null);
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        execute(stateContainer, false, true, null, null, r0, r0, null, null);
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        execute(stateContainer, false, true, null, null, r0, r0, null, null);
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        execute(stateContainer, false, true, null, null, r0, r0, null, null);
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        execute(stateContainer, false, true, null, null, r0, r0, null, null);
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        execute(stateContainer, false, true, null, null, r0, r0, null, null);
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
    }
}