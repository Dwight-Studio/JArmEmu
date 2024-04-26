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

package fr.dwightstudio.jarmemu.base.util;

import java.nio.ByteBuffer;

public class MathUtils {

    public static boolean hasCarry(int num1, int num2) {
        // Perform the addition
        int sum = num1 + num2;

        // Check for carry
        return ((num1 & num2) | ((num1 | num2) & ~sum)) < 0;
    }

    public static boolean hasOverflow(int a, int b) {
        int r = a + b;
        // Overflow if both arguments have the opposite sign of the result
        return ((a ^ r) & (b ^ r)) < 0;
    }

    public static int toWord(Byte byte3, Byte byte2, Byte byte1, Byte byte0) {
        return ByteBuffer.wrap(new byte[]{byte3, byte2, byte1, byte0}).getInt();
    }

    public static String toBinString(byte b) {
        StringBuilder rtn = new StringBuilder();

        for (int i = 0 ; i < 8 ; i++) {
            rtn.append((b >> (7 - i)) & 1);
        }

        return rtn.toString();
    }
}
