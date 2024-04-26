package fr.dwightstudio.jarmemu.base.asm.argument;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LabelArgumentTest extends ArgumentTest<Integer> {

    public LabelArgumentTest() {
        super(LabelArgument.class);
    }

    @Test
    public void parseTest() throws ASMException {
        stateContainer.getAccessibleLabels().put("COUCOU", 23);

        assertEquals(23, parse("COUCOU"));
        assertThrows(SyntaxASMException.class, () -> parse("PASCOUCOU"));
        assertThrows(SyntaxASMException.class, () -> parse("COUCOU:"));
    }
}