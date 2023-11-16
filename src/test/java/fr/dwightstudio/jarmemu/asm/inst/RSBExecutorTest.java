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
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RSBExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private StateContainer stateContainerBis;
    private RSBExecutor rsbExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        stateContainerBis = new StateContainer();
        rsbExecutor = new RSBExecutor();
    }

    @Test
    public void simpleRsbTest() {
        stateContainer.registers[0].setData(-15);
        Register r0 = stateContainerBis.registers[0];
        r0.setData(99);
        Register r1 = stateContainerBis.registers[1];
        r1.setData(20);
        Register r2 = stateContainerBis.registers[2];
        r2.setData(5);
        rsbExecutor.execute(stateContainerBis, false, false, null, null, r0, r1, r2.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(stateContainer.registers[0].getData(), r0.getData());
        stateContainer.registers[0].setData(0b01111111111111111111111111111111);
        r0.setData(1);
        r1.setData(0b10000000000000000000000000000000);
        rsbExecutor.execute(stateContainerBis, false, false, null, null, r2, r0, r1.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(stateContainer.registers[0].getData(), r2.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        r0.setData(1);
        r1.setData(0b10000000000000000000000000000000);
        rsbExecutor.execute(stateContainer, false, true, null, null, r2, r0, r1.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(0b01111111111111111111111111111111, r2.getData());
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
        assertTrue(stateContainer.cpsr.getV());
        r0.setData(0b11111111111111111111111111111111);
        r1.setData(0b11111111111111111111111111111111);
        rsbExecutor.execute(stateContainer, false, true, null, null, r2, r0, r1.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(0, r2.getData());
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
        assertFalse(stateContainer.cpsr.getV());
        r0.setData(0b11111111111111111111111111111111);
        r1.setData(0b01111111111111111111111111111111);
        rsbExecutor.execute(stateContainer, false, true, null, null, r2, r0, r1.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(0b10000000000000000000000000000000, r2.getData());
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
        assertTrue(stateContainer.cpsr.getV());
    }

}
