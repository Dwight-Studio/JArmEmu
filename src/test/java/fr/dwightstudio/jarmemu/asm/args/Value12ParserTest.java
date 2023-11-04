package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.args.Value12Parser;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Value12ParserTest {
    // TODO: Refaire tous les tests
    private StateContainer stateContainer;
    private static final Value12Parser VALUE12 = new Value12Parser();

    @BeforeEach
    void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    void decTest() {
        assertEquals(87, VALUE12.parse(stateContainer,"#87"));
        assertEquals(65280, VALUE12.parse(stateContainer,"#65280"));
        assertEquals(1020, VALUE12.parse(stateContainer,"#1020"));
        assertEquals(-2147483648, VALUE12.parse(stateContainer,"#-2147483648"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer, "#258"));
    }

    @Test
    void hexTest() {
        assertEquals(-16777216, VALUE12.parse(stateContainer,"#0XFF000000"));
        assertEquals(1044480, VALUE12.parse(stateContainer,"#0X000FF000"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer, "#0X01020000"));
    }

    @Test
    void octTest() {
        assertEquals(63, VALUE12.parse(stateContainer,"#0077"));
        assertEquals(183500800, VALUE12.parse(stateContainer,"#001274000000"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer, "#002010000000"));
    }

    @Test
    void binTest() {
        assertEquals(87, VALUE12.parse(stateContainer,"#0B1010111"));
        assertEquals(-2147483648, VALUE12.parse(stateContainer,"#0B10000000000000000000000000000000"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer, "#0B01111111100000000000000000000000"));

    }

    @Test
    void failTest() {
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer,"#udhad"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer,"#0B0xff"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer,"#7440b"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer,"# 0b01"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer,"#"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer,"=#48"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer,"-4"));
    }
}
