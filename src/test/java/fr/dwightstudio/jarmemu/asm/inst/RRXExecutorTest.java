package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RRXExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private RRXExecutor rrxExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        rrxExecutor = new RRXExecutor();
    }

    @Test
    public void simpleRrxTest() {
        //TODO: faire les tests de RRX
    }

}
