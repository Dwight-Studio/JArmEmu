package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.exceptions.BreakpointASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BKPTExecutorTest {

    private BKPTExecutor bkptExecutor;

    @BeforeEach
    public void setUp() {
        bkptExecutor = new BKPTExecutor();
    }

    @Test
    public void simpleBkptTest() {
        Assertions.assertThrows(BreakpointASMException.class, () -> bkptExecutor.execute(new StateContainer(), false, false, null, null, 1, null, null, null));
    }

}
