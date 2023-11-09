package fr.dwightstudio.jarmemu.sim.args;

import fr.dwightstudio.jarmemu.asm.exceptions.BadArgumentsASMException;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "imm8"
public class RotatedImmParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        try {
            if (string.startsWith("#")) {
                String valueString = string.substring(1).strip();
                int value = stateContainer.evalWithConsts(valueString);
                checkOverflow(value, string);
                return value;

            } else if (string.startsWith("=")) {
                throw new IllegalArgumentException("Detecting unprocessed '=' Pseudo-Instruction");
            } else {
                throw new SyntaxASMException("Invalid 8bits rotated immediate value '" + string + "'");
            }
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException("Invalid 8bits rotated immediate value '" + string + "' (" + exception.getMessage() + ")");
        }
    }

    private void checkOverflow(int value, String string) {
        boolean valid = false;

        for (int i = 0 ; i < 32 ; i += 2) {
            int original = Integer.rotateLeft(value, i);

            if (Integer.numberOfLeadingZeros(original) >= 24) {
                valid = true;
                break;
            }
        }

        if (!valid) throw new SyntaxASMException("Overflowing 8bits rotated immediate value '" + string + "'");
    }

    @Override
    public Integer none() {
        throw new BadArgumentsASMException("missing immediate (12bits)");
    }
}
