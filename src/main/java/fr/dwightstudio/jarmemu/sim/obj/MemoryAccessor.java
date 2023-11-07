package fr.dwightstudio.jarmemu.sim.obj;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class MemoryAccessor {

    private final HashMap<Integer, Byte> memory;

    public MemoryAccessor() {
        memory = new HashMap<>();
    }

    public byte getByte(int add) {
        return memory.getOrDefault(add, (byte) 0);
        //return (byte) (((add/4) >> ((3 - (add % 4)) * 8)) & 0xFF); // Retourne le numéro du Word, pour tester
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
        memory.put(add, b);
    }

    public void putHalf(int add, short s) {
        byte[] bytes = new byte[2];
        ByteBuffer.wrap(bytes).putShort(s);

        for (int i = 0; i < bytes.length; i++) {
            memory.put(add + i, bytes[i]);
        }
    }

    public void putWord(int add, int i) {
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).putInt(i);

        for (int j = 0; j < bytes.length; j++) {
            memory.put(add + j, bytes[j]);
        }
    }

    public void clear() {
        memory.clear();
    }

    public boolean isByteInitiated(int address) {
        return memory.get(address) != null;
    }

    public boolean isHalfInitiated(int address) {
        return isByteInitiated(address) || isByteInitiated(address + 1);
    }

    public boolean isWordInitiated(int address) {
        return isHalfInitiated(address) || isHalfInitiated(address + 2);
    }
}
