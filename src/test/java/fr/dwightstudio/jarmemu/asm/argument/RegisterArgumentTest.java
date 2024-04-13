package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterArgumentTest extends ArgumentTest<Register> {
    public RegisterArgumentTest() {
        super(RegisterArgument.class);
    }

    @Test
    public void allRegisterTest() throws ASMException {
        for (int i = 0 ; i < 16 ; i++) {
            assertEquals(stateContainer.getRegister(i), parse("R" + i));
        }

        assertEquals(stateContainer.getRegister(13), parse("SP"));
        assertEquals(stateContainer.getLR(), parse("LR"));
        assertEquals(stateContainer.getPC(), parse("PC"));
        assertEquals(stateContainer.getCPSR(), parse("CPSR"));
        assertEquals(stateContainer.getSPSR(), parse("SPSR"));

        assertThrows(SyntaxASMException.class, () -> parse("DAF"));
        assertThrows(SyntaxASMException.class, () -> parse("R16"));
        assertThrows(SyntaxASMException.class, () -> parse("R-1"));
        assertThrows(SyntaxASMException.class, () -> parse("RL"));
        assertThrows(SyntaxASMException.class, () -> parse("PCCPSR"));
        assertThrows(SyntaxASMException.class, () -> parse("CPSR15"));
    }
}