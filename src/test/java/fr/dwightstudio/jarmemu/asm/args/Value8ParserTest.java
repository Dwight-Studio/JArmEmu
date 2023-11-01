package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Value8ParserTest {

    private StateContainer stateContainer;
    private static final Value8Parser VALUE8 = new Value8Parser();

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    public void OverflowTest() {
        assertEquals(255, VALUE8.parse(stateContainer, "#255"));
        assertThrows(SyntaxASMException.class, () -> VALUE8.parse(stateContainer, "#-132"));
        assertThrows(SyntaxASMException.class, () -> VALUE8.parse(stateContainer, "#1023"));
        assertThrows(SyntaxASMException.class, () -> VALUE8.parse(stateContainer, "#256"));
    }
}
