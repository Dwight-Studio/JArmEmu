package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

// Correspond à un argument supplémentaire à "arg"
public class ShiftParser implements ArgumentParser<Function<byte[],byte[]>> {
    @Override
    public Function<byte[],byte[]> parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        // TODO: Finir le ShiftParser
        try {

            if (string.length() <= 3) {
                if (string.equals("RRX")) {
                    return null;
                } else {
                    throw new AssemblySyntaxException("Invalid shift expression '" + string + "'");
                }
            }

            String type = string.substring(0, 3);
            int value = ArgumentParsers.VALUE_OR_REGISTER.parse(stateContainer, string.substring(3););

            switch (type) {
                case "LSL":

                    break;

                case "LSR":

                    break;

                case "ASR":

                    break;

                case "ASL":

                    break;

                case "ROR":

                    break;
            }

            return null; // Retourne une fonction qui effectue le shift

        } catch (IndexOutOfBoundsException exception) {
            throw new AssemblySyntaxException("Invalid shift expression '" + string + "'");
        }
    }
}
