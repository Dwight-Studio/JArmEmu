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

package fr.dwightstudio.jarmemu.base.util.converters;

import javafx.util.StringConverter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class WordASCIIStringConverter extends StringConverter<Number> {
    @Override
    public String toString(Number number) {
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).putInt((Integer) number);

        String s0 = new String(new byte[]{bytes[0]}, StandardCharsets.UTF_8).replace('\0', '_');
        String s1 = new String(new byte[]{bytes[1]}, StandardCharsets.UTF_8).replace('\0', '_');
        String s2 = new String(new byte[]{bytes[2]}, StandardCharsets.UTF_8).replace('\0', '_');
        String s3 = new String(new byte[]{bytes[3]}, StandardCharsets.UTF_8).replace('\0', '_');

        return s0 + s1 + s2 + s3;
    }

    @Override
    public Number fromString(String s) {

        switch (s.length()) {
            case 0 -> s = "\0\0\0\0";
            case 1 -> s = s + "\0\0\0";
            case 2 -> s = s + "\0\0";
            case 3 -> s = s + "\0";
            default -> s = s.substring(0,4);
        }

        return ByteBuffer.wrap(s.getBytes(StandardCharsets.UTF_8)).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
}
