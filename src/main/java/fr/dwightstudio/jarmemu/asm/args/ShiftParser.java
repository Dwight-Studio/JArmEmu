package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.sim.Register;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

// Correspond à un argument supplémentaire à "arg"
public class ShiftParser implements ArgumentParser<Function<Integer,Integer>> {
    @Override
    public Function<Integer,Integer> parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        try {
            if (string.length() <= 3) {
                if (string.equals("RRX")) {
                    return (i -> {
                        i = Integer.rotateRight(i, 1);
                        boolean c = ((i >> 31) & 1) == 1;
                        if (stateContainer.cpsr.getC()) {
                            i |= (1 << 31); // set a bit to 1
                        } else {
                            i &= ~(1 << 31); // set a bit to 0
                        }

                        stateContainer.cpsr.setC(c);

                        return i;
                    });
                } else {
                    throw new AssemblySyntaxException("Invalid shift expression '" + string + "'");
                }
            }

            String type = string.substring(0, 3);
            String shift = string.substring(3);
            int value = ArgumentParsers.VALUE_OR_REGISTER.parse(stateContainer, shift);

            return switch (type) {
                case "LSL" -> {
                    if (value < 0 || value > 31)
                        throw new AssemblySyntaxException("Invalid shift value '" + shift + "', expected value between 0 and 31 included");
                    yield (i -> i << value);
                }
                case "LSR" -> {
                    if (value < 1 || value > 32)
                        throw new AssemblySyntaxException("Invalid shift value '" + shift + "', expected value between 1 and 32 included");
                    yield (i -> i >>> value);
                }
                case "ASR" -> {
                    if (value < 1 || value > 32)
                        throw new AssemblySyntaxException("Invalid shift value '" + shift + "', expected value between 1 and 32 included");
                    yield (i -> i >> value);
                }
                case "ROR" -> {
                    if (value < 1 || value > 31)
                        throw new AssemblySyntaxException("Invalid shift value '" + shift + "', expected value between 1 and 31 included");
                    yield (i -> Integer.rotateRight(i, value));
                }
                default -> throw new AssemblySyntaxException("Invalid shift expression '" + string + "'");
            };

        } catch (IndexOutOfBoundsException exception) {
            throw new AssemblySyntaxException("Invalid shift expression '" + string + "'");
        }
    }

    @Override
    public Function<Integer, Integer> none() {
        return (i -> i);
    }
}
