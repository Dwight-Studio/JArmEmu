package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.RegisterWithUpdateParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.dwightstudio.jarmemu.asm.UpdateMode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class STMExecutorTest extends JArmEmuTest {
    private StateContainer stateContainer;
    private STMExecutor stmExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        stmExecutor = new STMExecutor();
    }

    @Test
    public void simpleStmTest() {
        Register sp = stateContainer.registers[13];
        sp.setData(1000);
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        r0.setData(54);
        r1.setData(12);
        r2.setData(65);
        stmExecutor.execute(stateContainer, false, null, FD, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, stateContainer.memory.getWord(996));
        assertEquals(12, stateContainer.memory.getWord(992));
        assertEquals(54, stateContainer.memory.getWord(988));
        assertEquals(988, sp.getData());
        sp.setData(2000);
        stmExecutor.execute(stateContainer, false, null, FA, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, stateContainer.memory.getWord(2004));
        assertEquals(12, stateContainer.memory.getWord(2008));
        assertEquals(54, stateContainer.memory.getWord(2012));
        assertEquals(2012, sp.getData());
        sp.setData(3000);
        stmExecutor.execute(stateContainer, false, null, ED, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, stateContainer.memory.getWord(3000));
        assertEquals(12, stateContainer.memory.getWord(2996));
        assertEquals(54, stateContainer.memory.getWord(2992));
        assertEquals(2988, sp.getData());
        sp.setData(4000);
        stmExecutor.execute(stateContainer, false, null, EA, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, stateContainer.memory.getWord(4000));
        assertEquals(12, stateContainer.memory.getWord(4004));
        assertEquals(54, stateContainer.memory.getWord(4008));
        assertEquals(4012, sp.getData());
    }
}
