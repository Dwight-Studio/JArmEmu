package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.LabelParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CBZExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private CBZExecutor cbzExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        stateContainer.clearAndInitFiles(1);
        cbzExecutor = new CBZExecutor();
    }

    @Test
    public void testCBZ() {
        Register pc = stateContainer.getPC();
        Register r2 = stateContainer.getRegister(2);
        Register r3 = stateContainer.getRegister(3);
        pc.setData(28);
        r2.setData(15);
        r3.setData(0);
        stateContainer.getAccessibleLabels().put("COUCOU", 20);
        Integer value =  new LabelParser().parse(stateContainer, "COUCOU");
        cbzExecutor.execute(stateContainer, false, false, null, null, r2, value, null, null);
        assertEquals(28, pc.getData());
        cbzExecutor.execute(stateContainer, false, false, null, null, r3, value, null, null);
        assertEquals(20, pc.getData());
    }

}
