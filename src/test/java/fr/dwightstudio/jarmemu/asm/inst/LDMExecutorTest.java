package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.parse.args.RegisterWithUpdateParser;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.dwightstudio.jarmemu.asm.UpdateMode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LDMExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private LDMExecutor ldmExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        ldmExecutor = new LDMExecutor();
    }

    @Test
    public void simpleLdmTest() {
        Register sp = stateContainer.registers[13];
        sp.setData(988);
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        stateContainer.memory.putWord(988, 54);
        stateContainer.memory.putWord(992, 12);
        stateContainer.memory.putWord(996, 65);
        ldmExecutor.execute(stateContainer, false, null, FD, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(1000, sp.getData());
        sp.setData(2012);
        stateContainer.memory.putWord(2012, 54);
        stateContainer.memory.putWord(2008, 12);
        stateContainer.memory.putWord(2004, 65);
        ldmExecutor.execute(stateContainer, false, null, FA, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(2000, sp.getData());
        sp.setData(2988);
        stateContainer.memory.putWord(2992, 54);
        stateContainer.memory.putWord(2996, 12);
        stateContainer.memory.putWord(3000, 65);
        ldmExecutor.execute(stateContainer, false, null, ED, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(3000, sp.getData());
        sp.setData(4012);
        stateContainer.memory.putWord(4008, 54);
        stateContainer.memory.putWord(4004, 12);
        stateContainer.memory.putWord(4000, 65);
        ldmExecutor.execute(stateContainer, false, null, EA, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(4000, sp.getData());
    }
}
