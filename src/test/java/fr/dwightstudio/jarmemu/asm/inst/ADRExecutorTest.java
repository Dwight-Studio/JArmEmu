package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.LabelParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ADRExecutorTest {

    private StateContainer stateContainer;
    private ADRExecutor adrExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        adrExecutor = new ADRExecutor();
    }

    @Test
    public void simpleAdrTest() {
        Register r2 = stateContainer.getRegister(2);
        r2.setData(16);
        Register pc = stateContainer.getPC();
        pc.setData(24);
        stateContainer.getAccessibleLabels().put("COUCOU", 20);
        Integer value =  new LabelParser().parse(stateContainer, "COUCOU");
        adrExecutor.execute(stateContainer, false, false, null, null, r2, value, null, null);
        assertEquals(20, r2.getData());
    }

    @Test
    public void failAdrTest() {
        Register r2 = stateContainer.getRegister(2);
        r2.setData(16);
        Register pc = stateContainer.getPC();
        pc.setData(24);
        stateContainer.getAccessibleLabels().put("COUCOU", 20);
        Integer value =  new LabelParser().parse(stateContainer, "COUCOU");
        assertThrows(SyntaxASMException.class, () -> adrExecutor.execute(stateContainer, false, true, null, null, r2, value, null, null));
    }

}
