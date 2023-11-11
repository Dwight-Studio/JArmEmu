package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.parse.args.AddressParser;
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParsers;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.dwightstudio.jarmemu.asm.DataMode.BYTE;
import static fr.dwightstudio.jarmemu.asm.DataMode.HALF_WORD;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class STRExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private STRExecutor strExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        strExecutor = new STRExecutor();
    }

    @Test
    public void simpleStrTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        Register r3 = stateContainer.registers[3];
        r0.setData(100);
        r1.setData(104);
        r2.setData(106);
        r3.setData(54);
        strExecutor.execute(stateContainer, false, null, null, r3, new AddressParser.UpdatableInteger(r0.getData(), stateContainer, false, false, null), 0, ArgumentParsers.SHIFT.none());
        assertEquals(54, stateContainer.memory.getWord(100));
        strExecutor.execute(stateContainer, false, HALF_WORD, null, r3, new AddressParser.UpdatableInteger(r1.getData(), stateContainer, false, false, null), 0, ArgumentParsers.SHIFT.none());
        assertEquals(54, stateContainer.memory.getHalf(104));
        strExecutor.execute(stateContainer, false, BYTE, null, r3, new AddressParser.UpdatableInteger(r2.getData(), stateContainer, false, false, null), 0, ArgumentParsers.SHIFT.none());
        assertEquals(54, stateContainer.memory.getByte(106));
    }

}
