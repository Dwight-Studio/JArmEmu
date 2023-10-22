package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Value8ParserTest {

    private StateContainer stateContainer;
    private static final Value8Parser VALUE8 = new Value8Parser();

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    public void OverflowTest() {
        assertEquals(127, VALUE8.parse(stateContainer, "#127"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE8.parse(stateContainer, "#-132"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE8.parse(stateContainer, "#128"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE8.parse(stateContainer, "#256"));
    }
}
