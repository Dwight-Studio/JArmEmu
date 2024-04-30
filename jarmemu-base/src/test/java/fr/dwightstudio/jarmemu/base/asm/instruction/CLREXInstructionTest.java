package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CLREXInstructionTest extends InstructionTest<Object, Object, Object, Object> {

    protected CLREXInstructionTest() {
        super(CLREXInstruction.class);
    }

    @Test
    public void testExecute() {
        assertThrows(SyntaxASMException.class, () -> legacyExecute(stateContainer, false, false, null, null, null, null, null, null));
    }
}