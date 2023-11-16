package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ANDExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private StateContainer stateContainerBis;
    private ANDExecutor andExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        stateContainerBis = new StateContainer();
        andExecutor = new ANDExecutor();
    }

    @Test
    public void simpleAndTest() {
        stateContainer.registers[0].setData(0b00000000000000000000000000101011);
        Register r0 = stateContainerBis.registers[0];
        r0.setData(99);
        Register r1 = stateContainerBis.registers[1];
        r1.setData(0b00000000000000000000000011111111);
        Register r2 = stateContainerBis.registers[2];
        r2.setData(0b00000000000000000000000000101011);
        andExecutor.execute(stateContainerBis, false, false, null, null, r0, r1, r2.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(stateContainer.registers[0].getData(), r0.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        r0.setData(0b11111111111111111111111111111111);
        r1.setData(0b11111111111111111111111111111111);
        andExecutor.execute(stateContainer, false, true, null, null, r2, r1, r0.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(0b11111111111111111111111111111111, r2.getData());
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r0.setData(0b00000000000000011111111111111111);
        r1.setData(0b11111111111111100000000000000000);
        andExecutor.execute(stateContainer, false, true, null, null, r2, r1, r0.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(0, r2.getData());
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
    }

}
