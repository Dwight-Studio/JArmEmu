package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RORExecutorTest {

    private StateContainer stateContainer;
    private RORExecutor rorExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        rorExecutor = new RORExecutor();
    }

    @Test
    public void simpleRorTest() {
        //TODO: faire les tests de ROR
    }

}
