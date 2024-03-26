package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CLREXExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private CLREXExecutor clrexExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        clrexExecutor = new CLREXExecutor();
    }

    @Test
    public void testExecute() {
        assertThrows(SyntaxASMException.class, () -> clrexExecutor.execute(stateContainer, false, false, null, null, null, null, null, null));
    }

}
