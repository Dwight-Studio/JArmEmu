package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MULExecutorTest {

    private StateContainer stateContainer;
    private MULExecutor mulExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        mulExecutor = new MULExecutor();
    }

    @Test
    public void simpleBTest() {
        //TODO: Faire les tests de l'instruction MUL
    }

}
