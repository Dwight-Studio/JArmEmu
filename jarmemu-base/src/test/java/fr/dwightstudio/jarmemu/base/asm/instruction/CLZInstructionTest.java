package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CLZInstructionTest extends InstructionTest<Register, Register, Object, Object> {

    protected CLZInstructionTest() {
        super(CLZInstruction.class);
    }

    @Test
    public void testExecute() throws ASMException {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        r0.setData(0);
        r1.setData(-1);
        execute(stateContainer, false, false, null, null, r0, r1, null, null);
        assertEquals(0, r0.getData());
        r1.setData(16);
        execute(stateContainer, false, false, null, null, r0, r1, null, null);
        assertEquals(27, r0.getData());
    }
}