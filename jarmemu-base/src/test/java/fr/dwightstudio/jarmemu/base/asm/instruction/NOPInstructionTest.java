package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NOPInstructionTest extends InstructionTest<Object, Object, Object, Object> {
     NOPInstructionTest() {
        super(NOPInstruction.class);
    }

    @Test
    public void simpleNopTest() throws ASMException {
        NOPInstruction nopInstruction = new NOPInstruction(Condition.AL, false, null, null, (String) null, null, null, null);
        Register r0 = stateContainer.getRegister(0);
        r0.setData(21);
        nopInstruction.contextualize(stateContainer);
        nopInstruction.execute(stateContainer, false);
        assertEquals(r0.getData(), 21);
    }
}
