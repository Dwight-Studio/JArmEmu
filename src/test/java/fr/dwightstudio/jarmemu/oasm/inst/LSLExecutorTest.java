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

package fr.dwightstudio.jarmemu.oasm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LSLExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private LSLExecutor lslExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        lslExecutor = new LSLExecutor();
    }

    @Test
    public void simpleLslTest() {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        r0.setData(25);
        r1.setData(-25);
        lslExecutor.execute(stateContainer, false, false, null, null, r0, r0, 3, null);
        assertEquals(200, r0.getData());
        lslExecutor.execute(stateContainer, false, false, null, null, r1, r1, 4, null);
        assertEquals(-400, r1.getData());
        lslExecutor.execute(stateContainer, false, false, null, null, r1, r1, 27, null);
        assertEquals(Integer.MIN_VALUE, r1.getData());
        lslExecutor.execute(stateContainer, false, false, null, null, r1, r1, 1, null);
        assertEquals(0, r1.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        r0.setData(-25);
        r1.setData(25);
        lslExecutor.execute(stateContainer, false, true, null, null, r1, r1, 4, null);
        assertEquals(400, r1.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        lslExecutor.execute(stateContainer, false, true, null, null, r0, r0, 4, null);
        assertEquals(-400, r0.getData());
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        lslExecutor.execute(stateContainer, false, true, null, null, r0, r0, 27, null);
        assertEquals(Integer.MIN_VALUE, r0.getData());
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        lslExecutor.execute(stateContainer, false, true, null, null, r0, r0, 1, null);
        assertEquals(0, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        lslExecutor.execute(stateContainer, false, true, null, null, r0, r0, 1, null);
        assertEquals(0, r0.getData());
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
    }

}
