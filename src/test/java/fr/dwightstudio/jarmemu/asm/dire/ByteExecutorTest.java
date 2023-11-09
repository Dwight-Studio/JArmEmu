package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ByteExecutorTest {

    ByteExecutor BYTE = new ByteExecutor();
    StateContainer container;

    @BeforeEach
    void setUp() {
        container = new StateContainer();
    }

    @Test
    void normalTest() {
        for (int i = 0 ; i < 32 ; i++) {
            Random random = new Random();

            for (int j = 0; j < 32; j++) {
                byte[] b = new byte[1];
                random.nextBytes(b);
                BYTE.apply(container, "" + (b[0] & 0xFF), j);
                assertEquals(b[0], container.memory.getByte(j));
            }
        }
    }

    @Test
    void failTest() {
        assertDoesNotThrow(() -> BYTE.apply(container, "0", 0));
        assertDoesNotThrow(() -> BYTE.apply(container, "127", 0));
        assertDoesNotThrow(() -> BYTE.apply(container, "0b101", 0));
        assertThrows(SyntaxASMException.class, () -> BYTE.apply(container, "256", 0));
        assertThrows(SyntaxASMException.class, () -> BYTE.apply(container, "dq", 0));
        assertThrows(SyntaxASMException.class, () -> BYTE.apply(container, "0xFFF", 0));
        assertThrows(SyntaxASMException.class, () -> BYTE.apply(container, "", 0));
    }

}