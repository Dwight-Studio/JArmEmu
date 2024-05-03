package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CRC32CInstructionTest extends InstructionTest<String, Object, Object, Object> {
    CRC32CInstructionTest() {
        super(CRC32CInstruction.class);
    }

    @Test
    public void testExecute() {
        assertThrows(SyntaxASMException.class, () -> new CRC32CInstruction(new InstructionModifier(Condition.AL, false, null, null), "R0, R0, $48, $65, $6C, $68, $65, $6F, $20", null, null, null));
    }
}
