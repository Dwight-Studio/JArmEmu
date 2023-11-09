package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LSLExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private LSLExecutor lslExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        lslExecutor = new LSLExecutor();
    }

    @Test
    public void simpleLslTest() {
        //TODO: faire les tests de LSL
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        r0.setData(25);
        r1.setData(-25);
        lslExecutor.execute(stateContainer, false, null, null, r0, r0, 3, null);
        assertEquals(200, r0.getData());
        lslExecutor.execute(stateContainer, false, null, null, r1, r1, 4, null);
        assertEquals(-400, r1.getData());
        lslExecutor.execute(stateContainer, false, null, null, r1, r1, 27, null);
        assertEquals(Integer.MIN_VALUE, r1.getData());
        lslExecutor.execute(stateContainer, false, null, null, r1, r1, 1, null);
        assertEquals(0, r1.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        r0.setData(-25);
        r1.setData(25);
        lslExecutor.execute(stateContainer, true, null, null, r1, r1, 4, null);
        assertEquals(400, r1.getData());
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
        lslExecutor.execute(stateContainer, true, null, null, r0, r0, 4, null);
        assertEquals(-400, r0.getData());
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
        lslExecutor.execute(stateContainer, true, null, null, r0, r0, 27, null);
        assertEquals(Integer.MIN_VALUE, r0.getData());
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
        lslExecutor.execute(stateContainer, true, null, null, r0, r0, 1, null);
        assertEquals(0, r0.getData());
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
        lslExecutor.execute(stateContainer, true, null, null, r0, r0, 1, null);
        assertEquals(0, r0.getData());
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
    }

}
