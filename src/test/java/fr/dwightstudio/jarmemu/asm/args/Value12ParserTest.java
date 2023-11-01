package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
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
    }

    @Test
    void hexTest() {
        assertEquals(135, VALUE12.parse(stateContainer,"#0X87"));
    }

    @Test
    void octTest() {
        assertEquals(63, VALUE12.parse(stateContainer,"#0077"));
    }

    @Test
    void binTest() {
        assertEquals(87, VALUE12.parse(stateContainer,"#0B1010111"));
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
