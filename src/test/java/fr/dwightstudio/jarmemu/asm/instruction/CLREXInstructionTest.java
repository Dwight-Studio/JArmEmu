package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CLREXInstructionTest extends InstructionTest<Object, Object, Object, Object> {

    protected CLREXInstructionTest() {
        super(CLREXInstruction.class);
    }

    @Test
    public void testExecute() {
        assertThrows(SyntaxASMException.class, () -> execute(stateContainer, false, false, null, null, null, null, null, null));
    }
}