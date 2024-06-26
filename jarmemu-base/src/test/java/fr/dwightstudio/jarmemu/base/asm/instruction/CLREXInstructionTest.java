package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.NotImplementedASMException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CLREXInstructionTest extends InstructionTest<Object, Object, Object, Object> {

    protected CLREXInstructionTest() {
        super(CLREXInstruction.class);
    }

    @Test
    public void testExecute() {
        assertThrows(NotImplementedASMException.class, () -> legacyExecute(stateContainer, false, false, null, null, null, null, null, null));
    }
}