package fr.dwightstudio.jarmemu.sim.obj;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class MemoryAccessor {

    private final HashMap<Integer, IntegerProperty> memory;

    public MemoryAccessor() {
        memory = new HashMap<>();
    }

    public byte getByte(int add) {
        int aAdd = Math.floorDiv(add, 4);
        int rAdd = (add % 4) < 0 ? 4 + (add % 4) : (add % 4);

        if (memory.containsKey(aAdd)) {
            byte[] bytes = new byte[4];
            ByteBuffer.wrap(bytes).putInt(0, memory.get(aAdd).get());
            return bytes[rAdd];
        } else {
            return 0;
        }
    }

    public short getHalf(int add) {
        byte[] bytes = new byte[]{getByte(add), getByte(add + 1)};
        return ByteBuffer.wrap(bytes).getShort();
    }

    public int getWord(int add) {
        byte[] bytes = new byte[]{getByte(add), getByte(add + 1), getByte(add + 2), getByte(add + 3)};
        return ByteBuffer.wrap(bytes).getInt();
    }
    public void putByte(int add, byte b) {
        int aAdd = Math.floorDiv(add, 4);
        int rAdd = (add % 4) < 0 ? 4 + (add % 4) : (add % 4);
        byte[] bytes = new byte[4];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        IntegerProperty property;

        //System.out.println(add + " " + aAdd);

        if (memory.containsKey(aAdd)) {
            property = memory.get(aAdd);
            buffer.putInt(0, property.get());
        } else {
            property = new SimpleIntegerProperty(0);
            memory.put(aAdd, property);
        }

        buffer.put(rAdd, b);
        property.set(buffer.getInt(0));
    }

    public void putHalf(int add, short s) {
        byte[] bytes = new byte[2];
        ByteBuffer.wrap(bytes).putShort(s);

        for (int i = 0; i < bytes.length; i++) {
            putByte(add + i, bytes[i]);
        }
    }

    public void putWord(int add, int i) {
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).putInt(i);

        for (int j = 0; j < bytes.length; j++) {
            putByte(add + j, bytes[j]);
        }
    }

    public void clear() {
        memory.clear();
    }

    public boolean isByteInitiated(int address) {
        return memory.get(address - (address % 4)) != null;
    }

    public boolean isHalfInitiated(int address) {
        return isByteInitiated(address - (address % 4)) || isByteInitiated(address - (address % 4) + 1);
    }

    public boolean isWordInitiated(int address) {
        return isHalfInitiated(address - (address % 4)) || isHalfInitiated(address - (address % 4) + 2);
    }

    public IntegerProperty getProperty(int address) {
        return memory.get(address - (address % 4));
    }

}
