package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.args.ArgumentParsers;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ADDExecutorTest {

    private StateContainer stateContainer;
    private StateContainer stateContainerBis;
    private ADDExecutor addExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        stateContainerBis = new StateContainer();
        addExecutor = new ADDExecutor();
    }

    @Test
    public void simpleAddTest() {
        stateContainer.registers[0].setData(25);
        Register r0 = stateContainerBis.registers[0];
        r0.setData(99);
        Register r1 = stateContainerBis.registers[1];
        r1.setData(5);
        Register r2 = stateContainerBis.registers[2];
        r2.setData(20);
        addExecutor.execute(stateContainerBis, false, null, null, r0, r1, r2.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(stateContainer.registers[0].getData(), r0.getData());
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
        addExecutor.execute(stateContainerBis, false, null, null, r0, r2, r1.getData(), ArgumentParsers.SHIFT.parse(stateContainerBis, "LSL#3"));
        assertEquals(stateContainer.registers[0].getData(), r0.getData());
    }

    @Test
    public void instantValueAddTest() {
        stateContainer.registers[0].setData(65736);
        Register r0 = stateContainerBis.registers[0];
        r0.setData(99);
        Register r1 = stateContainerBis.registers[1];
        r1.setData(456);
        addExecutor.execute(stateContainerBis, false, null, null, r0, r1, 0xFF00, ArgumentParsers.SHIFT.none());
        assertEquals(stateContainer.registers[0].getData(), r0.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        r0.setData(-55);
        Register r1 = stateContainer.registers[1];
        r1.setData(45);
        Register r2 = stateContainer.registers[2];
        addExecutor.execute(stateContainer, true, null, null, r2, r1, r0.getData(), ArgumentParsers.SHIFT.none());
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
        assertFalse(stateContainer.cpsr.getV());
        addExecutor.execute(stateContainer, false, null, null, r2, r2, 10, ArgumentParsers.SHIFT.none());
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
        assertFalse(stateContainer.cpsr.getV());
        addExecutor.execute(stateContainer, true, null, null, r2, r2, 0, ArgumentParsers.SHIFT.none());
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
        assertFalse(stateContainer.cpsr.getV());
        r0.setData(0b01111111111111111111111111111111);
        r1.setData(1);
        addExecutor.execute(stateContainer, true, null, null, r2, r1, r0.getData(), ArgumentParsers.SHIFT.none());
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
        assertTrue(stateContainer.cpsr.getV());
        r0.setData(0b11111111111111111111111111111111);
        r1.setData(1);
        addExecutor.execute(stateContainer, true, null, null, r2, r1, r0.getData(), ArgumentParsers.SHIFT.none());
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
        assertFalse(stateContainer.cpsr.getV());
        r0.setData(0b10000000000000000000000000000000);
        r1.setData(0b10000000000000000000000000000000);
        addExecutor.execute(stateContainer, true, null, null, r2, r1, r0.getData(), ArgumentParsers.SHIFT.none());
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
        assertTrue(stateContainer.cpsr.getV());
    }

}
