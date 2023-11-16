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

package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExecutorTest extends JArmEmuTest {

    GlobalExecutor GLOBAL = new GlobalExecutor();
    StateContainer container;

    @BeforeEach
    void setUp() {
        container = new StateContainer();
    }

    @Test
    void normalTest() {
        GLOBAL.apply(container, "ExEMpLE", 0);
        assertEquals("EXEMPLE", container.getGlobal());
        assertEquals(0, GLOBAL.computeDataLength(container,"EXEMPLE", 0));
    }

    @Test
    void failTest() {
        assertDoesNotThrow(() -> GLOBAL.apply(container, "AHHHHHHHHHHHHHHH", 0));
        assertThrows(SyntaxASMException.class, () -> GLOBAL.apply(container, "", 0));
        assertThrows(SyntaxASMException.class, () -> GLOBAL.apply(container, "/.", 0));
    }
}