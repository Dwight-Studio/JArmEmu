package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SWPExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private SWPExecutor swpExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        swpExecutor = new SWPExecutor();
    }

    @Test
    public void simpleSwpTest() {
        Register r0 = stateContainer.registers[0];
        assertThrows(SyntaxASMException.class, () -> swpExecutor.execute(stateContainer, false, null, null, r0, null, null, null));
    }
}
