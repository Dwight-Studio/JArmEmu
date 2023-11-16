/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2023 Dwight Studio
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

public class SMULLExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private SMULLExecutor smullExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        smullExecutor = new SMULLExecutor();
    }

    @Test
    public void simpleSmullTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        Register r3 = stateContainer.registers[3];
        r2.setData(1);
        r3.setData(-1);
        smullExecutor.execute(stateContainer, false, false, null, null, r0, r1, r2, r3);
        assertEquals(-1, r1.getData());
        assertEquals(-1, r0.getData());
        r2.setData(25);
        r3.setData(14);
        smullExecutor.execute(stateContainer, false, false, null, null, r0, r1, r2, r3);
        assertEquals(0, r1.getData());
        assertEquals(350, r0.getData());
        r2.setData(Integer.MAX_VALUE);
        r3.setData(Integer.MAX_VALUE);
        smullExecutor.execute(stateContainer, false, false, null, null, r0, r1, r2, r3);
        assertEquals(1073741823, r1.getData());
        assertEquals(1, r0.getData());
        r2.setData(Integer.MAX_VALUE);
        r3.setData(-21);
        smullExecutor.execute(stateContainer, false, false, null, null, r0, r1, r2, r3);
        assertEquals(-11, r1.getData());
        assertEquals(-2147483627, r0.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        Register r3 = stateContainer.registers[3];
        r2.setData(1);
        r3.setData(-1);
        smullExecutor.execute(stateContainer, false, true, null, null, r0, r1, r2, r3);
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r2.setData(0);
        r3.setData(-1);
        smullExecutor.execute(stateContainer, false, true, null, null, r0, r1, r2, r3);
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
        r2.setData(4);
        r3.setData(5);
        smullExecutor.execute(stateContainer, false, true, null, null, r0, r1, r2, r3);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r2.setData(1);
        r3.setData(1);
        smullExecutor.execute(stateContainer, false, true, null, null, r0, r1, r2, r3);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
    }

}
