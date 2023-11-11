package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MULExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private MULExecutor mulExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        mulExecutor = new MULExecutor();
    }

    @Test
    public void simpleMulTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        r0.setData(16);
        r1.setData(16);
        mulExecutor.execute(stateContainer, false, null, null, r2, r1, r0, ArgumentParsers.SHIFT.none());
        assertEquals(16*16, r2.getData());
        r1.setData(-1);
        mulExecutor.execute(stateContainer, false, null, null, r0, r1, r2, ArgumentParsers.SHIFT.none());
        assertEquals(-16*16, r0.getData());
        r0.setData(8594297);
        r1.setData(859425);
        mulExecutor.execute(stateContainer, false, null, null, r2, r1, r0, ArgumentParsers.SHIFT.none());
        assertEquals(-1190049895, r2.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        r0.setData(16);
        r1.setData(16);
        mulExecutor.execute(stateContainer, true, null, null, r1, r1, r0, ArgumentParsers.SHIFT.none());
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r1.setData(-1);
        mulExecutor.execute(stateContainer, true, null, null, r1, r1, r0, ArgumentParsers.SHIFT.none());
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r1.setData(0);
        mulExecutor.execute(stateContainer, true, null, null, r1, r1, r0, ArgumentParsers.SHIFT.none());
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
    }

}
