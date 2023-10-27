package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.asm.args.ArgumentParsers;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MOVExecutorTest {

    private StateContainer stateContainer;
    private MOVExecutor movExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        movExecutor = new MOVExecutor();
    }

    @Test
    public void simpleMovTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        movExecutor.execute(stateContainer, false, null, null, r0, 5, ArgumentParsers.SHIFT.none(), null);
        movExecutor.execute(stateContainer, false, null, null, r1, r0.getData(), ArgumentParsers.SHIFT.none(), null);
        r1.setData(r1.getData()+1);
        movExecutor.execute(stateContainer, false, null, null, r2, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertEquals(5, r0.getData());
        assertEquals(6, r1.getData());
        assertEquals(6, r2.getData());
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        movExecutor.execute(stateContainer, true, null, null, r0, 0, ArgumentParsers.SHIFT.none(), null);
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
        movExecutor.execute(stateContainer, true, null, null, r1, -2, ArgumentParsers.SHIFT.none(), null);
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r1.setData(4);
        movExecutor.execute(stateContainer, true, null, null, r2, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
    }

}
