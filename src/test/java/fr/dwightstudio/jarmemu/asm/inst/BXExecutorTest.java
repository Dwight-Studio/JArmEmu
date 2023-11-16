package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.exceptions.StuckExecutionASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BXExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private BXExecutor bxExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        bxExecutor = new BXExecutor();
    }

    @Test
    public void simpleBxTest() {
        Register lr = stateContainer.registers[0];
        Register pc = stateContainer.registers[15];
        lr.setData(24);
        pc.setData(48);
        bxExecutor.execute(stateContainer, false, false, null, null, lr, null, null, null);
        assertEquals(24, pc.getData());
        assertFalse(stateContainer.cpsr.getT());
        lr.setData(45);
        bxExecutor.execute(stateContainer, false, false, null, null, lr, null, null, null);
        assertEquals(45, pc.getData());
        assertTrue(stateContainer.cpsr.getT());
    }

    @Test
    public void BxExceptionTest() {
        Register pc = stateContainer.registers[15];
        assertThrows(StuckExecutionASMException.class, () -> bxExecutor.execute(stateContainer, false, false, null, null, pc, null, null, null));
    }

}
