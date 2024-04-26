package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BXJInstructionTest extends InstructionTest<Register, Object, Object, Object> {

    public BXJInstructionTest() {
        super(BXJInstruction.class);
    }

    @Test
    public void testExecute() {
        Register lr = stateContainer.getRegister(0);
        lr.setData(24);
        assertThrows(SyntaxASMException.class, () -> execute(stateContainer, false, false, null, null, lr, null, null, null));
    }
}