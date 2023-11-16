package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.exceptions.SoftwareInterruptionASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SWIExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private SWIExecutor swiExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        swiExecutor = new SWIExecutor();
    }

    @Test
    public void simpleSwiTest() {
        assertThrows(SoftwareInterruptionASMException.class, () -> swiExecutor.execute(stateContainer, false, false, null, null, 0, null, null, null));
    }

}
