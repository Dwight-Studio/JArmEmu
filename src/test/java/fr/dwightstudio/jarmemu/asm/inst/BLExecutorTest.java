package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.args.LabelParser;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BLExecutorTest {

    private StateContainer stateContainer;
    private BLExecutor blExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        blExecutor = new BLExecutor();
    }

    @Test
    public void simpleBlTest() {
        Register lr = stateContainer.registers[14];
        Register pc = stateContainer.registers[15];
        pc.setData(24);
        stateContainer.labels.put("COUCOU", 20);
        Integer value =  new LabelParser().parse(stateContainer, "COUCOU");
        blExecutor.execute(stateContainer, false, null, null, value, null, null, null);
        assertEquals(20, pc.getData());
        assertEquals(28, lr.getData());
    }

}
