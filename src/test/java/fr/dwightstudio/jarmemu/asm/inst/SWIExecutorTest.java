package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SWIExecutorTest {

    private StateContainer stateContainer;
    private SWIExecutor swiExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        swiExecutor = new SWIExecutor();
    }

    @Test
    public void simpleSwiTest() {

    }

}
