/*
 *             ____           _       __    __     _____ __            ___
 *            / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *           / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *          / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *         /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                          /____/
 *      Copyright (C) 2024 Dwight Studio
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.dwightstudio.jarmemu.base.util;

public class WordUtils {
    public static int toUnsignedInt(long number) {
        return (int) (number & 0xFFFFFFFFL);
    }

    public static boolean overflows(long number, int bitNumber) {
        int min = -(1 << (bitNumber - 1));
        int max = (1 << bitNumber) - 1; // We want to allow signed and unsigned

        return number > max || number < min;
    }
}
