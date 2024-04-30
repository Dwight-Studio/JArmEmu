package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BLXNSInstructionTest extends InstructionTest<Integer, Object, Object, Object> {

    BLXNSInstructionTest() {
        super(BLXNSInstruction.class);
    }

    @Test
    public void simpleBlxnsTest() {
        Register r0 = stateContainer.getRegister(0);
        Register pc = stateContainer.getPC();
        pc.setData(8);
        r0.setData(45);
        assertThrows(ASMException.class, () -> legacyExecute(stateContainer, false, false, null, null, r0.getData(), null, null, null));
    }
}
