package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SpaceExecutorTest extends JArmEmuTest {

    SpaceExecutor SPACE = new SpaceExecutor();
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
            assertEquals(r, SPACE.computeDataLength(container,"" + r, 0));
        }
    }

    void failTest() {
        assertDoesNotThrow(() -> SPACE.apply(container, "", 0));
        assertThrows(SyntaxASMException.class, () -> SPACE.apply(container, "ODdad$ù", 0));
    }
}