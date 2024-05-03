package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DBGInstructionTest extends InstructionTest<String, Object, Object, Object> {
    DBGInstructionTest() {
        super(DBGInstruction.class);
    }

    @Test
    public void simpleDbgTest() throws ASMException {
        DBGInstruction dbgInstruction = new DBGInstruction(new InstructionModifier(Condition.AL, false, null, null), "15", null, null, null);
        Register r0 = stateContainer.getRegister(0);
        r0.setData(18);
        dbgInstruction.contextualize(stateContainer);
        dbgInstruction.execute(stateContainer, false);
        assertEquals(r0.getData(), 18);
    }
}
