package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.args.ArgumentParsers;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RSBExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private StateContainer stateContainerBis;
    private RSBExecutor rsbExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        stateContainerBis = new StateContainer();
        rsbExecutor = new RSBExecutor();
    }

    @Test
    public void simpleRsbTest() {
        stateContainer.registers[0].setData(-15);
        Register r0 = stateContainerBis.registers[0];
        r0.setData(99);
        Register r1 = stateContainerBis.registers[1];
        r1.setData(20);
        Register r2 = stateContainerBis.registers[2];
        r2.setData(5);
        rsbExecutor.execute(stateContainerBis, false, null, null, r0, r1, r2.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(stateContainer.registers[0].getData(), r0.getData());
        stateContainer.registers[0].setData(0b01111111111111111111111111111111);
        r0.setData(1);
        r1.setData(0b10000000000000000000000000000000);
        rsbExecutor.execute(stateContainerBis, false, null, null, r2, r0, r1.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(stateContainer.registers[0].getData(), r2.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        r0.setData(1);
        r1.setData(0b10000000000000000000000000000000);
        rsbExecutor.execute(stateContainer, true, null, null, r2, r0, r1.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(0b01111111111111111111111111111111, r2.getData());
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
        assertTrue(stateContainer.cpsr.getV());
        r0.setData(0b11111111111111111111111111111111);
        r1.setData(0b11111111111111111111111111111111);
        rsbExecutor.execute(stateContainer, true, null, null, r2, r0, r1.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(0, r2.getData());
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
        assertTrue(stateContainer.cpsr.getC());
        assertFalse(stateContainer.cpsr.getV());
        r0.setData(0b11111111111111111111111111111111);
        r1.setData(0b01111111111111111111111111111111);
        rsbExecutor.execute(stateContainer, true, null, null, r2, r0, r1.getData(), ArgumentParsers.SHIFT.none());
        assertEquals(0b10000000000000000000000000000000, r2.getData());
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        assertFalse(stateContainer.cpsr.getC());
        assertTrue(stateContainer.cpsr.getV());
    }

}
