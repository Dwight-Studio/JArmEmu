package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class POPInstructionTest extends InstructionTest<Register[], Object, Object, Object>{
    POPInstructionTest() {
        super(POPInstruction.class);
    }

    @Test
    public void simplePopTest() throws ASMException {
        POPInstruction popInstruction = new POPInstruction(Condition.AL, false, null, null, "{r0-r2}", null, null, null);
        Register sp = stateContainer.getRegister(13);
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        sp.setData(13988);
        stateContainer.getMemory().putWord(13988, 54);
        stateContainer.getMemory().putWord(13992, 12);
        stateContainer.getMemory().putWord(13996, 65);
        popInstruction.contextualize(stateContainer);
        popInstruction.execute(stateContainer, false);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(14000, sp.getData());
        sp.setData(988);
        r0 = stateContainer.getRegister(0);
        r1 = stateContainer.getRegister(1);
        r2 = stateContainer.getRegister(2);
        stateContainer.getMemory().putWord(988, 54);
        stateContainer.getMemory().putWord(992, 12);
        stateContainer.getMemory().putWord(996, 65);
        popInstruction.contextualize(stateContainer);
        popInstruction.execute(stateContainer, false);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(1000, sp.getData());
    }
}
