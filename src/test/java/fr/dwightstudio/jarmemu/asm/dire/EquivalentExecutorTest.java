package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class EquivalentExecutorTest extends JArmEmuTest {
    EquivalentExecutor EQUIVALENT = new EquivalentExecutor();
    StateContainer container;

    @BeforeEach
    void setUp() {
        container = new StateContainer();
    }

    @Test
    void normalTest() {
        Random random = new Random();
        for (int i = 0 ; i < 32 ; i++) {
            int r = random.nextInt();
            String s = RandomStringUtils.randomAlphabetic(i+1).toUpperCase();
            EQUIVALENT.apply(container, s + ", " + r, 0);
            assertEquals(r, container.consts.get(s));
        }

        assertEquals(0, EQUIVALENT.computeDataLength(container, "HEY, 31", 0));
    }

    @Test
    void failTest() {
        assertDoesNotThrow(() -> EQUIVALENT.computeDataLength(container, "HEY,", 0));
        assertThrows(SyntaxASMException.class, () -> EQUIVALENT.apply(container, "HEY, p", 0));
        assertThrows(SyntaxASMException.class, () -> EQUIVALENT.apply(container, "/, 3", 0));
        assertThrows(SyntaxASMException.class, () -> EQUIVALENT.apply(container, ", 0", 0));
    }
}