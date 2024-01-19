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

package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ASRExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private ASRExecutor asrExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        asrExecutor = new ASRExecutor();
    }

    @Test
    public void simpleAsrTest() {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        r0.setData(25);
        r1.setData(-25);
        asrExecutor.execute(stateContainer, false, false, null, null, r0, r0, 3, null);
        assertEquals(3, r0.getData());
        asrExecutor.execute(stateContainer, false, false, null, null, r1, r1, 4, null);
        assertEquals(-2, r1.getData());
        asrExecutor.execute(stateContainer, false, false, null, null, r1, r1, 27, null);
        assertEquals(-1, r1.getData());
        asrExecutor.execute(stateContainer, false, false, null, null, r1, r1, 1, null);
        assertEquals(-1, r1.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        r0.setData(25);
        r1.setData(-25);
        r2.setData(-25);
        asrExecutor.execute(stateContainer, false, true, null, null, r2, r2, 1, null);
        assertEquals(-13, r2.getData());
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        asrExecutor.execute(stateContainer, false, true, null, null, r2, r2, 1, null);
        assertEquals(-7, r2.getData());
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        asrExecutor.execute(stateContainer, false, true, null, null, r2, r2, 1, null);
        assertEquals(-4, r2.getData());
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        asrExecutor.execute(stateContainer, false, true, null, null, r0, r0, 3, null);
        assertEquals(3, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        asrExecutor.execute(stateContainer, false, true, null, null, r1, r1, 4, null);
        assertEquals(-2, r1.getData());
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        asrExecutor.execute(stateContainer, false, true, null, null, r1, r1, 27, null);
        assertEquals(-1, r1.getData());
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        asrExecutor.execute(stateContainer, false, true, null, null, r1, r1, 1, null);
        assertEquals(-1, r1.getData());
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        r1.setData(Integer.MAX_VALUE);
        asrExecutor.execute(stateContainer, false, true, null, null, r0, r1, 1, null);
        assertEquals(1073741823, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        asrExecutor.execute(stateContainer, false, true, null, null, r0, r1, 2, null);
        assertEquals(536870911, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        asrExecutor.execute(stateContainer, false, true, null, null, r0, r1, 3, null);
        assertEquals(268435455, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        asrExecutor.execute(stateContainer, false, true, null, null, r0, r1, 4, null);
        assertEquals(134217727, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        asrExecutor.execute(stateContainer, false, true, null, null, r0, r1, 31, null);
        assertEquals(0, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        r1.setData(0x7ffffffe);
        asrExecutor.execute(stateContainer, false, true, null, null, r0, r1, 1, null);
        assertEquals(1073741823, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        asrExecutor.execute(stateContainer, false, true, null, null, r0, r1, 2, null);
        assertEquals(536870911, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        asrExecutor.execute(stateContainer, false, true, null, null, r0, r1, 3, null);
        assertEquals(268435455, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        asrExecutor.execute(stateContainer, false, true, null, null, r0, r1, 4, null);
        assertEquals(134217727, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
    }

}
