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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class FillDirectiveTest extends DirectiveTest {
    public FillDirectiveTest() {
        super(FillDirective.class);
    }

    @BeforeEach
    void setUp() {
        super.setUp();
        for (int i = -1024 ; i < 1024 ; i += 4) {
            container.getMemory().putWord(i, -1);
        }

        container.getCurrentMemoryPos().setPos(0);
    }

    @Test
    void normalTest() throws ASMException {
        Random random = new Random();

        for (int i = 0 ; i < 32 ; i ++) {
            setUp();
            int r = random.nextInt(100);

            execute(container, Section.DATA, String.valueOf(r));

            for (int j = 0 ; j < r ; j++) {
                assertEquals((byte) 0, container.getMemory().getByte(j));
            }

            assertEquals((byte) 0xFF, container.getMemory().getByte(-4));
            assertEquals((byte) 0xFF, container.getMemory().getByte(r));
        }
    }

    @Test
    void oneSizeTest() throws ASMException {
        Random random = new Random();

        for (int i = 0 ; i < 32 ; i ++) {
            setUp();
            int r = random.nextInt(100);

            execute(container, Section.DATA, r + ", 0b00101111, 1");

            for (int j = 0 ; j < r ; j++) {
                assertEquals((byte) 0b00101111, container.getMemory().getByte(j));
            }

            assertEquals((byte) 0xFF, container.getMemory().getByte(-4));
            assertEquals((byte) 0xFF, container.getMemory().getByte(r));
        }
    }

    @Test
    void twoSizeTest() throws ASMException {
        Random random = new Random();

        for (int i = 0 ; i < 32 ; i ++) {
            setUp();
            int r = random.nextInt(100);

            execute(container, Section.DATA, r + ", 0b0010111100001010, 2");

            for (int j = 0 ; j < r ; j++) {
                if (j % 2 == 0) assertEquals((byte) 0b00101111, container.getMemory().getByte(j));
                if (j % 2 == 1) assertEquals((byte) 0b00001010, container.getMemory().getByte(j));
            }

            assertEquals((byte) 0xFF, container.getMemory().getByte(-4));
            assertEquals((byte) 0xFF, container.getMemory().getByte(r));
        }
    }

    @Test
    void threeSizeTest() throws ASMException {
        Random random = new Random();

        for (int i = 0 ; i < 32 ; i ++) {
            setUp();
            int r = random.nextInt(100);

            execute(container, Section.DATA, r + ", 0b001011110000101011111111, 3");

            for (int j = 0 ; j < r ; j++) {
                if (j % 3 == 0) assertEquals((byte) 0b00101111, container.getMemory().getByte(j));
                if (j % 3 == 1) assertEquals((byte) 0b00001010, container.getMemory().getByte(j));
                if (j % 3 == 2) assertEquals((byte) 0b11111111, container.getMemory().getByte(j));
            }

            assertEquals((byte) 0xFF, container.getMemory().getByte(-4));
            assertEquals((byte) 0xFF, container.getMemory().getByte(r));
        }
    }

    @Test
    void fourSizeTest() throws ASMException {
        Random random = new Random();

        for (int i = 0 ; i < 32 ; i ++) {
            setUp();
            int r = random.nextInt(100);

            execute(container, Section.DATA, r + ", 0b00101111000010101111111100000001, 4");

            for (int j = 0 ; j < r ; j++) {
                if (j % 4 == 0) assertEquals((byte) 0b00101111, container.getMemory().getByte(j));
                if (j % 4 == 1) assertEquals((byte) 0b00001010, container.getMemory().getByte(j));
                if (j % 4 == 2) assertEquals((byte) 0b11111111, container.getMemory().getByte(j));
                if (j % 4 == 3) assertEquals((byte) 0b00000001, container.getMemory().getByte(j));
            }

            assertEquals((byte) 0xFF, container.getMemory().getByte(-4));
            assertEquals((byte) 0xFF, container.getMemory().getByte(r));
        }
    }

    @Test
    void highSizeTest() throws ASMException {
        Random random = new Random();

        for (int i = 0 ; i < 32 ; i ++) {
            setUp();
            int r = random.nextInt(100);

            execute(container, Section.DATA, r + ", 0b00101111000010101111111100000001, 17");

            for (int j = 0 ; j < r ; j++) {
                if (j % 17 == 13) assertEquals((byte) 0b00101111, container.getMemory().getByte(j));
                if (j % 17 == 14) assertEquals((byte) 0b00001010, container.getMemory().getByte(j));
                if (j % 17 == 15) assertEquals((byte) 0b11111111, container.getMemory().getByte(j));
                if (j % 17 == 16) assertEquals((byte) 0b00000001, container.getMemory().getByte(j));
                if (j % 17 < 13) assertEquals((byte) 0b00000000, container.getMemory().getByte(j));
            }

            assertEquals((byte) 0xFF, container.getMemory().getByte(-4));
            assertEquals((byte) 0xFF, container.getMemory().getByte(r));
        }
    }

    @Test
    void higherSizeTest() throws ASMException {
        Random random = new Random();

        for (int i = 0 ; i < 32 ; i ++) {
            setUp();
            int r = random.nextInt(100);

            execute(container, Section.DATA, r + ", 0b00101111000010101111111100000001, 123");

            for (int j = 0 ; j < r ; j++) {
                assertEquals((byte) 0b00000000, container.getMemory().getByte(j));
            }

            assertEquals((byte) 0xFF, container.getMemory().getByte(-4));
            assertEquals((byte) 0xFF, container.getMemory().getByte(r));
        }
    }

    @Test
    void failTest() {
        assertThrows(SyntaxASMException.class, () -> execute(container, Section.BSS, "1, 2, 123"));
        assertDoesNotThrow(() -> execute(container, Section.BSS, "1, 0, 123"));
    }
}