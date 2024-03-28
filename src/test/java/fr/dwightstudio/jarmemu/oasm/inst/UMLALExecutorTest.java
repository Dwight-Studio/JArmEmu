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

public class UMLALExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private UMLALExecutor umlalExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        umlalExecutor = new UMLALExecutor();
    }

    @Test
    public void simpleUmlalTest() {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        Register r3 = stateContainer.getRegister(3);
        r2.setData(4);
        r3.setData(5);
        umlalExecutor.execute(stateContainer, false, false, null, null, r1, r0, r2, r3);
        assertEquals(0, r0.getData());
        assertEquals(20, r1.getData());
        umlalExecutor.execute(stateContainer, false, false, null, null, r1, r0, r2, r3);
        assertEquals(0, r0.getData());
        assertEquals(40, r1.getData());
        r0.setData(0);
        r1.setData(0);
        r2.setData(1);
        r3.setData(-1);
        umlalExecutor.execute(stateContainer, false, false, null, null, r1, r0, r2, r3);
        assertEquals(0, r0.getData());
        assertEquals(-1, r1.getData());
        umlalExecutor.execute(stateContainer, false, false, null, null, r1, r0, r2, r3);
        assertEquals(1, r0.getData());
        assertEquals(-2, r1.getData());
        r0.setData(0);
        r1.setData(0);
        r2.setData(Integer.MAX_VALUE);
        r3.setData(1);
        umlalExecutor.execute(stateContainer, false, false, null, null, r1, r0, r2, r3);
        assertEquals(0, r0.getData());
        assertEquals(Integer.MAX_VALUE, r1.getData());
        umlalExecutor.execute(stateContainer, false, false, null, null, r1, r0, r2, r3);
        assertEquals(0, r0.getData());
        assertEquals(-2, r1.getData());
        r0.setData(0);
        r1.setData(0);
        r2.setData(Integer.MIN_VALUE);
        r3.setData(1);
        umlalExecutor.execute(stateContainer, false, false, null, null, r1, r0, r2, r3);
        assertEquals(0, r0.getData());
        assertEquals(Integer.MIN_VALUE, r1.getData());
        umlalExecutor.execute(stateContainer, false, false, null, null, r1, r0, r2, r3);
        assertEquals(1, r0.getData());
        assertEquals(0, r1.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        Register r3 = stateContainer.getRegister(3);
        r2.setData(4);
        r3.setData(5);
        umlalExecutor.execute(stateContainer, false, true, null, null, r1, r0, r2, r3);
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        r2.setData(4);
        r3.setData(-5);
        umlalExecutor.execute(stateContainer, false, true, null, null, r1, r0, r2, r3);
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        r0.setData(0);
        r1.setData(0);
        r2.setData(4);
        r3.setData(0);
        umlalExecutor.execute(stateContainer, false, true, null, null, r1, r0, r2, r3);
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
        r2.setData(-1);
        r3.setData(-456);
        umlalExecutor.execute(stateContainer, false, true, null, null, r1, r0, r2, r3);
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        umlalExecutor.execute(stateContainer, false, true, null, null, r1, r0, r2, r3);
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
    }

}
