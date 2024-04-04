package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RotatedImmediateArgumentTest extends ArgumentTest<Integer> {
    public RotatedImmediateArgumentTest() {
        super(RotatedImmediateArgument.class);
    }

    @Test
    void decTest() throws ASMException {
        assertEquals(87, parse("#87"));
        assertEquals(65280, parse("#65280"));
        assertEquals(1020, parse("#1020"));
        assertEquals(-2147483648, parse("#-2147483648"));
        assertThrows(SyntaxASMException.class, () -> parse( "#258"));
    }

    @Test
    void hexTest() throws ASMException {
        assertEquals(-16777216, parse("#0XFF000000"));
        assertEquals(1044480, parse("#0X000FF000"));
        assertThrows(SyntaxASMException.class, () -> parse( "#0X01020000"));
    }

    @Test
    void octTest() throws ASMException {
        assertEquals(63, parse("#0077"));
        assertEquals(183500800, parse("#001274000000"));
        assertThrows(SyntaxASMException.class, () -> parse( "#002010000000"));
    }

    @Test
    void binTest() throws ASMException {
        assertEquals(87, parse("#0B1010111"));
        assertEquals(-2147483648, parse("#0B10000000000000000000000000000000"));
        assertThrows(SyntaxASMException.class, () -> parse( "#0B01111111100000000000000000000000"));

    }

    @Test
    void failTest() {
        assertThrows(SyntaxASMException.class, () -> parse("#udhad"));
        assertThrows(SyntaxASMException.class, () -> parse("#0B0xff"));
        assertThrows(SyntaxASMException.class, () -> parse("#7440b"));
        assertThrows(SyntaxASMException.class, () -> parse("#"));
        assertThrows(IllegalStateException.class, () -> parse("=#48"));
        assertThrows(SyntaxASMException.class, () -> parse("-4"));
    }
}