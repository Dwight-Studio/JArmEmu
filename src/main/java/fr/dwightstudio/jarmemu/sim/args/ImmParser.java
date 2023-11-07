package fr.dwightstudio.jarmemu.sim.args;

import fr.dwightstudio.jarmemu.asm.exceptions.BadArgumentsASMException;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "imm12"
public class ImmParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        try {
            if (string.startsWith("#")) {
                String valueString = string.substring(1).strip();

                int rtn = RotatedImmParser.generalParse(stateContainer, valueString);



                if (Integer.numberOfLeadingZeros(Math.abs(rtn)) < 21 && rtn != -2048)
                    throw new SyntaxASMException("Overflowing 12bits value '" + string + "'");
                return rtn;

            } else if (string.startsWith("=")) {
                throw new IllegalArgumentException("Detecting unprocessed '=' Pseudo-Instruction");
            } else if (string.startsWith("*")) {
                    throw new SyntaxASMException("Detecting Pseudo-Instruction '" + string + "'");
            } else {
                throw new SyntaxASMException("Invalid 12bits immediate value '" + string + "'");
            }
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException("Invalid 12bits immediate value '" + string + "' (" + exception.getMessage() + ")");
        }
    }

    @Override
    public Integer none() {
        throw new BadArgumentsASMException("missing immediate (12bits)");
    }
}
