package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BLXExecutorTest {

    private StateContainer stateContainer;
    private BLXExecutor blxExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        blxExecutor = new BLXExecutor();
    }

    @Test
    public void simpleBlxTest() {
        Register r0 = stateContainer.getRegister(0);
        Register pc = stateContainer.getPC();
        pc.setData(8);
        r0.setData(45);
        blxExecutor.execute(stateContainer, false, false, null, null, r0, null, null, null);
        assertEquals(12, stateContainer.getLR().getData());
        assertEquals(45, pc.getData());
        assertTrue(stateContainer.getCPSR().getT());
    }

}
