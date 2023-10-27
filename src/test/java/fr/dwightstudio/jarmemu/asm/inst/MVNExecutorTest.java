package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.args.ArgumentParsers;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MVNExecutorTest {

    private StateContainer stateContainer;
    private MVNExecutor mvnExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        mvnExecutor = new MVNExecutor();
    }

    @Test
    public void simpleMvnTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        mvnExecutor.execute(stateContainer, false, null, null, r0, Integer.MIN_VALUE, ArgumentParsers.SHIFT.none(), null);
        assertEquals(Integer.MAX_VALUE, r0.getData());
        mvnExecutor.execute(stateContainer, false, null, null, r1, r0.getData(), ArgumentParsers.SHIFT.none(), null);
        assertEquals(Integer.MIN_VALUE, r1.getData());
        mvnExecutor.execute(stateContainer, false, null, null, r2, 0b10101010101010101010010001000001, ArgumentParsers.SHIFT.none(), null);
        assertEquals(~0b10101010101010101010010001000001, r2.getData());
    }

}
