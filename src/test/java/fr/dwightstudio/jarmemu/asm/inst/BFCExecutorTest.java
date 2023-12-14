package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BFCExecutorTest {

    private StateContainer stateContainer;
    private BFCExecutor bfcExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        bfcExecutor = new BFCExecutor();
    }

    @Test
    public void simpleBfcTest() {
        Register r2 = stateContainer.getRegister(2);
        r2.setData(0b010101010101010);
        bfcExecutor.execute(stateContainer, false, false, null, null, r2, 3, 4, null);
        assertEquals(0b010101010000010, r2.getData());
    }

    @Test
    public void failAdrTest() {
        Register r2 = stateContainer.getRegister(2);
        r2.setData(0b010101010101010);
        assertThrows(SyntaxASMException.class, () -> bfcExecutor.execute(stateContainer, false, true, null, null, r2, -2, 4, null));
        assertThrows(SyntaxASMException.class, () -> bfcExecutor.execute(stateContainer, false, true, null, null, r2, 3, 32, null));
    }

}
