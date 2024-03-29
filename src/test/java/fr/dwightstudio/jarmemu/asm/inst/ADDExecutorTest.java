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
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ADDExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private StateContainer stateContainerBis;
    private ADDExecutor addExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        stateContainerBis = new StateContainer();
        addExecutor = new ADDExecutor();
    }

    @Test
    public void simpleAddTest() {
        stateContainer.getRegister(0).setData(25);
        Register r0 = stateContainerBis.getRegister(0);
        r0.setData(99);
        Register r1 = stateContainerBis.getRegister(1);
        r1.setData(5);
        Register r2 = stateContainerBis.getRegister(2);
        r2.setData(20);
        addExecutor.execute(stateContainerBis, false, false, null, null, r0, r1, r2.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(stateContainer.getRegister(0).getData(), r0.getData());
    }

    @Test
    public void shiftRegisterTest() {
        stateContainer.getRegister(0).setData(560);
        Register r0 = stateContainerBis.getRegister(0);
        r0.setData(99);
        Register r1 = stateContainerBis.getRegister(1);
        r1.setData(13);
        Register r2 = stateContainerBis.getRegister(2);
        r2.setData(456);
        addExecutor.execute(stateContainerBis, false, false, null, null, r0, r2, r1.getData(), ArgumentParsers.SHIFT.parse(stateContainerBis, "LSL#3"));
        assertEquals(stateContainer.getRegister(0).getData(), r0.getData());
    }

    @Test
    public void instantValueAddTest() {
        stateContainer.getRegister(0).setData(65736);
        Register r0 = stateContainerBis.getRegister(0);
        r0.setData(99);
        Register r1 = stateContainerBis.getRegister(1);
        r1.setData(456);
        addExecutor.execute(stateContainerBis, false, false, null, null, r0, r1, 0xFF00, ArgumentParsers.SHIFT.none());
        assertEquals(stateContainer.getRegister(0).getData(), r0.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.getRegister(0);
        r0.setData(-55);
        Register r1 = stateContainer.getRegister(1);
        r1.setData(45);
        Register r2 = stateContainer.getRegister(2);
        addExecutor.execute(stateContainer, false, true, null, null, r2, r1, r0.getData(), ArgumentParsers.SHIFT.none());
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        assertFalse(stateContainer.getCPSR().getV());
        addExecutor.execute(stateContainer, false, false, null, null, r2, r2, 10, ArgumentParsers.SHIFT.none());
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        assertFalse(stateContainer.getCPSR().getV());
        addExecutor.execute(stateContainer, false, true, null, null, r2, r2, 0, ArgumentParsers.SHIFT.none());
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        assertFalse(stateContainer.getCPSR().getV());
        r0.setData(0b01111111111111111111111111111111);
        r1.setData(1);
        addExecutor.execute(stateContainer, false, true, null, null, r2, r1, r0.getData(), ArgumentParsers.SHIFT.none());
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        assertTrue(stateContainer.getCPSR().getV());
        r0.setData(0b11111111111111111111111111111111);
        r1.setData(1);
        addExecutor.execute(stateContainer, false, true, null, null, r2, r1, r0.getData(), ArgumentParsers.SHIFT.none());
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        assertFalse(stateContainer.getCPSR().getV());
        r0.setData(0b10000000000000000000000000000000);
        r1.setData(0b10000000000000000000000000000000);
        addExecutor.execute(stateContainer, false, true, null, null, r2, r1, r0.getData(), ArgumentParsers.SHIFT.none());
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        assertTrue(stateContainer.getCPSR().getV());
    }

}
