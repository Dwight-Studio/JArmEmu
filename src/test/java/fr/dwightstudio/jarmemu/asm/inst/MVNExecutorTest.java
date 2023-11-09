package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.args.ArgumentParsers;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MVNExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private MVNExecutor mvnExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        mvnExecutor = new MVNExecutor();
    }

    @Test
    public void simpleMvnTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        mvnExecutor.execute(stateContainer, false, null, null, r0, Integer.MIN_VALUE, ArgumentParsers.SHIFT.none(), null);
        assertEquals(Integer.MAX_VALUE, r0.getData());
        mvnExecutor.execute(stateContainer, false, null, null, r1, r0.getData(), ArgumentParsers.SHIFT.none(), null);
        assertEquals(Integer.MIN_VALUE, r1.getData());
        mvnExecutor.execute(stateContainer, false, null, null, r2, 0b10101010101010101010010001000001, ArgumentParsers.SHIFT.none(), null);
        assertEquals(~0b10101010101010101010010001000001, r2.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        mvnExecutor.execute(stateContainer, true, null, null, r0, 0, ArgumentParsers.SHIFT.none(), null);
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        mvnExecutor.execute(stateContainer, true, null, null, r1, -2, ArgumentParsers.SHIFT.none(), null);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r1.setData(-1);
        mvnExecutor.execute(stateContainer, true, null, null, r2, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
    }

}
