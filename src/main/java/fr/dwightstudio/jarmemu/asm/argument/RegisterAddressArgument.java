package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Supplier;

public class RegisterAddressArgument extends ParsedArgument<Integer> {

    RegisterArgument argument;

    public RegisterAddressArgument(String originalString) {
        super(originalString);
    }

    @Override
    protected void parse(String originalString) throws SyntaxASMException {
        if (originalString.startsWith("[") && originalString.endsWith("]")) {
            String string = originalString.substring(1, originalString.length()-1).strip();
            argument = new RegisterArgument(string);
        } else {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.invalidAddressRegister", originalString));
        }
    }

    @Override
    public Integer getValue(StateContainer stateContainer) throws ExecutionASMException {
        return argument.getValue(stateContainer).getData();
    }

    @Override
    public Integer getNullValue() throws BadArgumentASMException {
        throw new BadArgumentASMException(JArmEmuApplication.formatMessage("%exception.argument.missingRegisterAddress"));
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier, int currentLine) throws ASMException {
        argument.verify(stateSupplier, currentLine);
        super.verify(stateSupplier, currentLine);
    }
}
