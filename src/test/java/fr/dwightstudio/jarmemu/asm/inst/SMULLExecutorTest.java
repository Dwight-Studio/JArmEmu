package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SMULLExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private SMULLExecutor smullExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        smullExecutor = new SMULLExecutor();
    }

    @Test
    public void simpleSmullTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        Register r3 = stateContainer.registers[3];
        r2.setData(1);
        r3.setData(-1);
        smullExecutor.execute(stateContainer, false, null, null, r0, r1, r2, r3);
        assertEquals(-1, r1.getData());
        assertEquals(-1, r0.getData());
        r2.setData(25);
        r3.setData(14);
        smullExecutor.execute(stateContainer, false, null, null, r0, r1, r2, r3);
        assertEquals(0, r1.getData());
        assertEquals(350, r0.getData());
        r2.setData(Integer.MAX_VALUE);
        r3.setData(Integer.MAX_VALUE);
        smullExecutor.execute(stateContainer, false, null, null, r0, r1, r2, r3);
        assertEquals(1073741823, r1.getData());
        assertEquals(1, r0.getData());
        r2.setData(Integer.MAX_VALUE);
        r3.setData(-21);
        smullExecutor.execute(stateContainer, false, null, null, r0, r1, r2, r3);
        assertEquals(-11, r1.getData());
        assertEquals(-2147483627, r0.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        Register r3 = stateContainer.registers[3];
        r2.setData(1);
        r3.setData(-1);
        smullExecutor.execute(stateContainer, true, null, null, r0, r1, r2, r3);
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r2.setData(0);
        r3.setData(-1);
        smullExecutor.execute(stateContainer, true, null, null, r0, r1, r2, r3);
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
        r2.setData(4);
        r3.setData(5);
        smullExecutor.execute(stateContainer, true, null, null, r0, r1, r2, r3);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r2.setData(1);
        r3.setData(1);
        smullExecutor.execute(stateContainer, true, null, null, r0, r1, r2, r3);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
    }

}
