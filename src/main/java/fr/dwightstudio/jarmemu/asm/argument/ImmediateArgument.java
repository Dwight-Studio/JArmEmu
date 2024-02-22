package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Supplier;

public class ImmediateArgument extends ParsedArgument<Integer> {

    private int value;

    public ImmediateArgument(String originalString) {
        super(originalString);
    }

    @Override
    protected void parse(String originalString) throws SyntaxASMException {

    }

    @Override
    public Integer getValue(StateContainer stateContainer) throws ExecutionASMException {
        return value;
    }

    @Override
    public Integer getNullValue() throws BadArgumentASMException {
        throw new BadArgumentASMException(JArmEmuApplication.formatMessage("%exception.argument.missingValue"));
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier, int currentLine) throws ASMException {
        StateContainer stateContainer = stateSupplier.get();

        try {
            if (originalString.startsWith("#")) {
                String valueString = originalString.substring(1).strip();

                value = stateContainer.evalWithAccessibleConsts(valueString);

                if (Integer.numberOfLeadingZeros(Math.abs(value)) < 21 && value != -2048)
                    throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.overflowingValue", originalString));

            } else if (originalString.startsWith("=") || originalString.startsWith("*")) {
                throw new RuntimeException(JArmEmuApplication.formatMessage("%exception.argument.illegalPseudo"));
            } else {
                throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.invalidValue", originalString));
            }
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.invalidValue", originalString) + " (" + exception.getMessage() + ")");
        }
    }
}
