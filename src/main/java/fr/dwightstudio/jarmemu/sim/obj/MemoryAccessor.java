package fr.dwightstudio.jarmemu.sim.obj;

import java.util.HashMap;

public class MemoryAccessor {

    private final HashMap<Integer, Byte> memory;

    public MemoryAccessor() {
        memory = new HashMap<>();
    }

    public byte get(int add) {
        return memory.getOrDefault(add, (byte) 0);
    }

    public byte put(int add, byte b) {
        Byte rtn = memory.put(add, b);
        return rtn == null ? 0 : rtn;
    }

    public void clear() {
        memory.clear();
    }
}
