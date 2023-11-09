package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class HalfExecutorTest {

    HalfExecutor HALF = new HalfExecutor();
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
            HALF.apply(container, "" + (r & 0xFFFF), i*2);
            assertEquals((short) (r & 0xFFFF), container.memory.getHalf(i*2));
        }
    }

    @Test
    void failTest() {
        assertDoesNotThrow(() -> HALF.apply(container, "12", 0));
        assertThrows(SyntaxASMException.class, () -> HALF.apply(container, "HIHI", 0));
    }

}