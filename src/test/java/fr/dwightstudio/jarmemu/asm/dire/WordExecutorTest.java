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

import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.FilePos;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.ParsedDirectiveLabel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class WordExecutorTest {

    WordExecutor WORD = new WordExecutor();
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
            FilePos pos = new FilePos(0, i*4);
            WORD.apply(container, "" + r, pos, Section.DATA);
            assertEquals(r, container.getMemory().getWord(i*4));
        }

        FilePos pos = new FilePos(0, 32*4);
        WORD.apply(container, "'c'", pos, Section.DATA);
        assertEquals(99, container.getMemory().getWord(32*4));
    }

    @Test
    void constTest() {
        FilePos pos = new FilePos(0, 100);
        DirectiveExecutors.EQUIVALENT.apply(container, "N, 4", FilePos.ZERO, Section.DATA);
        WORD.apply(container, "N", pos, Section.DATA);
        assertEquals(4, container.getMemory().getWord(100));
    }

    @Test
    void labelTest() {
        FilePos pos = new FilePos(0, 100);
        ParsedDirectiveLabel l = new ParsedDirectiveLabel("TEST", Section.NONE);
        l.register(container, 99);
        WORD.apply(container, "=TEST", pos, Section.DATA);
        assertEquals(99, container.getMemory().getWord(100));
    }

    @Test
    void failTest() {
        assertDoesNotThrow(() -> WORD.apply(container, "12 * 1 * 9^4", FilePos.ZERO.clone(), Section.DATA));
        assertThrows(SyntaxASMException.class, () -> WORD.apply(container, "HIHI", FilePos.ZERO.clone(), Section.DATA));
    }

}