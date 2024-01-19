package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BFCExecutorTest {

    private StateContainer stateContainer;
    private BFCExecutor bfcExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        bfcExecutor = new BFCExecutor();
    }

    @Test
    public void simpleBfcTest() {
        Random rand = new Random();
        Register r2 = stateContainer.getRegister(2);
        r2.setData(0b010101010101010);
        bfcExecutor.execute(stateContainer, false, false, null, null, r2, 3, 4, null);
        assertEquals(0b010101010000010, r2.getData());
        for (int i = 0; i < 1000; i++) {
            int value = rand.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
            r2.setData(value);
            bfcExecutor.execute(stateContainer, false, false, null, null, r2, 28, 4, null);
            assertEquals(value & 0xFFFFFFF, r2.getData());
            r2.setData(value);
            bfcExecutor.execute(stateContainer, false, false, null, null, r2, 0, 4, null);
            assertEquals(value & 0xFFFFFFF0, r2.getData());
        }
    }

    @Test
    public void edgeCaseTest() {
        Register r2 = stateContainer.getRegister(2);
        r2.setData(15);
        bfcExecutor.execute(stateContainer, false, false, null, null, r2, 0, 32, null);
        assertEquals(0, r2.getData());
        r2.setData(15);
        bfcExecutor.execute(stateContainer, false, false, null, null, r2, 1, 31, null);
        assertEquals(1, r2.getData());
    }

    @Test
    public void failAdrTest() {
        Register r2 = stateContainer.getRegister(2);
        r2.setData(0b010101010101010);
        assertThrows(SyntaxASMException.class, () -> bfcExecutor.execute(stateContainer, false, true, null, null, r2, -2, 4, null));
        assertThrows(SyntaxASMException.class, () -> bfcExecutor.execute(stateContainer, false, true, null, null, r2, 3, 32, null));
    }

}
