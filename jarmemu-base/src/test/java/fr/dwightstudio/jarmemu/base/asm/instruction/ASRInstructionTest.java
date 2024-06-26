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
import fr.dwightstudio.jarmemu.base.sim.entity.RegisterOrImmediate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ASRInstructionTest extends InstructionTest<Register, Register, RegisterOrImmediate, Object> {
    protected ASRInstructionTest() {
        super(ASRInstruction.class);
    }

    @Test
    public void simpleAsrTest() throws ASMException {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        r0.setData(25);
        r1.setData(-25);
        legacyExecute(stateContainer, false, false, null, null, r0, r0, new RegisterOrImmediate(3), null);
        assertEquals(3, r0.getData());
        legacyExecute(stateContainer, false, false, null, null, r1, r1, new RegisterOrImmediate(4), null);
        assertEquals(-2, r1.getData());
        legacyExecute(stateContainer, false, false, null, null, r1, r1, new RegisterOrImmediate(27), null);
        assertEquals(-1, r1.getData());
        legacyExecute(stateContainer, false, false, null, null, r1, r1, new RegisterOrImmediate(1), null);
        assertEquals(-1, r1.getData());
    }

    @Test
    public void flagsTest() throws ASMException {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        r0.setData(25);
        r1.setData(-25);
        r2.setData(-25);
        legacyExecute(stateContainer, false, true, null, null, r2, r2, new RegisterOrImmediate(1), null);
        assertEquals(-13, r2.getData());
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        legacyExecute(stateContainer, false, true, null, null, r2, r2, new RegisterOrImmediate(1), null);
        assertEquals(-7, r2.getData());
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        legacyExecute(stateContainer, false, true, null, null, r2, r2, new RegisterOrImmediate(1), null);
        assertEquals(-4, r2.getData());
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        legacyExecute(stateContainer, false, true, null, null, r0, r0, new RegisterOrImmediate(3), null);
        assertEquals(3, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        legacyExecute(stateContainer, false, true, null, null, r1, r1, new RegisterOrImmediate(4), null);
        assertEquals(-2, r1.getData());
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        legacyExecute(stateContainer, false, true, null, null, r1, r1, new RegisterOrImmediate(27), null);
        assertEquals(-1, r1.getData());
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        legacyExecute(stateContainer, false, true, null, null, r1, r1, new RegisterOrImmediate(1), null);
        assertEquals(-1, r1.getData());
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        r1.setData(Integer.MAX_VALUE);
        legacyExecute(stateContainer, false, true, null, null, r0, r1, new RegisterOrImmediate(1), null);
        assertEquals(1073741823, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        legacyExecute(stateContainer, false, true, null, null, r0, r1, new RegisterOrImmediate(2), null);
        assertEquals(536870911, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        legacyExecute(stateContainer, false, true, null, null, r0, r1, new RegisterOrImmediate(3), null);
        assertEquals(268435455, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        legacyExecute(stateContainer, false, true, null, null, r0, r1, new RegisterOrImmediate(4), null);
        assertEquals(134217727, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        legacyExecute(stateContainer, false, true, null, null, r0, r1, new RegisterOrImmediate(31), null);
        assertEquals(0, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        r1.setData(0x7ffffffe);
        legacyExecute(stateContainer, false, true, null, null, r0, r1, new RegisterOrImmediate(1), null);
        assertEquals(1073741823, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        legacyExecute(stateContainer, false, true, null, null, r0, r1, new RegisterOrImmediate(2), null);
        assertEquals(536870911, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        legacyExecute(stateContainer, false, true, null, null, r0, r1, new RegisterOrImmediate(3), null);
        assertEquals(268435455, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        legacyExecute(stateContainer, false, true, null, null, r0, r1, new RegisterOrImmediate(4), null);
        assertEquals(134217727, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
    }
    
}
