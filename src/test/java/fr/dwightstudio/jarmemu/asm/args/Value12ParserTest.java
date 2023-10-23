package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Value12ParserTest {

    private StateContainer stateContainer;
    private static final Value12Parser VALUE12 = new Value12Parser();

    @BeforeEach
    void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    void decTest() {
        assertEquals(87, VALUE12.parse(stateContainer,"#87"));
        assertEquals(397, VALUE12.parse(stateContainer,"#397"));
        assertEquals(-744, VALUE12.parse(stateContainer,"#-744"));
        assertEquals(2001, VALUE12.parse(stateContainer,"#2001"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE12.parse(stateContainer,"#2048"));
    }

    @Test
    void hexTest() {
        assertEquals(135, VALUE12.parse(stateContainer,"#0x87"));
        assertEquals(2041, VALUE12.parse(stateContainer,"#0x7F9"));
        assertEquals(-69, VALUE12.parse(stateContainer,"#-0x45"));
        assertEquals(2032, VALUE12.parse(stateContainer,"#0x7f0"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE12.parse(stateContainer,"#0xFFF"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE12.parse(stateContainer,"#0xP"));
    }

    @Test
    void octTest() {
        assertEquals(63, VALUE12.parse(stateContainer,"#0077"));
        assertEquals(2041, VALUE12.parse(stateContainer,"#003771"));
        assertEquals(-69, VALUE12.parse(stateContainer,"#-00105"));
        assertEquals(2034, VALUE12.parse(stateContainer,"#003762"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE12.parse(stateContainer,"#005670"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE12.parse(stateContainer,"#008"));
    }

    @Test
    void binTest() {
        assertEquals(87, VALUE12.parse(stateContainer,"#0b1010111"));
        assertEquals(2047, VALUE12.parse(stateContainer,"#0b11111111111"));
        assertEquals(-774, VALUE12.parse(stateContainer,"#-0b1100000110"));
        assertEquals(2029, VALUE12.parse(stateContainer,"#0b11111101101"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE12.parse(stateContainer,"#0b1001010010110"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE12.parse(stateContainer,"#0b475"));
    }

    @Test
    void failTest() {
        assertThrows(AssemblySyntaxException.class, () -> VALUE12.parse(stateContainer,"#udhad"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE12.parse(stateContainer,"#0b0xff"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE12.parse(stateContainer,"#7440b"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE12.parse(stateContainer,"# 0b01"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE12.parse(stateContainer,"#"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE12.parse(stateContainer,"=48"));
        assertThrows(AssemblySyntaxException.class, () -> VALUE12.parse(stateContainer,"-4"));
    }
}
