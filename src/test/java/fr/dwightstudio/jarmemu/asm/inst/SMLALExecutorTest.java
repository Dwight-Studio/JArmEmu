package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SMLALExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private SMLALExecutor smlalExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        smlalExecutor = new SMLALExecutor();
    }

    @Test
    public void simpleSmlalTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        Register r3 = stateContainer.registers[3];
        r2.setData(4);
        r3.setData(5);
        smlalExecutor.execute(stateContainer, false, false, null, null, r0, r1, r2, r3);
        assertEquals(0, r1.getData());
        assertEquals(20, r0.getData());
        smlalExecutor.execute(stateContainer, false, false, null, null, r0, r1, r2, r3);
        assertEquals(0, r1.getData());
        assertEquals(40, r0.getData());
        r2.setData(Integer.MAX_VALUE);
        r3.setData(-4);
        smlalExecutor.execute(stateContainer, false, false, null, null, r0, r1, r2, r3);
        assertEquals(-2, r1.getData());
        assertEquals(44, r0.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        Register r3 = stateContainer.registers[3];
        r2.setData(4);
        r3.setData(-5);
        smlalExecutor.execute(stateContainer, false, true, null, null, r0, r1, r2, r3);
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r2.setData(4);
        r3.setData(5);
        smlalExecutor.execute(stateContainer, false, true, null, null, r0, r1, r2, r3);
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
        smlalExecutor.execute(stateContainer, false, true, null, null, r0, r1, r2, r3);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r2.setData(1);
        r3.setData(1);
        smlalExecutor.execute(stateContainer, false, true, null, null, r0, r1, r2, r3);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
    }

}
