package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterWithUpdateParserTest {

    private StateContainer stateContainer;
    private static final RegisterWithUpdateParser REGISTER_WITH_UPDATE = new RegisterWithUpdateParser();

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    public void normalTest() {
        stateContainer.registers[0].setData(7430223);

        RegisterWithUpdateParser.UpdatableRegister reg = REGISTER_WITH_UPDATE.parse(stateContainer, "R0");
        assertEquals(stateContainer.registers[0].getData(), reg.getData());

        reg.update(3);

        assertEquals(7430223, reg.getData());
    }

    @Test
    public void updateTest() {
        stateContainer.registers[0].setData(7430223);

        RegisterWithUpdateParser.UpdatableRegister reg = REGISTER_WITH_UPDATE.parse(stateContainer, "R0!");
        assertEquals(stateContainer.registers[0].getData(), reg.getData());

        reg.update(3);

        assertEquals(7430226, reg.getData());
    }

    @Test
    public void failTest() {
        assertThrows(AssemblySyntaxException.class, () -> REGISTER_WITH_UPDATE.parse(stateContainer, "!R1"));
        assertThrows(AssemblySyntaxException.class, () -> REGISTER_WITH_UPDATE.parse(stateContainer, "R16!"));
        assertThrows(AssemblySyntaxException.class, () -> REGISTER_WITH_UPDATE.parse(stateContainer, "R!"));
        assertThrows(AssemblySyntaxException.class, () -> REGISTER_WITH_UPDATE.parse(stateContainer, "R17"));
    }
}