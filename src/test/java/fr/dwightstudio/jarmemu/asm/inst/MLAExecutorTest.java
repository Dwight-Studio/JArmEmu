package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MLAExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private MLAExecutor mlaExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        mlaExecutor = new MLAExecutor();
    }

    @Test
    public void simpleMlaTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        Register r3 = stateContainer.registers[3];
        r1.setData(10);
        r2.setData(6);
        r3.setData(-15);
        mlaExecutor.execute(stateContainer, false, false, null, null, r0, r1, r2, r3);
        assertEquals(45, r0.getData());
        r1.setData(65847685);
        r2.setData(456456);
        r3.setData(456456456);
        mlaExecutor.execute(stateContainer, false, false, null, null, r0, r1, r2, r3);
        assertEquals(846223408, r0.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        Register r3 = stateContainer.registers[3];
        r1.setData(10);
        r2.setData(-15);
        r3.setData(6);
        mlaExecutor.execute(stateContainer, false, true, null, null, r0, r1, r2, r3);
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r1.setData(10);
        r2.setData(15);
        r3.setData(-6);
        mlaExecutor.execute(stateContainer, false, true, null, null, r0, r1, r2, r3);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r1.setData(10);
        r2.setData(1);
        r3.setData(-10);
        mlaExecutor.execute(stateContainer, false, true, null, null, r0, r1, r2, r3);
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
    }

}
