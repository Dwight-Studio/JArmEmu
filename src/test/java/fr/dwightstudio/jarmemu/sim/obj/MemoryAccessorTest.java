package fr.dwightstudio.jarmemu.sim.obj;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MemoryAccessorTest {

    MemoryAccessor memoryAccessor;

    @BeforeEach
    public void setUp() {
        memoryAccessor = new MemoryAccessor();
    }

    @Test
    public void allTest() {
        Random random = new Random();

        for (int i = 0 ; i < 100000 ; i++) {
            int add = random.nextInt();
            int val = random.nextInt();

            memoryAccessor.putWord(add, val);
            assertEquals(val, memoryAccessor.getWord(add));
        }

        for (int i = 0 ; i < 100000 ; i++) {
            int add = random.nextInt();
            short val = (short) (random.nextInt(Short.MIN_VALUE, Short.MAX_VALUE + 1) & 0xFFFF);

            memoryAccessor.putHalf(add, val);
            assertEquals(val, memoryAccessor.getHalf(add));
        }

        for (int i = 0 ; i < 100000 ; i++) {
            int add = random.nextInt();
            byte val = (byte) (random.nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE + 1) & 0xFF);

            memoryAccessor.putByte(add, val);
            assertEquals(val, memoryAccessor.getByte(add));
        }
    }

}