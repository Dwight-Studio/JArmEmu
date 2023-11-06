package fr.dwightstudio.jarmemu.sim.args;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.args.RegisterParser;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RegisterParserTest {

    private StateContainer stateContainer;
    private static final RegisterParser REGISTER = new RegisterParser();

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    public void allRegisterTest() {
        for (int i = 0 ; i < 16 ; i++) {
            assertEquals(stateContainer.registers[i], REGISTER.parse(stateContainer, "R" + i));
        }

        assertEquals(stateContainer.registers[13], REGISTER.parse(stateContainer, "SP"));
        assertEquals(stateContainer.registers[14], REGISTER.parse(stateContainer, "LR"));
        assertEquals(stateContainer.registers[15], REGISTER.parse(stateContainer, "PC"));
        assertEquals(stateContainer.cpsr, REGISTER.parse(stateContainer, "CPSR"));
        assertEquals(stateContainer.spsr, REGISTER.parse(stateContainer, "SPSR"));

        assertThrows(SyntaxASMException.class, () -> REGISTER.parse(stateContainer, "DAF"));
        assertThrows(SyntaxASMException.class, () -> REGISTER.parse(stateContainer, "R16"));
        assertThrows(SyntaxASMException.class, () -> REGISTER.parse(stateContainer, "R-1"));
        assertThrows(SyntaxASMException.class, () -> REGISTER.parse(stateContainer, "RL"));
        assertThrows(SyntaxASMException.class, () -> REGISTER.parse(stateContainer, "PCCPSR"));
        assertThrows(SyntaxASMException.class, () -> REGISTER.parse(stateContainer, "CPSR15"));
    }
}
