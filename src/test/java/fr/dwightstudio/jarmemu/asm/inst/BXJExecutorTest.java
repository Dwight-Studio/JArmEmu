package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.exceptions.SoftwareInterruptionASMException;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BXJExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private BXJExecutor bxjExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        bxjExecutor = new BXJExecutor();
    }

    @Test
    public void testExecute() {
        Register lr = stateContainer.getRegister(0);
        lr.setData(24);
        assertThrows(SyntaxASMException.class, () -> bxjExecutor.execute(stateContainer, false, false, null, null, lr, null, null, null));
    }

}
