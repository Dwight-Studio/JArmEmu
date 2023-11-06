package fr.dwightstudio.jarmemu.sim.args;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.args.RegisterArrayParser;
import fr.dwightstudio.jarmemu.sim.args.RegisterWithUpdateParser;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterWithUpdateParserTest {

    private StateContainer stateContainer;
    private static final RegisterWithUpdateParser REGISTER_WITH_UPDATE = new RegisterWithUpdateParser();
    private static final RegisterArrayParser REGISTER_ARRAY = new RegisterArrayParser();

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    public void normalTest() {
        stateContainer.registers[0].setData(7430223);

        RegisterWithUpdateParser.UpdatableRegister reg = REGISTER_WITH_UPDATE.parse(stateContainer, "R0");
        assertEquals(stateContainer.registers[0].getData(), reg.getData());

        REGISTER_ARRAY.parse(stateContainer, "{R0,R1,R2}");
        reg.update();

        assertEquals(7430223, reg.getData());
    }

    @Test
    public void updateTest() {
        stateContainer.registers[0].setData(7430223);

        RegisterWithUpdateParser.UpdatableRegister reg = REGISTER_WITH_UPDATE.parse(stateContainer, "R0!");
        assertEquals(stateContainer.registers[0].getData(), reg.getData());

        REGISTER_ARRAY.parse(stateContainer, "{R0,R1,R2}");
        reg.update();

        assertEquals(7430226, reg.getData());
    }

    @Test
    public void failTest() {
        assertThrows(SyntaxASMException.class, () -> REGISTER_WITH_UPDATE.parse(stateContainer, "!R1"));
        assertThrows(SyntaxASMException.class, () -> REGISTER_WITH_UPDATE.parse(stateContainer, "R16!"));
        assertThrows(SyntaxASMException.class, () -> REGISTER_WITH_UPDATE.parse(stateContainer, "R!"));
        assertThrows(SyntaxASMException.class, () -> REGISTER_WITH_UPDATE.parse(stateContainer, "R17"));
    }
}