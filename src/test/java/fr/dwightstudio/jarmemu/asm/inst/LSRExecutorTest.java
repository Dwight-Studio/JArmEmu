package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LSRExecutorTest {

    private StateContainer stateContainer;
    private LSRExecutor lsrExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        lsrExecutor = new LSRExecutor();
    }

    @Test
    public void simpleLsrTest() {
        //TODO: faire les tests de LSR
    }

}
