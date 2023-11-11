package fr.dwightstudio.jarmemu.sim.parse.args;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImmRegisterParserTest extends JArmEmuTest {
    private StateContainer stateContainer;
    private static final ImmOrRegisterParser VALUE_OR_REGISTER = new ImmOrRegisterParser();

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
        assertEquals(1, VALUE_OR_REGISTER.parse(stateContainer, "#0B01"));
        assertEquals(8, VALUE_OR_REGISTER.parse(stateContainer, "#0010"));
        assertEquals(16, VALUE_OR_REGISTER.parse(stateContainer, "#0X010"));

        assertThrows(SyntaxASMException.class, () -> VALUE_OR_REGISTER.parse(stateContainer, "#R14"));
        assertThrows(SyntaxASMException.class, () -> VALUE_OR_REGISTER.parse(stateContainer, "#0XR14"));
        assertThrows(SyntaxASMException.class, () -> VALUE_OR_REGISTER.parse(stateContainer, "#LR"));
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

        assertThrows(SyntaxASMException.class, () -> VALUE_OR_REGISTER.parse(stateContainer, "R16"));
        assertThrows(SyntaxASMException.class, () -> VALUE_OR_REGISTER.parse(stateContainer, "48"));
        assertThrows(SyntaxASMException.class, () -> VALUE_OR_REGISTER.parse(stateContainer, "1R"));
        assertThrows(SyntaxASMException.class, () -> VALUE_OR_REGISTER.parse(stateContainer, "4LR"));
    }
}
