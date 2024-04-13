package fr.dwightstudio.jarmemu.asm.instruction;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CBNZInstructionTest extends InstructionTest<Register, Integer, Object, Object> {

    protected CBNZInstructionTest() {
        super(CBNZInstruction.class);
    }

    @Test
    public void testCBNZ() throws ASMException {
        Register pc = stateContainer.getPC();
        Register r2 = stateContainer.getRegister(2);
        Register r3 = stateContainer.getRegister(3);
        pc.setData(28);
        r2.setData(0);
        r3.setData(15);
        stateContainer.getAccessibleLabels().put("COUCOU", 20);
        Integer value =  label(stateContainer, "COUCOU");
        execute(stateContainer, false, false, null, null, r2, value, null, null);
        assertEquals(32, pc.getData());
        execute(stateContainer, false, false, null, null, r3, value, null, null);
        assertEquals(20, pc.getData());
    }
}