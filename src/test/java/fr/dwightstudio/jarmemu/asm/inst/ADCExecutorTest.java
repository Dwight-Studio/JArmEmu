package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ADCExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private StateContainer stateContainerBis;
    private ADCExecutor adcExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        stateContainerBis = new StateContainer();
        adcExecutor = new ADCExecutor();
    }

    @Test
    public void simpleAdcTest() {
        stateContainer.registers[0].setData(25);
        Register r0 = stateContainerBis.registers[0];
        r0.setData(99);
        Register r1 = stateContainerBis.registers[1];
        r1.setData(5);
        Register r2 = stateContainerBis.registers[2];
        r2.setData(20);
        adcExecutor.execute(stateContainerBis, false, null, null, r0, r1, r2.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(stateContainer.registers[0].getData(), stateContainerBis.registers[0].getData());
        r0.setData(0b11111111111111111111111111111111);
        r1.setData(1);
        adcExecutor.execute(stateContainerBis, true, null, null, r2, r1, r0.getData(), ArgumentParsers.SHIFT.none());
        assertFalse(stateContainerBis.cpsr.getN());
        assertTrue(stateContainerBis.cpsr.getZ());
        assertTrue(stateContainerBis.cpsr.getC());
        assertFalse(stateContainerBis.cpsr.getV());
        stateContainer.registers[0].setData(26);
        r1.setData(5);
        r2.setData(20);
        adcExecutor.execute(stateContainerBis, true, null, null, r0, r1, r2.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(stateContainer.registers[0].getData(), stateContainerBis.registers[0].getData());
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
        assertFalse(stateContainer.cpsr.getV());
    }

    @Test
    public void shiftRegisterTest() {
        stateContainer.registers[0].setData(560);
        Register r0 = stateContainerBis.registers[0];
        r0.setData(99);
        Register r1 = stateContainerBis.registers[1];
        r1.setData(13);
        Register r2 = stateContainerBis.registers[2];
        r2.setData(456);
        adcExecutor.execute(stateContainerBis, false, null, null, r0, r2, r1.getData(), ArgumentParsers.SHIFT.parse(stateContainerBis, "LSL#3"));
        assertEquals(stateContainer.registers[0].getData(), r0.getData());
        r0.setData(0b11111111111111111111111111111111);
        r1.setData(1);
        adcExecutor.execute(stateContainerBis, true, null, null, r2, r1, r0.getData(), ArgumentParsers.SHIFT.none());
        stateContainer.registers[0].setData(561);
        r1.setData(13);
        r2.setData(456);
        adcExecutor.execute(stateContainerBis, false, null, null, r0, r2, r1.getData(), ArgumentParsers.SHIFT.parse(stateContainerBis, "LSL#3"));
        assertEquals(stateContainer.registers[0].getData(), r0.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        r0.setData(0b11111111111111111111111111111111);
        r1.setData(1);
        adcExecutor.execute(stateContainer, true, null, null, r2, r1, r0.getData(), ArgumentParsers.SHIFT.none());
        assertTrue(stateContainer.cpsr.getC());
        r0.setData(0b01111111111111111111111111111111);
        r1.setData(0);
        adcExecutor.execute(stateContainer, true, null, null, r2, r1, r0.getData(), ArgumentParsers.SHIFT.none());
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
        assertTrue(stateContainer.cpsr.getV());
        r0.setData(0b11111111111111111111111111111111);
        r1.setData(1);
        adcExecutor.execute(stateContainer, true, null, null, r2, r1, r0.getData(), ArgumentParsers.SHIFT.none());
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
        assertFalse(stateContainer.cpsr.getV());
        r0.setData(0b11111111111111111111111111111111);
        r1.setData(1);
        adcExecutor.execute(stateContainer, true, null, null, r2, r1, r0.getData(), ArgumentParsers.SHIFT.none());
        assertTrue(stateContainer.cpsr.getC());
        r0.setData(0b11111111111111111111111111111111);
        r1.setData(0);
        adcExecutor.execute(stateContainer, true, null, null, r2, r1, r0.getData(), ArgumentParsers.SHIFT.none());
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
        assertFalse(stateContainer.cpsr.getV());
    }

}
