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

public class UMULLExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private UMULLExecutor umullExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        umullExecutor = new UMULLExecutor();
    }

    @Test
    public void simpleUmullTest() {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        Register r3 = stateContainer.getRegister(3);
        r2.setData(1);
        r3.setData(-1);
        umullExecutor.execute(stateContainer, false, false, null, null, r1, r0, r2, r3);
        assertEquals(0, r0.getData());
        assertEquals(-1, r1.getData());
        r2.setData(4);
        r3.setData(5);
        umullExecutor.execute(stateContainer, false, false, null, null, r1, r0, r2, r3);
        assertEquals(0, r0.getData());
        assertEquals(20, r1.getData());
        r2.setData(Integer.MAX_VALUE);
        r3.setData(2);
        umullExecutor.execute(stateContainer, false, false, null, null, r1, r0, r2, r3);
        assertEquals(0, r0.getData());
        assertEquals(-2, r1.getData());
        r2.setData(Integer.MAX_VALUE);
        r3.setData(3);
        umullExecutor.execute(stateContainer, false, false, null, null, r1, r0, r2, r3);
        assertEquals(1, r0.getData());
        assertEquals(Integer.MAX_VALUE-2, r1.getData());
        r2.setData(-Integer.MAX_VALUE);
        r3.setData(3);
        umullExecutor.execute(stateContainer, false, false, null, null, r1, r0, r2, r3);
        assertEquals(1, r0.getData());
        assertEquals(Integer.MIN_VALUE+3, r1.getData());
        r2.setData(-Integer.MAX_VALUE);
        r3.setData(654);
        umullExecutor.execute(stateContainer, false, false, null, null, r1, r0, r2, r3);
        assertEquals(327, r0.getData());
        assertEquals(654, r1.getData());
        r2.setData(-Integer.MAX_VALUE);
        r3.setData(-Integer.MAX_VALUE);
        umullExecutor.execute(stateContainer, false, false, null, null, r1, r0, r2, r3);
        assertEquals(1073741825, r0.getData());
        assertEquals(1, r1.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        Register r3 = stateContainer.getRegister(3);
        r2.setData(5);
        r3.setData(4);
        umullExecutor.execute(stateContainer, false, true, null, null, r1, r0, r2, r3);
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        r2.setData(0);
        r3.setData(4);
        umullExecutor.execute(stateContainer, false, true, null, null, r1, r0, r2, r3);
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
        r2.setData(-1);
        r3.setData(1);
        umullExecutor.execute(stateContainer, false, true, null, null, r1, r0, r2, r3);
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        r2.setData(-1);
        r3.setData(-456);
        umullExecutor.execute(stateContainer, false, true, null, null, r1, r0, r2, r3);
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        r2.setData(Integer.MIN_VALUE);
        r3.setData(456);
        umullExecutor.execute(stateContainer, false, true, null, null, r1, r0, r2, r3);
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
    }

}
