package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LabelOrRegisterArgumentTest extends ArgumentTest<Integer> {

    public LabelOrRegisterArgumentTest() {
        super(LabelOrRegisterArgument.class);
    }

    @Test
    public void parseTest() throws ASMException {
        stateContainer.getAccessibleLabels().put("COUCOU", 23);
        stateContainer.getAccessibleLabels().put("R1", 43);
        stateContainer.getRegister(12).setData(13);
        stateContainer.getRegister(1).setData(14);

        assertEquals(23, parse("COUCOU"));
        assertEquals(13, parse("R12"));
        assertEquals(14, parse("R1"));
        assertThrows(SyntaxASMException.class, () -> parse("PASCOUCOU"));
        assertThrows(SyntaxASMException.class, () -> parse("R99"));
        assertThrows(SyntaxASMException.class, () -> parse("COUCOU:"));
    }
}