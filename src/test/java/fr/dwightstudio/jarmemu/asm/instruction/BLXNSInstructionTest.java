package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.sim.entity.Register;
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
        assertThrows(ASMException.class, () -> execute(stateContainer, false, false, null, null, r0.getData(), null, null, null));
    }
}
