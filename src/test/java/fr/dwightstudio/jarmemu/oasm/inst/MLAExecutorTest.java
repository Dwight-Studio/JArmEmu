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

public class MLAExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private MLAExecutor mlaExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        mlaExecutor = new MLAExecutor();
    }

    @Test
    public void simpleMlaTest() {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        Register r3 = stateContainer.getRegister(3);
        r1.setData(10);
        r2.setData(6);
        r3.setData(-15);
        mlaExecutor.execute(stateContainer, false, false, null, null, r0, r1, r2, r3);
        assertEquals(45, r0.getData());
        r1.setData(65847685);
        r2.setData(456456);
        r3.setData(456456456);
        mlaExecutor.execute(stateContainer, false, false, null, null, r0, r1, r2, r3);
        assertEquals(846223408, r0.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        Register r3 = stateContainer.getRegister(3);
        r1.setData(10);
        r2.setData(-15);
        r3.setData(6);
        mlaExecutor.execute(stateContainer, false, true, null, null, r0, r1, r2, r3);
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        r1.setData(10);
        r2.setData(15);
        r3.setData(-6);
        mlaExecutor.execute(stateContainer, false, true, null, null, r0, r1, r2, r3);
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        r1.setData(10);
        r2.setData(1);
        r3.setData(-10);
        mlaExecutor.execute(stateContainer, false, true, null, null, r0, r1, r2, r3);
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
    }

}
