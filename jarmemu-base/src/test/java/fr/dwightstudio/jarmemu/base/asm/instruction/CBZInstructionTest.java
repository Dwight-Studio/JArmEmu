package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CBZInstructionTest extends InstructionTest<Register, Integer, Object, Object> {

    protected CBZInstructionTest() {
        super(CBZInstruction.class);
    }

    @Test
    public void testCBZ() throws ASMException {
        Register pc = stateContainer.getPC();
        Register r2 = stateContainer.getRegister(2);
        Register r3 = stateContainer.getRegister(3);
        pc.setData(28);
        r2.setData(15);
        r3.setData(0);
        stateContainer.getAccessibleLabels().put("COUCOU", 20);
        Integer value = label(stateContainer, "COUCOU");
        execute(stateContainer, false, false, null, null, r2, value, null, null);
        assertEquals(32, pc.getData());
        execute(stateContainer, false, false, null, null, r3, value, null, null);
        assertEquals(20, pc.getData());
    }
}