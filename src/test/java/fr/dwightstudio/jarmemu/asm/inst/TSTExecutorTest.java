package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TSTExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private TSTExecutor tstExecutor;

    @BeforeEach
    public void setup() {
        stateContainer = new StateContainer();
        tstExecutor = new TSTExecutor();
    }

    @Test
    public void flagsTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        r0.setData(0b110101);
        r1.setData(0b110101);
        tstExecutor.execute(stateContainer, false, false, null, null, r0, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertFalse(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
        r0.setData(0b0011);
        r1.setData(0b1100);
        tstExecutor.execute(stateContainer, false, false, null, null, r0, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertFalse(stateContainer.cpsr.getN());
        assertTrue(stateContainer.cpsr.getZ());
        r0.setData(0b11111111111111111111111111111111);
        r1.setData(0b11111111111111111111111111111111);
        tstExecutor.execute(stateContainer, false, false, null, null, r0, r1.getData(), ArgumentParsers.SHIFT.none(), null);
        assertTrue(stateContainer.cpsr.getN());
        assertFalse(stateContainer.cpsr.getZ());
    }

}
