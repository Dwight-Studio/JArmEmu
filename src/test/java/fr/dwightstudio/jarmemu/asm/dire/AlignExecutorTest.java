package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class AlignExecutorTest extends JArmEmuTest {

    AlignExecutor ALIGN = new AlignExecutor();
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
            assertEquals(0, (ALIGN.computeDataLength(container, "", r) + r) % 4);
        }
    }

    @Test
    void failTest() {
        assertDoesNotThrow(() -> ALIGN.apply(container, "", 0));
        assertThrows(SyntaxASMException.class, () -> ALIGN.apply(container, "HIHI", 0));
    }
}