package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.ParsedDirectiveLabel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class WordExecutorTest {

    WordExecutor WORD = new WordExecutor();
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
            WORD.apply(container, "" + r, i*4);
            assertEquals(r, container.memory.getWord(i*4));
        }

        WORD.apply(container, "'c'", 32*4);
        assertEquals(99, container.memory.getWord(32*4));
    }

    @Test
    void constTest() {
        DirectiveExecutors.EQUIVALENT.apply(container, "N, 4", 0);
        WORD.apply(container, "N", 100);
        assertEquals(4, container.memory.getWord(100));
    }

    @Test
    void labelTest() {
        ParsedDirectiveLabel l = new ParsedDirectiveLabel("TEST", Section.NONE);
        l.register(container, 99);
        WORD.apply(container, "=TEST", 100);
        assertEquals(99, container.memory.getWord(100));
    }

    @Test
    void failTest() {
        assertDoesNotThrow(() -> WORD.apply(container, "12 * 1 * 9^4", 0));
        assertThrows(SyntaxASMException.class, () -> WORD.apply(container, "HIHI", 0));
    }

}