package fr.dwightstudio.jarmemu.sim.parse.args;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImmParserTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private static final ImmParser IMM = new ImmParser();

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    public void normalTest() {
        assertEquals(2047, IMM.parse(stateContainer, "#2047"));
        assertEquals(256, IMM.parse(stateContainer, "#256"));
        assertEquals(-2048, IMM.parse(stateContainer, "#-2048"));
        assertEquals(0, IMM.parse(stateContainer, "#00000"));
    }

    @Test
    public void overflowTest() {
        assertThrows(SyntaxASMException.class, () -> IMM.parse(stateContainer, "#-2049"));
        assertThrows(SyntaxASMException.class, () -> IMM.parse(stateContainer, "#4096"));
        assertThrows(SyntaxASMException.class, () -> IMM.parse(stateContainer, "#2048"));
        assertThrows(SyntaxASMException.class, () -> IMM.parse(stateContainer, "#4096"));
    }
}
