package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LDMExecutorTest {

    private StateContainer stateContainer;
    private LDMExecutor ldmExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        ldmExecutor = new LDMExecutor();
    }

    @Test
    public void simpleLdmTest() {
        //TODO: faire les tests de LDM
    }
}
