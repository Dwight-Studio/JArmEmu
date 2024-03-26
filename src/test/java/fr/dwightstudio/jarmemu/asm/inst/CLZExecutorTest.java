package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CLZExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private CLZExecutor clzExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        clzExecutor = new CLZExecutor();
    }

    @Test
    public void testExecute() {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        r0.setData(0);
        r1.setData(-1);
        clzExecutor.execute(stateContainer, false, false, null, null, r0, r1, null, null);
        assertEquals(0, r0.getData());
        r1.setData(16);
        clzExecutor.execute(stateContainer, false, false, null, null, r0, r1, null, null);
        assertEquals(27, r0.getData());
    }
}
