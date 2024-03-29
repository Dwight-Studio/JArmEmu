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
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CMNExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private CMNExecutor cmnExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        cmnExecutor = new CMNExecutor();
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        r0.setData(5);
        r1.setData(3);
        cmnExecutor.execute(stateContainer, false, false, null, null, r0, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        assertFalse(stateContainer.getCPSR().getV());
        r0.setData(-5);
        r1.setData(5);
        cmnExecutor.execute(stateContainer, false, false, null, null, r0, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        assertFalse(stateContainer.getCPSR().getV());
        r0.setData(3);
        r1.setData(-5);
        cmnExecutor.execute(stateContainer, false, false, null, null, r0, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        assertFalse(stateContainer.getCPSR().getV());
        r0.setData(0b01111111111111111111111111111111);
        r1.setData(1);
        cmnExecutor.execute(stateContainer, false, false, null, null, r0, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        assertFalse(stateContainer.getCPSR().getC());
        assertTrue(stateContainer.getCPSR().getV());
        r0.setData(0b10000000000000000000000000000000);
        r1.setData(0b10000000000000000000000000000000);
        cmnExecutor.execute(stateContainer, false, false, null, null, r0, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
        assertTrue(stateContainer.getCPSR().getC());
        assertTrue(stateContainer.getCPSR().getV());
    }

}