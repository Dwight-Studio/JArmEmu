package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class STMExecutorTest extends JArmEmuTest {
    private StateContainer stateContainer;
    private STMExecutor stmExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        stmExecutor = new STMExecutor();
    }

    @Test
    public void simpleStmTest() {
        //TODO: faire les tests de STM
    }
}
