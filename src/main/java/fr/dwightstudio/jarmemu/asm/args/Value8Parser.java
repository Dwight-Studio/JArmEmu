package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.exceptions.BadArgumentsASMException;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "imm8" TODO: Ajouter le Barrel Shifting
public class Value8Parser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        try {
            int rtn = ArgumentParsers.VALUE_12.parse(stateContainer, string);
            if (Integer.numberOfLeadingZeros(Math.abs(rtn)) < 25)
                throw new SyntaxASMException("Overflowing 8 bits value '" + string + "'");
            return rtn;
        } catch (SyntaxASMException exception) {
            if (exception.getMessage().startsWith("Invalid 12 bits value")) {
                throw new SyntaxASMException("Invalid 8 bits value '" + string + "'");
            } else throw exception;
        }
    }

    @Override
    public Integer none() {
        throw new BadArgumentsASMException("missing immediate (8 bits)");
    }
}
