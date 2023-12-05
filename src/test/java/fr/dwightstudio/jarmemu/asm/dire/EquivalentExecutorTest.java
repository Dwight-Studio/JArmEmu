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
import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class EquivalentExecutorTest extends JArmEmuTest {
    EquivalentExecutor EQUIVALENT = new EquivalentExecutor();
    StateContainer container;

    @BeforeEach
    void setUp() {
        container = new StateContainer();
    }

    @Test
    void normalTest() {
        Random random = new Random();
        for (int i = 0 ; i < 32 ; i++) {
            int r = random.nextInt();
            String s = RandomStringUtils.randomAlphabetic(i+1).toUpperCase();
            EQUIVALENT.apply(container, s + ", " + r, 0, Section.DATA);
            assertEquals(r, container.getConsts().get(s));
        }

        assertEquals(0, EQUIVALENT.computeDataLength(container, "HEY, 31", 0, Section.DATA));
    }

    @Test
    void failTest() {
        assertDoesNotThrow(() -> EQUIVALENT.computeDataLength(container, "HEY,", 0, Section.DATA));
        assertThrows(SyntaxASMException.class, () -> EQUIVALENT.apply(container, "HEY, p", 0, Section.DATA));
        assertThrows(SyntaxASMException.class, () -> EQUIVALENT.apply(container, "/, 3", 0, Section.DATA));
        assertThrows(SyntaxASMException.class, () -> EQUIVALENT.apply(container, ", 0", 0, Section.DATA));
    }
}