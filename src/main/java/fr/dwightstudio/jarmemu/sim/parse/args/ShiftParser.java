/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2023 Dwight Studio
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.dwightstudio.jarmemu.sim.parse.args;

import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
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
                        if (stateContainer.getCPSR().getC()) {
                            i |= (1 << 31); // set a bit to 1
                        } else {
                            i &= ~(1 << 31); // set a bit to 0
                        }

                        stateContainer.getCPSR().setC(c);

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
