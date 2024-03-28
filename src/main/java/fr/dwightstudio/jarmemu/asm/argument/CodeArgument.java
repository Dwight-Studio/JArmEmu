package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

public class CodeArgument extends ParsedArgument<Integer> {
    private int value;

    public CodeArgument(String originalString) {
        super(originalString);
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws SyntaxASMException {
        value = stateContainer.evalWithAccessibleConsts(originalString);
    }

    @Override
    public Integer getValue(StateContainer stateContainer) throws ExecutionASMException {
        return value;
    }

    @Override
    public Integer getNullValue() throws BadArgumentASMException {
        throw new BadArgumentASMException(JArmEmuApplication.formatMessage("%exception.argument.missingCode"));
    }
}
