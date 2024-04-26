package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BXNSInstructionTest extends InstructionTest<Register, Object, Object, Object> {
    BXNSInstructionTest() {
        super(BXNSInstruction.class);
    }

    @Test
    public void simpleBxTest() {
        Register lr = stateContainer.getRegister(0);
        Register pc = stateContainer.getPC();
        lr.setData(24);
        pc.setData(48);
        assertThrows(ASMException.class, () -> execute(stateContainer, false, false, null, null, lr, null, null, null));
    }
}
