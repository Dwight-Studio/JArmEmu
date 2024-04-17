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

package fr.dwightstudio.jarmemu.asm.directive;

import fr.dwightstudio.jarmemu.asm.ParsedLabel;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.entity.FilePos;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class WordDirectiveTest extends DirectiveTest {
    public WordDirectiveTest() {
        super(WordDirective.class);
    }

    @Test
    void normalTest() throws ASMException {
        Random random = new Random();

        for (int i = 0 ; i < 32 ; i++) {
            int r = random.nextInt();
            execute(container,  Section.DATA, "" + r);
            assertEquals(r, container.getMemory().getWord(i*4));
        }

        execute(container,  Section.DATA, "'c'");
        assertEquals(99, container.getMemory().getWord(32*4));
    }

    @Test
    void constTest() throws ASMException {
        EquivalentDirective dir = new EquivalentDirective(Section.DATA, "N, 4");
        dir.contextualize(container);
        dir.execute(container);
        execute(container,  Section.DATA, "N");
        assertEquals(4, container.getMemory().getWord(0));
    }

    @Test
    void labelTest() throws ASMException {
        FilePos pos = new FilePos(0, 99);
        ParsedLabel l = new ParsedLabel(Section.NONE,"TEST");
        l.register(container, pos);
        execute(container,  Section.DATA, "=TEST");
        assertEquals(99, container.getMemory().getWord(0));
    }

    @Test
    void failTest() {
        assertDoesNotThrow(() -> execute(container,  Section.DATA, "12 * 1 * 9^4"));
        assertThrows(SyntaxASMException.class, () -> execute(container,  Section.BSS, "12 * 1 * 9^4"));
        assertThrows(SyntaxASMException.class, () -> execute(container,  Section.DATA, "HIHI"));
    }
}