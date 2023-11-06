package fr.dwightstudio.jarmemu.sim.args;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImmParserTest {

    private StateContainer stateContainer;
    private static final ImmParser VALUE8 = new ImmParser();

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    public void OverflowTest() {
        assertEquals(255, VALUE8.parse(stateContainer, "#255"));
        //assertThrows(SyntaxASMException.class, () -> VALUE8.parse(stateContainer, "#-132"));
        //assertThrows(SyntaxASMException.class, () -> VALUE8.parse(stateContainer, "#1023"));
        //assertThrows(SyntaxASMException.class, () -> VALUE8.parse(stateContainer, "#256"));
    }
}
