package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RRXExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private RRXExecutor rrxExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        rrxExecutor = new RRXExecutor();
    }

    @Test
    public void simpleRrxTest() {
        Register r0 = stateContainer.registers[0];
        r0.setData(25);
        rrxExecutor.execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(12, r0.getData());
        rrxExecutor.execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(6, r0.getData());
        rrxExecutor.execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(3, r0.getData());
        rrxExecutor.execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(1, r0.getData());
        rrxExecutor.execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(0, r0.getData());
        r0.setData(25);
        stateContainer.cpsr.setC(true);
        rrxExecutor.execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(-2147483636, r0.getData());
        rrxExecutor.execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(-1073741818, r0.getData());
        rrxExecutor.execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(-536870909, r0.getData());
        stateContainer.cpsr.setC(false);
        rrxExecutor.execute(stateContainer, false, false, null, null, r0, r0, null, null);
        assertEquals(1879048193, r0.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        r0.setData(25);
        stateContainer.cpsr.setC(true);
        rrxExecutor.execute(stateContainer, false, true, null, null, r0, r0, null, null);
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
        rrxExecutor.execute(stateContainer, false, true, null, null, r0, r0, null, null);
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
        rrxExecutor.execute(stateContainer, false, true, null, null, r0, r0, null, null);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
        rrxExecutor.execute(stateContainer, false, true, null, null, r0, r0, null, null);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
        rrxExecutor.execute(stateContainer, false, true, null, null, r0, r0, null, null);
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
        rrxExecutor.execute(stateContainer, false, true, null, null, r0, r0, null, null);
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
    }

}
