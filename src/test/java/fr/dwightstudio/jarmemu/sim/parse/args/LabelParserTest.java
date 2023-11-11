package fr.dwightstudio.jarmemu.sim.parse.args;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.LabelParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LabelParserTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private static final LabelParser LABEL = new LabelParser();

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    public void parseTest(){
        stateContainer.labels.put("COUCOU", 23);

        assertEquals(23, LABEL.parse(stateContainer, "COUCOU"));
        assertThrows(SyntaxASMException.class, () -> LABEL.parse(stateContainer, "PASCOUCOU:"));
    }

}
