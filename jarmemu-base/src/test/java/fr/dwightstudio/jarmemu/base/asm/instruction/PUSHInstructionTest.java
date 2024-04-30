package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PUSHInstructionTest extends InstructionTest<Register[], Object, Object, Object> {
    PUSHInstructionTest() {
        super(PUSHInstruction.class);
    }

    @Test
    public void simplePushTest() throws ASMException {
        PUSHInstruction pushInstruction = new PUSHInstruction(new InstructionModifier(Condition.AL, false, null, null), "{r0-r2}", null, null, null);
        Register sp = stateContainer.getRegister(13);
        sp.setData(1000);
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        r0.setData(54);
        r1.setData(12);
        r2.setData(65);
        pushInstruction.contextualize(stateContainer);
        pushInstruction.execute(stateContainer, false);
        assertEquals(65, stateContainer.getMemory().getWord(996));
        assertEquals(12, stateContainer.getMemory().getWord(992));
        assertEquals(54, stateContainer.getMemory().getWord(988));
        assertEquals(988, sp.getData());
        sp.setData(10000);
        r0.setData(54);
        r1.setData(12);
        r2.setData(65);
        pushInstruction.contextualize(stateContainer);
        pushInstruction.execute(stateContainer, false);
        assertEquals(65, stateContainer.getMemory().getWord(9996));
        assertEquals(12, stateContainer.getMemory().getWord(9992));
        assertEquals(54, stateContainer.getMemory().getWord(9988));
        assertEquals(9988, sp.getData());
    }
}
