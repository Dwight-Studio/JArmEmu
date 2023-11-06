package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SWPExecutorTest {

    private StateContainer stateContainer;
    private SWPExecutor swpExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        swpExecutor = new SWPExecutor();
    }

    @Test
    public void simpleSwpTest() {
        //TODO: faire les tests de SWP
    }
}
