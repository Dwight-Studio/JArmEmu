package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.args.LabelParser;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BExecutorTest {

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
        bExecutor.execute(stateContainer, false, null, null, value, null, null, null);
        assertEquals(20, pc.getData());
    }

}
