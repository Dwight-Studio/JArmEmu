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
import fr.dwightstudio.jarmemu.asm.exception.StuckExecutionASMException;
import fr.dwightstudio.jarmemu.sim.entity.Register;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BXExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private BXExecutor bxExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        bxExecutor = new BXExecutor();
    }

    @Test
    public void simpleBxTest() {
        Register lr = stateContainer.getRegister(0);
        Register pc = stateContainer.getPC();
        lr.setData(24);
        pc.setData(48);
        bxExecutor.execute(stateContainer, false, false, null, null, lr, null, null, null);
        assertEquals(24, pc.getData());
        assertFalse(stateContainer.getCPSR().getT());
        lr.setData(45);
        bxExecutor.execute(stateContainer, false, false, null, null, lr, null, null, null);
        assertEquals(45, pc.getData());
        assertTrue(stateContainer.getCPSR().getT());
    }

    @Test
    public void BxExceptionTest() {
        Register pc = stateContainer.getPC();
        assertThrows(StuckExecutionASMException.class, () -> bxExecutor.execute(stateContainer, false, false, null, null, pc, null, null, null));
    }

}
