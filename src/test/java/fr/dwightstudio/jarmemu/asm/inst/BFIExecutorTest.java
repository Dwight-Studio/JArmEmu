package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BFIExecutorTest {

    private StateContainer stateContainer;
    private BFIExecutor bfiExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        bfiExecutor = new BFIExecutor();
    }

    @Test
    public void simpleBfiTest() {
        Register r2 = stateContainer.getRegister(2);
        Register r3 = stateContainer.getRegister(3);
        r2.setData(13);
        r3.setData(1);
        bfiExecutor.execute(stateContainer, false, false, null, null, r2, r3, 0, 1);
        assertEquals(13, r2.getData());
        bfiExecutor.execute(stateContainer, false, false, null, null, r2, r3, 1, 1);
        assertEquals(15, r2.getData());
        r2.setData(13);
        bfiExecutor.execute(stateContainer, false, false, null, null, r2, r3, 0, 3);
        assertEquals(9, r2.getData());
        r2.setData(13);
        r3.setData(2);
        bfiExecutor.execute(stateContainer, false, false, null, null, r2, r3, 1, 1);
        assertEquals(13, r2.getData());
        bfiExecutor.execute(stateContainer, false, false, null, null, r2, r3, 0, 1);
        assertEquals(12, r2.getData());
        r2.setData(13);
        bfiExecutor.execute(stateContainer, false, false, null, null, r2, r3, 0, 2);
        assertEquals(14, r2.getData());
        r2.setData(13);
        r3.setData(3);
        bfiExecutor.execute(stateContainer, false, false, null, null, r2, r3, 0, 2);
        assertEquals(15, r2.getData());
    }

    @Test
    public void edgeCaseTest() {
        Register r2 = stateContainer.getRegister(2);
        Register r3 = stateContainer.getRegister(3);
        r2.setData(654651635);
        r3.setData(0);
        bfiExecutor.execute(stateContainer, false, false, null, null, r2, r3, 0, 32);
        assertEquals(0, r2.getData());
    }

}
