package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

import java.util.function.Supplier;

public class LabelArgument extends ParsedArgument<Integer> {

    public LabelArgument(String originalString) {
        super(originalString);
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {

    }

    @Override
    public Integer getValue(StateContainer stateContainer) throws ExecutionASMException {
        return stateContainer.getAccessibleLabels().get(originalString);
    }

    @Override
    public Integer getNullValue() throws BadArgumentASMException {
        throw new BadArgumentASMException(JArmEmuApplication.formatMessage("%exception.argument.missingLabel"));
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        if (stateSupplier.get().getAccessibleLabels().get(originalString) == null) throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.unknownLabel", originalString));

        super.verify(stateSupplier);
    }
}
