package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LSLExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private LSLExecutor lslExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        lslExecutor = new LSLExecutor();
    }

    @Test
    public void simpleLslTest() {
        //TODO: faire les tests de LSL
    }

}
