package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MLSExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private MLSExecutor mlsExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        mlsExecutor = new MLSExecutor();
    }

    @Test
    public void simpleMlsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        Register r3 = stateContainer.registers[3];
        r3.setData(100);
        r2.setData(4);
        r1.setData(5);
        mlsExecutor.execute(stateContainer, false, null, null, r0, r1, r2, r3);
        assertEquals(80, r0.getData());
        r3.setData(65847685);
        r2.setData(456456);
        r1.setData(456456456);
        mlsExecutor.execute(stateContainer, false, null, null, r0, r1, r2, r3);
        assertEquals(936264005, r0.getData());
    }

}
