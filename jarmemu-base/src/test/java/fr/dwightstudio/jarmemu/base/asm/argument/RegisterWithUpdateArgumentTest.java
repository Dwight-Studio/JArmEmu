package fr.dwightstudio.jarmemu.base.asm.argument;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.base.sim.entity.UpdatableRegister;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterWithUpdateArgumentTest extends ArgumentTest<UpdatableRegister> {
    public RegisterWithUpdateArgumentTest() {
        super(RegisterWithUpdateArgument.class);
    }

    protected void parseArray(String s) throws ASMException {
        RegisterArrayArgument arg = new RegisterArrayArgument(s);
        arg.contextualize(stateContainer);
        arg.verify(() -> new StateContainer(stateContainer));
        arg.getValue(stateContainer);
    }

    @Test
    public void normalTest() throws ASMException {
        stateContainer.getRegister(0).setData(404);

        UpdatableRegister reg = parse("R0");
        assertEquals(stateContainer.getRegister(0).getData(), reg.getData());

        parseArray("{R0,R1,R2}");
        reg.update(0);

        assertEquals(404, reg.getData());
    }

    @Test
    public void updateTest() throws ASMException {
        stateContainer.getRegister(0).setData(404);

        UpdatableRegister reg = parse("R0!");
        assertEquals(stateContainer.getRegister(0).getData(), reg.getData());

        parseArray("{R0,R1,R2}");
        reg.update(-12);

        assertEquals(392, reg.getData());
    }

    @Test
    public void failTest() {
        assertThrows(SyntaxASMException.class, () -> parse("!R1"));
        assertThrows(SyntaxASMException.class, () -> parse("R16!"));
        assertThrows(SyntaxASMException.class, () -> parse("R!"));
        assertThrows(SyntaxASMException.class, () -> parse("R17"));
    }
}