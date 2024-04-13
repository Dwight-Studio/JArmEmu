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

package fr.dwightstudio.jarmemu.sim.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemoryAccessorTest {

    MemoryAccessor memoryAccessor;

    @BeforeEach
    public void setUp() {
        memoryAccessor = new MemoryAccessor();
    }

    @Test
    public void allTest() {
        Random random = new Random();

        for (int i = 0 ; i < 1024 ; i++) {
            int add = random.nextInt();
            int val = random.nextInt();

            memoryAccessor.putWord(add, val);
            assertEquals(val, memoryAccessor.getWord(add));
        }

        for (int i = 0 ; i < 1024 ; i++) {
            int add = random.nextInt();
            short val = (short) (random.nextInt(Short.MIN_VALUE, Short.MAX_VALUE + 1) & 0xFFFF);

            memoryAccessor.putHalf(add, val);
            assertEquals(val, memoryAccessor.getHalf(add));
        }

        for (int i = 0 ; i < 1024 ; i++) {
            int add = random.nextInt();
            byte val = (byte) (random.nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE + 1) & 0xFF);

            memoryAccessor.putByte(add, val);
            assertEquals(val, memoryAccessor.getByte(add));
        }
    }

}