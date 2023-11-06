package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ASRExecutorTest {

    private StateContainer stateContainer;
    private ASRExecutor asrExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        asrExecutor = new ASRExecutor();
    }

    @Test
    public void simpleAsrTest() {
        //TODO: faire les tests de ASR
    }

}
