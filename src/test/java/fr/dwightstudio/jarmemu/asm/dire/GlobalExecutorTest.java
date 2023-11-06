package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExecutorTest extends JArmEmuTest {

    GlobalExecutor GLOBAL = new GlobalExecutor();
    StateContainer container;

    @BeforeEach
    void setUp() {
        container = new StateContainer();
    }

    @Test
    void normalTest() {
        GLOBAL.apply(container, "EXEMPLE", 0);
        assertEquals("EXEMPLE", container.globals.get(0));
        assertEquals(0, GLOBAL.computeDataLength("EXEMPLE", 0));
    }

    @Test
    void failTest() {
        assertDoesNotThrow(() -> GLOBAL.apply(container, "AHHHHHHHHHHHHHHH", 0));
        assertThrows(SyntaxASMException.class, () -> GLOBAL.apply(container, "", 0));
        assertThrows(SyntaxASMException.class, () -> GLOBAL.apply(container, "/.", 0));
    }
}