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
import fr.dwightstudio.jarmemu.sim.entity.Register;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RRXExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private RRXExecutor rrxExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        rrxExecutor = new RRXExecutor();
    }

    @Test
    public void simpleRrxTest() {
        Register r0 = stateContainer.getRegister(0);
        r0.setData(25);
        rrxExecutor.execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(12, r0.getData());
        rrxExecutor.execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(6, r0.getData());
        rrxExecutor.execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(3, r0.getData());
        rrxExecutor.execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(1, r0.getData());
        rrxExecutor.execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(0, r0.getData());
        r0.setData(25);
        stateContainer.getCPSR().setC(true);
        rrxExecutor.execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(-2147483636, r0.getData());
        rrxExecutor.execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(-1073741818, r0.getData());
        rrxExecutor.execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(-536870909, r0.getData());
        stateContainer.getCPSR().setC(false);
        rrxExecutor.execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(1879048193, r0.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.getRegister(0);
        r0.setData(25);
        stateContainer.getCPSR().setC(true);
        rrxExecutor.execute(stateContainer, false, true, null, null, r0, r0, null, null);
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        rrxExecutor.execute(stateContainer, false, true, null, null, r0, r0, null, null);
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        rrxExecutor.execute(stateContainer, false, true, null, null, r0, r0, null, null);
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        rrxExecutor.execute(stateContainer, false, true, null, null, r0, r0, null, null);
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        rrxExecutor.execute(stateContainer, false, true, null, null, r0, r0, null, null);
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        rrxExecutor.execute(stateContainer, false, true, null, null, r0, r0, null, null);
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
    }

}
