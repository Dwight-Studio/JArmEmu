package fr.dwightstudio.jarmemu.sim.obj;

import java.util.HashMap;

public class MemoryAccessor {

    private final HashMap<Integer, Byte> memory;

    public MemoryAccessor() {
        memory = new HashMap<>();
    }

    public byte get(int add) {
        return memory.getOrDefault(add, (byte) 0);
        //return (byte) (((add/4) >> ((3 - (add % 4)) * 8)) & 0xFF); // Retourne le numéro du Word, pour tester
    }

    public byte put(int add, byte b) {
        Byte rtn = memory.put(add, b);
        return rtn == null ? 0 : rtn;
    }

    public void clear() {
        memory.clear();
    }
}
