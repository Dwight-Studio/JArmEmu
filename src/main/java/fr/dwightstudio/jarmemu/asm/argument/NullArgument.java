package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Supplier;

public class NullArgument extends ParsedArgument<Object> {
    public NullArgument(String originalString) throws BadArgumentASMException {
        super(originalString);

        throw new BadArgumentASMException(JArmEmuApplication.formatMessage("%exception.argument.unexpected", originalString));
    }

    @Override
    public Object getValue(StateContainer stateContainer) throws ExecutionASMException {
        return null;
    }

    @Override
    public Object getNullValue() throws BadArgumentASMException {
        return null;
    }

}
