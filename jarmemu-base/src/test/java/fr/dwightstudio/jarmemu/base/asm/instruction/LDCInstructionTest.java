package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LDCInstructionTest extends InstructionTest<String, Object, Object, Object> {
    LDCInstructionTest() {
        super(LDCInstruction.class);
    }

    @Test
    public void testExecute() {
        assertThrows(SyntaxASMException.class, () -> new LDCInstruction(new InstructionModifier(Condition.AL, false, null, null), "p5, c12, [r5]", null, null, null));
    }
}
