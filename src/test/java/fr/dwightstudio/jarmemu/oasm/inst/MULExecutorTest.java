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
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MULExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private MULExecutor mulExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        mulExecutor = new MULExecutor();
    }

    @Test
    public void simpleMulTest() {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        r0.setData(16);
        r1.setData(16);
        mulExecutor.execute(stateContainer, false, false, null, null, r2, r1, r0, ArgumentParsers.SHIFT.none());
        assertEquals(16*16, r2.getData());
        r1.setData(-1);
        mulExecutor.execute(stateContainer, false, false, null, null, r0, r1, r2, ArgumentParsers.SHIFT.none());
        assertEquals(-16*16, r0.getData());
        r0.setData(8594297);
        r1.setData(859425);
        mulExecutor.execute(stateContainer, false, false, null, null, r2, r1, r0, ArgumentParsers.SHIFT.none());
        assertEquals(-1190049895, r2.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        r0.setData(16);
        r1.setData(16);
        mulExecutor.execute(stateContainer, false, true, null, null, r1, r1, r0, ArgumentParsers.SHIFT.none());
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        r1.setData(-1);
        mulExecutor.execute(stateContainer, false, true, null, null, r1, r1, r0, ArgumentParsers.SHIFT.none());
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        r1.setData(0);
        mulExecutor.execute(stateContainer, false, true, null, null, r1, r1, r0, ArgumentParsers.SHIFT.none());
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
    }

}
