package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LDAEXInstructionTest extends InstructionTest<String, Object, Object, Object> {
    LDAEXInstructionTest() {
        super(LDAEXInstruction.class);
    }

    @Test
    public void testExecute() {
        assertThrows(SyntaxASMException.class, () -> new LDAEXInstruction(new Modifier(Condition.AL, false, null, null), "r0, [r2]", null, null, null));
    }
}
