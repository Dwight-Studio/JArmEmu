package fr.dwightstudio.jarmemu.sim.args;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

// Correspond à un argument supplémentaire à "arg" TODO: Ajouter les flags (parce que apparent il y en a)
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
                    throw new SyntaxASMException("Invalid shift expression '" + string + "'");
                }
            }

            String type = string.substring(0, 3);
            String shift = string.substring(3).strip();
            int value = ArgumentParsers.IMM_OR_REGISTER.parse(stateContainer, shift);

            Function<Integer,Integer> func = switch (type) {
                case "LSL" -> {
                    if (value < 0 || value > 31)
                        throw new SyntaxASMException("Invalid shift value '" + shift + "', expected value between 0 and 31 included");
                    yield (i -> i << value);
                }
                case "LSR" -> {
                    if (value < 1 || value > 32)
                        throw new SyntaxASMException("Invalid shift value '" + shift + "', expected value between 1 and 32 included");
                    yield (i -> i >>> value);
                }
                case "ASR" -> {
                    if (value < 1 || value > 32)
                        throw new SyntaxASMException("Invalid shift value '" + shift + "', expected value between 1 and 32 included");
                    yield (i -> i >> value);
                }
                case "ROR" -> {
                    if (value < 1 || value > 31)
                        throw new SyntaxASMException("Invalid shift value '" + shift + "', expected value between 1 and 31 included");
                    yield (i -> Integer.rotateRight(i, value));
                }
                default -> throw new SyntaxASMException("Invalid shift expression '" + string + "'");
            };

            return new ShiftFunction(stateContainer, func);

        } catch (IndexOutOfBoundsException exception) {
            throw new SyntaxASMException("Invalid shift expression '" + string + "'");
        }
    }

    public static class ShiftFunction {

        private final StateContainer stateContainer;
        private final Function<Integer, Integer> shift;
        private boolean called;

        public ShiftFunction(StateContainer stateContainer, Function<Integer, Integer> shift) {
            this.stateContainer = stateContainer;
            this.shift = shift;
            this.called = false;
        }

        public final int apply(int i) {
            if (called) throw new IllegalStateException("ShiftFunctions are single-use functions");
            int rtn = this.shift.apply(i);
            AddressParser.updateValue.put(stateContainer, rtn);
            called = true;
            return rtn;
        }
    }

    @Override
    public ShiftParser.ShiftFunction none() {
        return new ShiftFunction(new StateContainer(), (i -> i));
    }
}
