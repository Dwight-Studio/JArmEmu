package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UMULLExecutorTest {

    private StateContainer stateContainer;
    private UMULLExecutor umullExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        umullExecutor = new UMULLExecutor();
    }

    @Test
    public void simpleUmullTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        Register r3 = stateContainer.registers[3];
        r2.setData(1);
        r3.setData(-1);
        umullExecutor.execute(stateContainer, false, null, null, r1, r0, r2, r3);
        assertEquals(0, r0.getData());
        assertEquals(-1, r1.getData());
        r2.setData(4);
        r3.setData(5);
        umullExecutor.execute(stateContainer, false, null, null, r1, r0, r2, r3);
        assertEquals(0, r0.getData());
        assertEquals(20, r1.getData());
        r2.setData(Integer.MAX_VALUE);
        r3.setData(2);
        umullExecutor.execute(stateContainer, false, null, null, r1, r0, r2, r3);
        assertEquals(0, r0.getData());
        assertEquals(-2, r1.getData());
        r2.setData(Integer.MAX_VALUE);
        r3.setData(3);
        umullExecutor.execute(stateContainer, false, null, null, r1, r0, r2, r3);
        assertEquals(1, r0.getData());
        assertEquals(Integer.MAX_VALUE-2, r1.getData());
        r2.setData(-Integer.MAX_VALUE);
        r3.setData(3);
        umullExecutor.execute(stateContainer, false, null, null, r1, r0, r2, r3);
        assertEquals(1, r0.getData());
        assertEquals(Integer.MIN_VALUE+3, r1.getData());
        r2.setData(-Integer.MAX_VALUE);
        r3.setData(654);
        umullExecutor.execute(stateContainer, false, null, null, r1, r0, r2, r3);
        assertEquals(327, r0.getData());
        assertEquals(654, r1.getData());
        r2.setData(-Integer.MAX_VALUE);
        r3.setData(-Integer.MAX_VALUE);
        umullExecutor.execute(stateContainer, false, null, null, r1, r0, r2, r3);
        assertEquals(1073741825, r0.getData());
        assertEquals(1, r1.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        Register r3 = stateContainer.registers[3];
        r2.setData(5);
        r3.setData(4);
        umullExecutor.execute(stateContainer, true, null, null, r1, r0, r2, r3);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r2.setData(0);
        r3.setData(4);
        umullExecutor.execute(stateContainer, true, null, null, r1, r0, r2, r3);
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
        r2.setData(-1);
        r3.setData(1);
        umullExecutor.execute(stateContainer, true, null, null, r1, r0, r2, r3);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r2.setData(-1);
        r3.setData(-456);
        umullExecutor.execute(stateContainer, true, null, null, r1, r0, r2, r3);
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r2.setData(Integer.MIN_VALUE);
        r3.setData(456);
        umullExecutor.execute(stateContainer, true, null, null, r1, r0, r2, r3);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
    }

}
