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

public class MOVExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private MOVExecutor movExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        movExecutor = new MOVExecutor();
    }

    @Test
    public void simpleMovTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        movExecutor.execute(stateContainer, false, false, null, null, r0, 5, ArgumentParsers.SHIFT.none(), null);
        movExecutor.execute(stateContainer, false, false, null, null, r1, r0.getData(), ArgumentParsers.SHIFT.none(), null);
        r1.setData(r1.getData()+1);
        movExecutor.execute(stateContainer, false, false, null, null, r2, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertEquals(5, r0.getData());
        assertEquals(6, r1.getData());
        assertEquals(6, r2.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        movExecutor.execute(stateContainer, false, true, null, null, r0, 0, ArgumentParsers.SHIFT.none(), null);
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
        movExecutor.execute(stateContainer, false, true, null, null, r1, -2, ArgumentParsers.SHIFT.none(), null);
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r1.setData(4);
        movExecutor.execute(stateContainer, false, true, null, null, r2, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
    }

}
