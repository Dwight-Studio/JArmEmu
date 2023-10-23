package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LabelParserTest {

    private StateContainer stateContainer;
    private static final LabelParser LABEL = new LabelParser();

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    public void parseTest(){
        stateContainer.symbols.put("COUCOU", 23);

        assertEquals(0b000000000000000000010111, LABEL.parse(stateContainer, "COUCOU:"));
        assertThrows(AssemblySyntaxException.class, () -> LABEL.parse(stateContainer, "PASCOUCOU:"));
    }

}
