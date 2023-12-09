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
import fr.dwightstudio.jarmemu.sim.exceptions.StuckExecutionASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.LabelParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private BExecutor bExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        stateContainer.clearAndInitFiles(1);
        bExecutor = new BExecutor();
    }

    @Test
    public void simpleBTest() {
        Register pc = stateContainer.getPC();
        pc.setData(24);
        stateContainer.getAccessibleLabels().put("COUCOU", 20);
        Integer value =  new LabelParser().parse(stateContainer, "COUCOU");
        bExecutor.execute(stateContainer, false, false, null, null, value, null, null, null);
        assertEquals(20, pc.getData());
    }

    @Test
    public void BExceptionTest() {
        Register pc = stateContainer.getPC();
        pc.setData(24);
        stateContainer.getAccessibleLabels().put("COUCOU", 24);
        Integer value =  new LabelParser().parse(stateContainer, "COUCOU");
        assertThrows(StuckExecutionASMException.class, () -> bExecutor.execute(stateContainer, false, false, null, null, value, null, null, null));
    }

}
