package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Value8OrRegisterParserTest {
    private StateContainer stateContainer;
    private static final Value8OrRegisterParser VALUE_OR_REGISTER = new Value8OrRegisterParser();

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();

        for (int i = 0 ; i < 16 ; i++) {
            stateContainer.registers[i].setData(i);
        }

        stateContainer.cpsr.setData(16);
        stateContainer.spsr.setData(17);
    }

    @Test
    public void valueTest() {
        assertEquals(48, VALUE_OR_REGISTER.parse(stateContainer, "#48"));
        assertEquals(-48, VALUE_OR_REGISTER.parse(stateContainer, "#-48"));
        assertEquals(1, VALUE_OR_REGISTER.parse(stateContainer, "#0b01"));
        assertEquals(8, VALUE_OR_REGISTER.parse(stateContainer, "#0010"));
        assertEquals(16, VALUE_OR_REGISTER.parse(stateContainer, "#0x010"));

        assertThrows(AssemblySyntaxException.class, () -> VALUE_OR_REGISTER.parse(stateContainer, "#R14"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE_OR_REGISTER.parse(stateContainer, "#0xR14"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE_OR_REGISTER.parse(stateContainer, "#LR"));
    }

    @Test
    public void registerTest() {
        for (int i = 0 ; i < 16 ; i++) {
            assertEquals(i, VALUE_OR_REGISTER.parse(stateContainer, "R" + i));
        }

        assertEquals(13, VALUE_OR_REGISTER.parse(stateContainer, "SP"));
        assertEquals(14, VALUE_OR_REGISTER.parse(stateContainer, "LR"));
        assertEquals(15, VALUE_OR_REGISTER.parse(stateContainer, "PC"));

        assertEquals(16, VALUE_OR_REGISTER.parse(stateContainer, "CPSR"));
        assertEquals(17, VALUE_OR_REGISTER.parse(stateContainer, "SPSR"));

        assertThrows(AssemblySyntaxException.class, () -> VALUE_OR_REGISTER.parse(stateContainer, "R16"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE_OR_REGISTER.parse(stateContainer, "48"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE_OR_REGISTER.parse(stateContainer, "1R"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE_OR_REGISTER.parse(stateContainer, "4LR"));
    }
}
