package fr.dwightstudio.jarmemu.base.asm.argument;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.ShiftFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShiftArgumentTest extends ArgumentTest<ShiftFunction> {
    public ShiftArgumentTest() {
        super(ShiftArgument.class);
    }

    @Test
    public void LSLTest() throws ASMException {
        int data = 0b00000000000000000000000000000001;
        stateContainer.getRegister(0).setData(0b0000000000000000000000000000010);
        stateContainer.getLR().setData(0b00000000000000000000000000000100);
        ShiftFunction f;

        f = parse("LSL#5");
        assertEquals(0b00000000000000000000000000100000, f.apply(data));

        f = parse("LSLR0");
        assertEquals(0b00000000000000000000000000000100, f.apply(data));

        f = parse("LSLLR");
        assertEquals(0b00000000000000000000000000010000, f.apply(data));
    }

    @Test
    public void LSRTest() throws ASMException {
        int data = 0b10000000000000000000000000000000;
        stateContainer.getRegister(0).setData(0b0000000000000000000000000000010);
        stateContainer.getLR().setData(0b00000000000000000000000000000100);
        ShiftFunction f;

        f = parse("LSR#5");
        assertEquals(0b00000100000000000000000000000000, f.apply(data));

        f = parse("LSRR0");
        assertEquals(0b00100000000000000000000000000000, f.apply(data));

        f = parse("LSRLR");
        assertEquals(0b00001000000000000000000000000000, f.apply(data));
    }

    @Test
    public void ASRTest() throws ASMException {
        int data = 0b10000000000000000000000000000000;
        stateContainer.getRegister(0).setData(0b0000000000000000000000000000010);
        stateContainer.getLR().setData(0b00000000000000000000000000000100);
        ShiftFunction f;

        f = parse("ASR#5");
        assertEquals(0b11111100000000000000000000000000, f.apply(data));

        f = parse("ASRR0");
        assertEquals(0b11100000000000000000000000000000, f.apply(data));

        f = parse("ASRLR");
        assertEquals(0b11111000000000000000000000000000, f.apply(data));

        data = 0b01000000000000000000000000000000;

        f = parse("ASR#5");
        assertEquals(0b00000010000000000000000000000000, f.apply(data));

        f = parse("ASRR0");
        assertEquals(0b00010000000000000000000000000000, f.apply(data));

        f = parse("ASRLR");
        assertEquals(0b00000100000000000000000000000000, f.apply(data));
    }

    @Test
    public void RORTest() throws ASMException {
        int data = 0b00000000000000000000000000001000;
        stateContainer.getRegister(0).setData(0b0000000000000000000000000000010);
        stateContainer.getLR().setData(0b00000000000000000000000000000100);
        ShiftFunction f;

        f = parse("ROR#5");
        assertEquals(0b01000000000000000000000000000000, f.apply(data));

        f = parse("RORR0");
        assertEquals(0b00000000000000000000000000000010, f.apply(data));

        f = parse("RORLR");
        assertEquals(0b10000000000000000000000000000000, f.apply(data));
    }

    @Test
    public void RRXTest() throws ASMException {
        int data;
        ShiftFunction f = parse("RRX");

        data = 0b00000000000000000000000000001000;
        stateContainer.getCPSR().setC(false);
        assertEquals(0b00000000000000000000000000000100, f.apply(data));
        assertFalse(stateContainer.getCPSR().getC());

        data = 0b00000000000000000000000000010000;
        stateContainer.getCPSR().setC(true);
        f = parse("RRX");
        assertEquals(0b10000000000000000000000000001000, f.apply(data));
        assertFalse(stateContainer.getCPSR().getC());

        data = 0b00000000000000000000000000001001;
        stateContainer.getCPSR().setC(false);
        f = parse("RRX");
        assertEquals(0b00000000000000000000000000000100, f.apply(data));
        assertTrue(stateContainer.getCPSR().getC());

        data = 0b00000000000000000000000000010001;
        stateContainer.getCPSR().setC(true);
        f = parse("RRX");
        assertEquals(0b10000000000000000000000000001000, f.apply(data));
        assertTrue(stateContainer.getCPSR().getC());
    }
}