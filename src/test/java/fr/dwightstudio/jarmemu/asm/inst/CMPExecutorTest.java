package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.args.ArgumentParsers;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CMPExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private CMPExecutor cmpExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        cmpExecutor = new CMPExecutor();
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        r0.setData(5);
        r1.setData(3);
        cmpExecutor.execute(stateContainer, false, null, null, r0, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
        assertFalse(stateContainer.cpsr.getV());
        r0.setData(5);
        r1.setData(5);
        cmpExecutor.execute(stateContainer, false, null, null, r0, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
        assertFalse(stateContainer.cpsr.getV());
        r0.setData(3);
        r1.setData(5);
        cmpExecutor.execute(stateContainer, false, null, null, r0, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
        assertFalse(stateContainer.cpsr.getV());
        r0.setData(0b01111111111111111111111111111111);
        r1.setData(0b11111111111111111111111111111111);
        cmpExecutor.execute(stateContainer, false, null, null, r0, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
        assertTrue(stateContainer.cpsr.getV());
        r0.setData(5);
        r1.setData(-3);
        cmpExecutor.execute(stateContainer, false, null, null, r0, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
        assertFalse(stateContainer.cpsr.getV());
    }

}
