package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ASCIIExecutorTest {

    ASCIIExecutor ASCII = new ASCIIExecutor();
    StateContainer container;

    @BeforeEach
    void setUp() {
        container = new StateContainer();
    }

    @Test
    void normalTest() {
        for (int i = 0 ; i < 32 ; i++) {
            String string = RandomStringUtils.randomAlphanumeric(32);

            ASCII.apply(container, "\"" + string + "\"", 0);

            for (int j = 0; j < 32; j++) {
                assertEquals(string.charAt(j), container.memory.getByte(j));
            }
            assertEquals(0, container.memory.getByte(32));
        }
    }

    @Test
    void failTest() {
        assertDoesNotThrow(() -> ASCII.apply(container, "\"'\"", 0));
        assertDoesNotThrow(() -> ASCII.apply(container, "'\"'", 0));
        assertThrows(SyntaxASMException.class, () -> ASCII.apply(container, "Hey", 0));
        assertThrows(SyntaxASMException.class, () -> ASCII.apply(container, "\"\"\"", 0));
        assertThrows(SyntaxASMException.class, () -> ASCII.apply(container, "'''", 0));
        assertThrows(SyntaxASMException.class, () -> ASCII.apply(container, "\"\"\"", 0));
    }
}