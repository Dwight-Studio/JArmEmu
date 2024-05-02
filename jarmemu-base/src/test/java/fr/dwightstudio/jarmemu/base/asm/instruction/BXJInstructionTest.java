package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.NotImplementedASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BXJInstructionTest extends InstructionTest<Register, Object, Object, Object> {

    BXJInstructionTest() {
        super(BXJInstruction.class);
    }

    @Test
    public void testExecute() {
        Register lr = stateContainer.getRegister(0);
        lr.setData(24);
        assertThrows(NotImplementedASMException.class, () -> legacyExecute(stateContainer, false, false, null, null, lr, null, null, null));
    }
}