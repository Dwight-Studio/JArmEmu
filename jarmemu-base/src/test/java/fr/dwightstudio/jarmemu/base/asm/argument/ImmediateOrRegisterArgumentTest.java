package fr.dwightstudio.jarmemu.base.asm.argument;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.base.util.RegisterUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImmediateOrRegisterArgumentTest extends ArgumentTest<ImmediateOrRegisterArgument.RegisterOrImmediate> {

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
        assertEquals(new ImmediateOrRegisterArgument.RegisterOrImmediate(48), parse( "#48"));
        assertEquals(new ImmediateOrRegisterArgument.RegisterOrImmediate(1), parse( "#0B01"));
        assertEquals(new ImmediateOrRegisterArgument.RegisterOrImmediate(8), parse( "#0010"));
        assertEquals(new ImmediateOrRegisterArgument.RegisterOrImmediate(16), parse( "#0X010"));

        assertThrows(SyntaxASMException.class, () -> parse( "#R14"));
        assertThrows(SyntaxASMException.class, () -> parse( "#0XR14"));
        assertThrows(SyntaxASMException.class, () -> parse( "#LR"));
    }

    @Test
    public void registerTest() throws ASMException {
        for (int i = 0 ; i < 16 ; i++) {
            assertEquals(new ImmediateOrRegisterArgument.RegisterOrImmediate(stateContainer.getRegister(i)), parse( "R" + i));
        }

        assertEquals(new ImmediateOrRegisterArgument.RegisterOrImmediate(stateContainer.getSP()), parse( "SP"));
        assertEquals(new ImmediateOrRegisterArgument.RegisterOrImmediate(stateContainer.getLR()), parse( "LR"));
        assertEquals(new ImmediateOrRegisterArgument.RegisterOrImmediate(stateContainer.getPC()), parse( "PC"));

        assertEquals(new ImmediateOrRegisterArgument.RegisterOrImmediate(stateContainer.getCPSR()), parse( "CPSR"));
        assertEquals(new ImmediateOrRegisterArgument.RegisterOrImmediate(stateContainer.getSPSR()), parse( "SPSR"));

        assertThrows(SyntaxASMException.class, () -> parse( "R16"));
        assertThrows(SyntaxASMException.class, () -> parse( "48"));
        assertThrows(SyntaxASMException.class, () -> parse( "1R"));
        assertThrows(SyntaxASMException.class, () -> parse( "4LR"));
    }
}