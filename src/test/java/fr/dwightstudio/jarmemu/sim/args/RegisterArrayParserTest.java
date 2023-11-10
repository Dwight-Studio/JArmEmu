package fr.dwightstudio.jarmemu.sim.args;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.args.RegisterArrayParser;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterArrayParserTest extends JArmEmuTest {
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
    public void twoTypesTest() {
        String string = "{R0-R3, R4}";
        Register[] registers = new Register[5];

        System.arraycopy(stateContainer.registers, 0, registers, 0, 5);

        assertArrayEquals(registers, REGISTER_ARRAY.parse(stateContainer, string));
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

        REGISTER_ARRAY.logger.setLevel(Level.OFF);
        assertArrayEquals(registers, REGISTER_ARRAY.parse(stateContainer, stringBuilder.toString()));
        REGISTER_ARRAY.logger.setLevel(Level.INFO);
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

        assertThrows(SyntaxASMException.class, () -> REGISTER_ARRAY.parse(stateContainer, stringBuilder.toString()));
    }
}