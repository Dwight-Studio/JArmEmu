package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class HLTInstructionTest extends InstructionTest<String, Object, Object, Object> {
    HLTInstructionTest() {
        super(HLTInstruction.class);
    }

    @Test
    public void testExecute() {
        assertThrows(SyntaxASMException.class, () -> new HLTInstruction(new InstructionModifier(Condition.AL, false, null, null), "#14", null, null, null));
    }
}
