package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class RotatedImmediateArgument extends ParsedArgument<Integer> {

    private int value;

    public RotatedImmediateArgument(String originalString) {
        super(originalString);
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        try {
            if (originalString.startsWith("#")) {
                String valueString = originalString.substring(1).strip();

                value = stateContainer.evalWithAccessibleConsts(valueString);
                checkOverflow(value, originalString);

            } else if (originalString.startsWith("=")) {
                throw new RuntimeException(JArmEmuApplication.formatMessage("%exception.argument.unprocessedPseudo"));
            } else {
                throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.invalidRotatedValue", originalString));
            }
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.invalidRotatedValue", originalString) + " (" + exception.getMessage() + ")");
        }
    }

    @Override
    public Integer getValue(StateContainer stateContainer) throws ExecutionASMException {
        return value;
    }

    @Override
    public Integer getNullValue() throws BadArgumentASMException {
        throw new BadArgumentASMException(JArmEmuApplication.formatMessage("%exception.argument.missingRotatedValue"));
    }

    private void checkOverflow(int value, String string) throws SyntaxASMException {
        boolean valid = false;

        for (int i = 0 ; i < 32 ; i += 2) {
            int original = Integer.rotateLeft(value, i);

            if (Integer.numberOfLeadingZeros(original) >= 24) {
                valid = true;
                break;
            }
        }

        if (!valid) throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.overflowingRotatedValue", string));
    }
}
