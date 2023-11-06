package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class STRExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private STRExecutor strExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        strExecutor = new STRExecutor();
    }

    @Test
    public void simpleStrTest() {
        //TODO: faire les tests de STR
    }

}
