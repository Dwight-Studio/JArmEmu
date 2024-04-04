package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImmediateOrRegisterArgumentTest extends ArgumentTest<Integer> {

    public ImmediateOrRegisterArgumentTest() {
        super(ImmediateOrRegisterArgument.class);
    }

    @BeforeEach
    public void setUp() {
        super.setUp();
        
        for (int i = 0 ; i < 16 ; i++) {
            stateContainer.getRegister(i).setData(i);
        }

        stateContainer.getCPSR().setData(16);
        stateContainer.getSPSR().setData(17);
    }

    @Test
    public void valueTest() throws ASMException {
        assertEquals(48, parse( "#48"));
        assertEquals(1, parse( "#0B01"));
        assertEquals(8, parse( "#0010"));
        assertEquals(16, parse( "#0X010"));

        assertThrows(SyntaxASMException.class, () -> parse( "#R14"));
        assertThrows(SyntaxASMException.class, () -> parse( "#0XR14"));
        assertThrows(SyntaxASMException.class, () -> parse( "#LR"));
    }

    @Test
    public void registerTest() throws ASMException {
        for (int i = 0 ; i < 16 ; i++) {
            assertEquals(i, parse( "R" + i));
        }

        assertEquals(13, parse( "SP"));
        assertEquals(14, parse( "LR"));
        assertEquals(15, parse( "PC"));

        assertEquals(16, parse( "CPSR"));
        assertEquals(17, parse( "SPSR"));

        assertThrows(SyntaxASMException.class, () -> parse( "R16"));
        assertThrows(SyntaxASMException.class, () -> parse( "48"));
        assertThrows(SyntaxASMException.class, () -> parse( "1R"));
        assertThrows(SyntaxASMException.class, () -> parse( "4LR"));
    }
}