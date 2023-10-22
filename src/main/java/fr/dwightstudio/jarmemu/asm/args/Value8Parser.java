package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "imm8"
public class Value8Parser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        try {
            int rtn = ArgumentParsers.VALUE_12.parse(stateContainer, string);
            if (Integer.numberOfLeadingZeros(Math.abs(rtn)) < 25)
                throw new AssemblySyntaxException("Overflowing 8 bits value '" + string + "'");
            return rtn;
        } catch (AssemblySyntaxException exception) {
            if (exception.getMessage().startsWith("Invalid 12 bits value")) {
                throw new AssemblySyntaxException("Invalid 8 bits value '" + string + "'");
            } else throw exception;
        }
    }
}
