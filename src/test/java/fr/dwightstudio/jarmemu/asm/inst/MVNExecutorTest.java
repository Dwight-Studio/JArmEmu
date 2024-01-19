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

public class MVNExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private MVNExecutor mvnExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        mvnExecutor = new MVNExecutor();
    }

    @Test
    public void simpleMvnTest() {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        mvnExecutor.execute(stateContainer, false, false, null, null, r0, Integer.MIN_VALUE, ArgumentParsers.SHIFT.none(), null);
        assertEquals(Integer.MAX_VALUE, r0.getData());
        mvnExecutor.execute(stateContainer, false, false, null, null, r1, r0.getData(), ArgumentParsers.SHIFT.none(), null);
        assertEquals(Integer.MIN_VALUE, r1.getData());
        mvnExecutor.execute(stateContainer, false, false, null, null, r2, 0b10101010101010101010010001000001, ArgumentParsers.SHIFT.none(), null);
        assertEquals(~0b10101010101010101010010001000001, r2.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        mvnExecutor.execute(stateContainer, false, true, null, null, r0, 0, ArgumentParsers.SHIFT.none(), null);
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        mvnExecutor.execute(stateContainer, false, true, null, null, r1, -2, ArgumentParsers.SHIFT.none(), null);
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        r1.setData(-1);
        mvnExecutor.execute(stateContainer, false, true, null, null, r2, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
    }

}
