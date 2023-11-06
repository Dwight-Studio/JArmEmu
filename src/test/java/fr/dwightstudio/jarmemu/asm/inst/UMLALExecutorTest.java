package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UMLALExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private UMLALExecutor umlalExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        umlalExecutor = new UMLALExecutor();
    }

    @Test
    public void simpleUmlalTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        Register r3 = stateContainer.registers[3];
        r2.setData(4);
        r3.setData(5);
        umlalExecutor.execute(stateContainer, false, null, null, r1, r0, r2, r3);
        assertEquals(0, r0.getData());
        assertEquals(20, r1.getData());
        umlalExecutor.execute(stateContainer, false, null, null, r1, r0, r2, r3);
        assertEquals(0, r0.getData());
        assertEquals(40, r1.getData());
        r0.setData(0);
        r1.setData(0);
        r2.setData(1);
        r3.setData(-1);
        umlalExecutor.execute(stateContainer, false, null, null, r1, r0, r2, r3);
        assertEquals(0, r0.getData());
        assertEquals(-1, r1.getData());
        umlalExecutor.execute(stateContainer, false, null, null, r1, r0, r2, r3);
        assertEquals(1, r0.getData());
        assertEquals(-2, r1.getData());
        r0.setData(0);
        r1.setData(0);
        r2.setData(Integer.MAX_VALUE);
        r3.setData(1);
        umlalExecutor.execute(stateContainer, false, null, null, r1, r0, r2, r3);
        assertEquals(0, r0.getData());
        assertEquals(Integer.MAX_VALUE, r1.getData());
        umlalExecutor.execute(stateContainer, false, null, null, r1, r0, r2, r3);
        assertEquals(0, r0.getData());
        assertEquals(-2, r1.getData());
        r0.setData(0);
        r1.setData(0);
        r2.setData(Integer.MIN_VALUE);
        r3.setData(1);
        umlalExecutor.execute(stateContainer, false, null, null, r1, r0, r2, r3);
        assertEquals(0, r0.getData());
        assertEquals(Integer.MIN_VALUE, r1.getData());
        umlalExecutor.execute(stateContainer, false, null, null, r1, r0, r2, r3);
        assertEquals(1, r0.getData());
        assertEquals(0, r1.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        Register r3 = stateContainer.registers[3];
        r2.setData(4);
        r3.setData(5);
        umlalExecutor.execute(stateContainer, true, null, null, r1, r0, r2, r3);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r2.setData(4);
        r3.setData(-5);
        umlalExecutor.execute(stateContainer, true, null, null, r1, r0, r2, r3);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r0.setData(0);
        r1.setData(0);
        r2.setData(4);
        r3.setData(0);
        umlalExecutor.execute(stateContainer, true, null, null, r1, r0, r2, r3);
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
        r2.setData(-1);
        r3.setData(-456);
        umlalExecutor.execute(stateContainer, true, null, null, r1, r0, r2, r3);
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        umlalExecutor.execute(stateContainer, true, null, null, r1, r0, r2, r3);
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
    }

}
