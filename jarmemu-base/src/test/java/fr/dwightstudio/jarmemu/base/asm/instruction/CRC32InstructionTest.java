package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CRC32InstructionTest extends InstructionTest<String, Object, Object, Object> {
    CRC32InstructionTest() {
        super(CRC32Instruction.class);
    }

    @Test
    public void testExecute() {
        assertThrows(SyntaxASMException.class, () -> new CRC32Instruction(new Modifier(Condition.AL, false, null, null), "R0, R0, $48, $65, $6C, $68, $65, $6F, $20", null, null, null));
    }
}
