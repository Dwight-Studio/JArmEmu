package fr.dwightstudio.jarmemu.sim.args;

import fr.dwightstudio.jarmemu.asm.exceptions.BadArgumentsASMException;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "imm8"
public class Value8Parser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        try {
            if (string.startsWith("#")) {
                String valueString = string.substring(1).strip();

                int rtn = ArgumentParsers.VALUE_12.generalParse(stateContainer, valueString);
                if (Integer.numberOfLeadingZeros(rtn) < 24)
                    throw new SyntaxASMException("Overflowing 8bits value '" + string + "'");
                return rtn;

            } else if (string.startsWith("=")) {
                throw new IllegalArgumentException("Detecting unprocessed '=' Pseudo-Op");
            } else {
                throw new SyntaxASMException("Invalid 8bits immediate value '" + string + "'");
            }
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException("Invalid 8bits immediate value '" + string + "' (" + exception.getMessage() + ")");
        }
    }

    @Override
    public Integer none() {
        throw new BadArgumentsASMException("missing immediate (8bits)");
    }
}
