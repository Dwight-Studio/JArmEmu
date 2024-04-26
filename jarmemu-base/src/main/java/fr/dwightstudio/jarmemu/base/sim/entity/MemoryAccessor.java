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

package fr.dwightstudio.jarmemu.base.sim.entity;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.HashSet;

public class MemoryAccessor {

    private final HashMap<Integer, IntegerProperty> memory;
    private final HashSet<Integer> initiationSet;

    public MemoryAccessor() {
        memory = new HashMap<>();
        initiationSet = new HashSet<>();
    }

    public byte getByte(int add) {
        int aAdd = Math.floorDiv(add, 4);
        int rAdd = (add % 4) < 0 ? 4 + (add % 4) : (add % 4);

        if (memory.containsKey(aAdd)) {
            byte[] bytes = new byte[4];
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).putInt(0, memory.get(aAdd).get());
            return bytes[rAdd];
        } else {
            return 0;
        }
    }

    public short getHalf(int add) {
        byte[] bytes = new byte[]{getByte(add), getByte(add + 1)};
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    public int getWord(int add) {
        byte[] bytes = new byte[]{getByte(add), getByte(add + 1), getByte(add + 2), getByte(add + 3)};
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    public void putByte(int add, byte b) {
        int aAdd = Math.floorDiv(add, 4);
        int rAdd = (add % 4) < 0 ? 4 + (add % 4) : (add % 4);
        byte[] bytes = new byte[4];
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        IntegerProperty property;

        if (memory.containsKey(aAdd)) {
            property = memory.get(aAdd);
            buffer.putInt(0, property.get());
        } else {
            property = new SimpleIntegerProperty(0);
            memory.put(aAdd, property);
        }

        buffer.put(rAdd, b);
        property.set(buffer.getInt(0));
        initiationSet.add(add);
    }

    public void putHalf(int add, short s) {
        byte[] bytes = new byte[2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).putShort(s);

        for (int i = 0; i < bytes.length; i++) {
            putByte(add + i, bytes[i]);
        }
    }

    public void putWord(int add, int i) {
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).putInt(i);

        for (int j = 0; j < bytes.length; j++) {
            putByte(add + j, bytes[j]);
        }
    }

    public void clear() {
        memory.clear();
    }

    public boolean isByteInitiated(int address) {
        return initiationSet.contains(address);
    }

    public boolean isHalfInitiated(int address) {
        return isByteInitiated(address - (address % 4)) || isByteInitiated(address - (address % 4) + 1);
    }

    public boolean isWordInitiated(int address) {
        return isHalfInitiated(address - (address % 4)) || isHalfInitiated(address - (address % 4) + 2);
    }

    public IntegerProperty getProperty(int address) {
        int aAdd = Math.floorDiv(address, 4);
        if (memory.containsKey(aAdd)) {
            return memory.get(aAdd);
        } else {
            IntegerProperty property = new SimpleIntegerProperty(0);
            memory.put(aAdd, property);
            return property;
        }
    }

    public void putAll(MemoryAccessor acc) {
        acc.initiationSet.forEach(add -> putByte(add, acc.getByte(add)));
    }
}
