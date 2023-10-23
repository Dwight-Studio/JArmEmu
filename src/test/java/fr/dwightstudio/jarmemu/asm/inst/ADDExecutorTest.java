package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.args.ArgumentParsers;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ADDExecutorTest {

    private StateContainer stateContainer;
    private StateContainer stateContainerBis;
    private ADDExecutor addExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        stateContainerBis = new StateContainer();
        addExecutor = new ADDExecutor();
    }

    @Test
    public void simpleAddTest() {
        stateContainer.registers[0].setData(25);
        Register r0 = stateContainerBis.registers[0];
        r0.setData(99);
        Register r1 = stateContainerBis.registers[1];
        r1.setData(5);
        Register r2 = stateContainerBis.registers[2];
        r2.setData(20);
        addExecutor.execute(stateContainerBis, false, null, null, r0, r1, r2.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(stateContainer.registers[0].getData(), stateContainerBis.registers[0].getData());
    }

    @Test
    public void shiftRegisterTest() {
        stateContainer.registers[0].setData(560);
        Register r0 = stateContainerBis.registers[0];
        r0.setData(99);
        Register r1 = stateContainerBis.registers[1];
        r1.setData(13);
        Register r2 = stateContainerBis.registers[2];
        r2.setData(456);
        addExecutor.execute(stateContainerBis, false, null, null, r0, r2, r1.getData(), ArgumentParsers.SHIFT.parse(stateContainerBis, "LSL#3"));
        assertEquals(stateContainer.registers[0].getData(), stateContainerBis.registers[0].getData());
    }

}
