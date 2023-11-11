package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ASCIZExecutorTest {

    ASCIZExecutor ASCIZ = new ASCIZExecutor();
    StateContainer container;

    @BeforeEach
    void setUp() {
        container = new StateContainer();
    }

    @Test
    void normalTest() {
        for (int i = 0 ; i < 32 ; i++) {
            String string = RandomStringUtils.randomAlphanumeric(32);

            ASCIZ.apply(container, "\"" + string + "\"", 0);

            for (int j = 0; j < 32; j++) {
                assertEquals(string.charAt(j), container.memory.getByte(j));
            }
            assertEquals('\0', container.memory.getByte(32));
        }
    }

    @Test
    void failTest() {
        assertDoesNotThrow(() -> ASCIZ.apply(container, "\"'\"", 0));
        assertDoesNotThrow(() -> ASCIZ.apply(container, "'\"'", 0));
        assertThrows(SyntaxASMException.class, () -> ASCIZ.apply(container, "Hey", 0));
        assertThrows(SyntaxASMException.class, () -> ASCIZ.apply(container, "\"\"\"", 0));
        assertThrows(SyntaxASMException.class, () -> ASCIZ.apply(container, "'''", 0));
        assertThrows(SyntaxASMException.class, () -> ASCIZ.apply(container, "\"\"\"", 0));
    }

}