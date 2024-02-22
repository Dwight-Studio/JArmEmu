package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Supplier;

public class CodeArgument extends ParsedArgument<Integer> {
    public CodeArgument(String originalString) {
        super(originalString);
    }

    private int value;

    @Override
    protected void parse(String originalString) throws SyntaxASMException {

    }

    @Override
    public Integer getValue(StateContainer stateContainer) throws ExecutionASMException {
        return value;
    }

    @Override
    public Integer getNullValue() throws BadArgumentASMException {
        throw new BadArgumentASMException(JArmEmuApplication.formatMessage("%exception.argument.missingCode"));
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier, int currentLine) throws ASMException {
        value = stateSupplier.get().evalWithAccessibleConsts(originalString);
    }
}
