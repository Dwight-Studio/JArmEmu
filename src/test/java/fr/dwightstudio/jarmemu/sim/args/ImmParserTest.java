package fr.dwightstudio.jarmemu.sim.args;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImmParserTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private static final ImmParser VALUE8 = new ImmParser();

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    public void normalTest() {
        assertEquals(2048, VALUE8.parse(stateContainer, "#2048"));
        assertEquals(256, VALUE8.parse(stateContainer, "#256"));
        assertEquals(4095, VALUE8.parse(stateContainer, "#0XFFF"));
        assertEquals(0, VALUE8.parse(stateContainer, "#00000"));
    }

    @Test
    public void overflowTest() {
        assertThrows(SyntaxASMException.class, () -> VALUE8.parse(stateContainer, "#-132"));
        assertThrows(SyntaxASMException.class, () -> VALUE8.parse(stateContainer, "#4096"));
        assertThrows(SyntaxASMException.class, () -> VALUE8.parse(stateContainer, "#-2048"));
        assertThrows(SyntaxASMException.class, () -> VALUE8.parse(stateContainer, "#4096"));
    }
}
