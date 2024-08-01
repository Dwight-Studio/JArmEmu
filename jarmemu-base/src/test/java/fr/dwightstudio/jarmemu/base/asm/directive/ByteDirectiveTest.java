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

package fr.dwightstudio.jarmemu.base.asm.directive;

import fr.dwightstudio.jarmemu.base.asm.Section;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ByteDirectiveTest extends DirectiveTest {
    public ByteDirectiveTest() {
        super(ByteDirective.class);
    }

    @Test
    void normalTest() throws ASMException {
        for (int i = 0 ; i < 32 ; i++) {
            Random random = new Random();

            for (int j = 0; j < 32; j++) {
                byte[] b = new byte[1];
                random.nextBytes(b);
                container.getCurrentMemoryPos().setPos(j);
                execute(container, Section.DATA, "" + (b[0] & 0xFF));
                assertEquals(b[0], container.getMemory().getByte(j));
            }
        }
    }

    @Test
    void failTest() {
        assertDoesNotThrow(() -> execute(container, Section.DATA, "0"));
        assertDoesNotThrow(() -> execute(container, Section.DATA, "127"));
        assertDoesNotThrow(() -> execute(container, Section.DATA, "0b101"));
        assertDoesNotThrow(() -> execute(container, Section.BSS, ""));
        assertThrows(SyntaxASMException.class, () -> execute(container, Section.DATA, "256"));
        assertThrows(SyntaxASMException.class, () -> execute(container, Section.DATA, "dq"));
        assertThrows(SyntaxASMException.class, () -> execute(container, Section.DATA, "0xFFF"));
        assertThrows(SyntaxASMException.class, () -> execute(container, Section.BSS, "0xFFF"));
    }
}