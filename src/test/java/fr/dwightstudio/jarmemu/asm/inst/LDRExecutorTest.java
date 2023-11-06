package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LDRExecutorTest {

    private StateContainer stateContainer;
    private LDRExecutor ldrExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        ldrExecutor = new LDRExecutor();
    }

    @Test
    public void simpleLdrTest() {
        //TODO: faire les tests de LDR
    }

}
