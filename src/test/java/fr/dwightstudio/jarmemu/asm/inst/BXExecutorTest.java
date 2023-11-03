package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BXExecutorTest {

    private StateContainer stateContainer;
    private BXExecutor bxExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        bxExecutor = new BXExecutor();
    }

    @Test
    public void simpleBxTest() {
        //TODO: Faire les tests de BX
    }

}
