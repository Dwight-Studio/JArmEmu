package fr.dwightstudio.jarmemu.base.asm.argument;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
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

        assertThrows(SyntaxASMException.class, () -> parse("CPSR"));
        assertThrows(SyntaxASMException.class, () -> parse("SPSR"));

        assertThrows(SyntaxASMException.class, () -> parse("DAF"));
        assertThrows(SyntaxASMException.class, () -> parse("R16"));
        assertThrows(SyntaxASMException.class, () -> parse("R-1"));
        assertThrows(SyntaxASMException.class, () -> parse("RL"));
        assertThrows(SyntaxASMException.class, () -> parse("PCCPSR"));
        assertThrows(SyntaxASMException.class, () -> parse("CPSR15"));
    }
}