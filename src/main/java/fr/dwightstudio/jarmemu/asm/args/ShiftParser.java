package fr.dwightstudio.jarmemu.asm.args;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

// Correspond à un argument supplémentaire à "arg"
public class ShiftParser implements ArgumentParser<ShiftParser.ShiftFunction> {
    @Override
    public ShiftParser.ShiftFunction parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        try {
            if (string.length() <= 3) {
                if (string.equals("RRX")) {
                    Function<Integer, Integer> func = (i -> {
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
                    return new ShiftFunction(stateContainer, func);
                } else {
                    throw new AssemblySyntaxException("Invalid shift expression '" + string + "'");
                }
            }

            String type = string.substring(0, 3);
            String shift = string.substring(3);
            int value = ArgumentParsers.VALUE_OR_REGISTER.parse(stateContainer, shift);

            Function<Integer,Integer> func = switch (type) {
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

            return new ShiftFunction(stateContainer, func);

        } catch (IndexOutOfBoundsException exception) {
            throw new AssemblySyntaxException("Invalid shift expression '" + string + "'");
        }
    }

    public static class ShiftFunction {

        private final StateContainer stateContainer;
        private final Function<Integer, Integer> shift;

        public ShiftFunction(StateContainer stateContainer, Function<Integer, Integer> shift) {
            this.stateContainer = stateContainer;
            this.shift = shift;
        }

        public final int apply(int i) {
            int rtn = this.shift.apply(i);
            AddressParser.updateValue.put(stateContainer, rtn);
            return rtn;
        }
    }

    @Override
    public ShiftParser.ShiftFunction none() {
        return new ShiftFunction(new StateContainer(), (i -> i));
    }
}
