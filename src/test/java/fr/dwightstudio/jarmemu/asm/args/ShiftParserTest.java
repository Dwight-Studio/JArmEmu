package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftParserTest {

    private StateContainer stateContainer;
    private static final ShiftParser SHIFT = new ShiftParser();

    @BeforeEach
    void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    public void LSLTest() {
        int data = 0b00000000000000000000000000000001;
        stateContainer.registers[0].setData(0b0000000000000000000000000000010);
        stateContainer.registers[14].setData(0b00000000000000000000000000000100);
        Function<Integer, Integer> f;

        f = SHIFT.parse(stateContainer, "LSL#5");
        assertEquals(0b00000000000000000000000000100000, f.apply(data));

        f = SHIFT.parse(stateContainer, "LSLR0");
        assertEquals(0b00000000000000000000000000000100, f.apply(data));

        f = SHIFT.parse(stateContainer, "LSLLR");
        assertEquals(0b00000000000000000000000000010000, f.apply(data));
    }

    @Test
    public void LSRTest() {
        int data = 0b10000000000000000000000000000000;
        stateContainer.registers[0].setData(0b0000000000000000000000000000010);
        stateContainer.registers[14].setData(0b00000000000000000000000000000100);
        Function<Integer, Integer> f;

        f = SHIFT.parse(stateContainer, "LSR#5");
        assertEquals(0b00000100000000000000000000000000, f.apply(data));

        f = SHIFT.parse(stateContainer, "LSRR0");
        assertEquals(0b00100000000000000000000000000000, f.apply(data));

        f = SHIFT.parse(stateContainer, "LSRLR");
        assertEquals(0b00001000000000000000000000000000, f.apply(data));
    }

    @Test
    public void ASRTest() {
        int data = 0b10000000000000000000000000000000;
        Function<Integer, Integer> f;

        f = SHIFT.parse(stateContainer, "ASR#5");
        System.out.println(Integer.toBinaryString(0b10000100000000000000000000000000));
        System.out.println(Integer.toBinaryString(f.apply(data)));
        assertEquals(0b11111100000000000000000000000000, f.apply(data));

        f = SHIFT.parse(stateContainer, "ASRR0");
        assertEquals(0b11100000000000000000000000000000, f.apply(data));

        f = SHIFT.parse(stateContainer, "ASRLR");
        assertEquals(0b11111000000000000000000000000000, f.apply(data));

        data = 0b01000000000000000000000000000000;

        f = SHIFT.parse(stateContainer, "ASR#5");
        assertEquals(0b00000010000000000000000000000000, f.apply(data));

        f = SHIFT.parse(stateContainer, "ASRR0");
        assertEquals(0b00010000000000000000000000000000, f.apply(data));

        f = SHIFT.parse(stateContainer, "ASRLR");
        assertEquals(0b00000100000000000000000000000000, f.apply(data));
    }

    // TODO: Tester RRX et ROR
}
