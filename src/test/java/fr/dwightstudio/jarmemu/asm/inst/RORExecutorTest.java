package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RORExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private RORExecutor rorExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        rorExecutor = new RORExecutor();
    }

    @Test
    public void simpleRorTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        r0.setData(25);
        r1.setData(-25);
        rorExecutor.execute(stateContainer, false, false, null, null, r0, r0, 3, null);
        assertEquals(536870915, r0.getData());
        rorExecutor.execute(stateContainer, false, false, null, null, r1, r1, 4, null);
        assertEquals(2147483646, r1.getData());
        rorExecutor.execute(stateContainer, false, false, null, null, r1, r1, 27, null);
        assertEquals(-49, r1.getData());
        rorExecutor.execute(stateContainer, false, false, null, null, r1, r1, 1, null);
        assertEquals(-25, r1.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        r0.setData(-25);
        r1.setData(25);
        rorExecutor.execute(stateContainer, false, true, null, null, r1, r1, 3, null);
        assertEquals(536870915, r1.getData());
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
        rorExecutor.execute(stateContainer, false, true, null, null, r0, r0, 4, null);
        assertEquals(2147483646, r0.getData());
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
        rorExecutor.execute(stateContainer, false, true, null, null, r0, r0, 27, null);
        assertEquals(-49, r0.getData());
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
        rorExecutor.execute(stateContainer, false, true, null, null, r0, r0, 1, null);
        assertEquals(-25, r0.getData());
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
        rorExecutor.execute(stateContainer, false, true, null, null, r0, r0, 1, null);
        assertEquals(-13, r0.getData());
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
    }

}
