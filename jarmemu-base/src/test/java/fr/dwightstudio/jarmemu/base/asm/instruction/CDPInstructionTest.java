package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CDPInstructionTest extends InstructionTest<String, Object, Object, Object> {
    CDPInstructionTest() {
        super(CDPInstruction.class);
    }

    @Test
    public void testExecute() {
        assertThrows(SyntaxASMException.class, () -> new CDPInstruction(new Modifier(Condition.AL, false, null, null), "p5, 2, c12, c10, c3, 4", null, null, null));
    }
}
