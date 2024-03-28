package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

public class NullArgument extends ParsedArgument<Object> {
    public NullArgument(String originalString) throws BadArgumentASMException {
        super(originalString);

        throw new BadArgumentASMException(JArmEmuApplication.formatMessage("%exception.argument.unexpected", originalString));
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {

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
