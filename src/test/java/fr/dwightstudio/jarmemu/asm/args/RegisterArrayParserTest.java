package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterArrayParserTest {
    private StateContainer stateContainer;
    private static final RegisterArrayParser REGISTER_ARRAY = new RegisterArrayParser();

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    public void normalTest() {
        StringBuilder stringBuilder = new StringBuilder();
        Register[] registers = new Register[16];

        stringBuilder.append("{");

        for (int i = 0 ; i < 16 ; i++) {
            if (i != 0) stringBuilder.append(",");
            stringBuilder.append("R").append(i);
            registers[i] = stateContainer.registers[i];
        }

        stringBuilder.append("}");

        assertArrayEquals(registers, REGISTER_ARRAY.parse(stateContainer, stringBuilder.toString()));
    }

    @Test
    public void duplicateTest() {
        StringBuilder stringBuilder = new StringBuilder();
        Register[] registers = new Register[16];

        stringBuilder.append("{");

        for (int i = 0 ; i < 64 ; i++) {
            if (i != 0) stringBuilder.append(",");
            stringBuilder.append("R").append(i % 16);
            if (i < 16) registers[i] = stateContainer.registers[i];
        }

        stringBuilder.append("}");

        assertArrayEquals(registers, REGISTER_ARRAY.parse(stateContainer, stringBuilder.toString()));
    }

    @Test
    public void failTest() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{");

        for (int i = 0 ; i < 32 ; i++) {
            if (i != 0) stringBuilder.append(",");
            stringBuilder.append("R").append(i);
        }

        stringBuilder.append("}");

        assertThrows(AssemblySyntaxException.class, () -> REGISTER_ARRAY.parse(stateContainer, stringBuilder.toString()));
    }
}