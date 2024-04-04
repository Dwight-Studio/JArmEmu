package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterArrayArgumentTest extends ArgumentTest<Register[]> {

    public RegisterArrayArgumentTest() {
        super(RegisterArrayArgument.class);
    }

    @Test
    public void normalTest() throws ASMException {
        StringBuilder stringBuilder = new StringBuilder();
        Register[] registers = new Register[16];

        stringBuilder.append("{");

        for (int i = 0 ; i < 16 ; i++) {
            if (i != 0) stringBuilder.append(",");
            stringBuilder.append("R").append(i);
            registers[i] = stateContainer.getRegister(i);
        }

        stringBuilder.append("}");

        assertArrayEquals(registers, parse(stringBuilder.toString()));
    }

    @Test
    public void twoTypesTest() throws ASMException {
        String string = "{R0-R3, R4}";
        Register[] registers = new Register[5];

        System.arraycopy(stateContainer.getRegisters(), 0, registers, 0, 5);

        assertArrayEquals(registers, parse(string));
    }

    @Test
    public void duplicateTest() throws ASMException {
        StringBuilder stringBuilder = new StringBuilder();
        Register[] registers = new Register[16];

        stringBuilder.append("{");

        for (int i = 0 ; i < 64 ; i++) {
            if (i != 0) stringBuilder.append(",");
            stringBuilder.append("R").append(i % 16);
            if (i < 16) registers[i] = stateContainer.getRegister(i);
        }

        stringBuilder.append("}");

        assertArrayEquals(registers, parse(stringBuilder.toString()));
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

        assertThrows(SyntaxASMException.class, () -> parse(stringBuilder.toString()));
    }
}