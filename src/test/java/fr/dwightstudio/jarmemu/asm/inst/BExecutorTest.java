package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.exceptions.StuckExecutionASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.LabelParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private BExecutor bExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        bExecutor = new BExecutor();
    }

    @Test
    public void simpleBTest() {
        Register pc = stateContainer.registers[15];
        pc.setData(24);
        stateContainer.labels.put("COUCOU", 20);
        Integer value =  new LabelParser().parse(stateContainer, "COUCOU");
        bExecutor.execute(stateContainer, false, false, null, null, value, null, null, null);
        assertEquals(20, pc.getData());
    }

    @Test
    public void BExceptionTest() {
        Register pc = stateContainer.registers[15];
        pc.setData(24);
        stateContainer.labels.put("COUCOU", 24);
        Integer value =  new LabelParser().parse(stateContainer, "COUCOU");
        assertThrows(StuckExecutionASMException.class, () -> bExecutor.execute(stateContainer, false, false, null, null, value, null, null, null));
    }

}
