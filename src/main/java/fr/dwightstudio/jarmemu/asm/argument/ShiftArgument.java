/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2024 Dwight Studio
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

package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ShiftArgument extends ParsedArgument<ShiftArgument.ShiftFunction> {

    private BiFunction<StateContainer, Integer, Integer> func;
    private String type;
    private String shift;
    private boolean rrx;
    private ImmediateOrRegisterArgument argument;

    public ShiftArgument(String originalString) throws SyntaxASMException {
        super(originalString);

        if (originalString != null) {
            originalString = originalString.toUpperCase();
            try {
                type = originalString.substring(0, 3);
                shift = originalString.substring(3).strip();

                if (originalString.length() == 3) {
                    rrx = true;
                    if (originalString.equals("RRX")) {
                        func = (stateContainer, i) -> {
                            i = Integer.rotateRight(i, 1);
                            boolean c = ((i >> 31) & 1) == 1;

                            if (stateContainer.getCPSR().getC()) {
                                i |= (1 << 31); // set a bit to 1
                            } else {
                                i &= ~(1 << 31); // set a bit to 0
                            }

                            stateContainer.getCPSR().setC(c);
                            return i;
                        };
                    } else {
                        throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.invalidShift", originalString));
                    }
                } else {
                    rrx = false;
                    argument = new ImmediateOrRegisterArgument(shift);
                }

            } catch (IndexOutOfBoundsException | SyntaxASMException exception) {
                throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.invalidShift", originalString));
            }
        }
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        if (originalString != null) {
            try {
                if (!rrx) {
                    argument.contextualize(stateContainer);
                    int value = argument.getValue(stateContainer);

                    func = switch (type) {
                        case "LSL" -> {
                            if (value < 0 || value > 31)
                                throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.shift0to31", shift));
                            yield ((container, i) -> i << value);
                        }
                        case "LSR" -> {
                            if (value < 1 || value > 32)
                                throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.shift1to32", shift));
                            yield ((container, i) -> i >>> value);
                        }
                        case "ASR" -> {
                            if (value < 1 || value > 32)
                                throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.shift1to32", shift));
                            yield ((container, i) -> i >> value);
                        }
                        case "ROR" -> {
                            if (value < 1 || value > 31)
                                throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.shift1to32", shift));
                            yield ((container, i) -> Integer.rotateRight(i, value));
                        }
                        default -> throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.invalidShift", originalString));
                    };
                }
            } catch (IndexOutOfBoundsException exception) {
                throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.invalidShift", originalString));
            }
        }
    }

    @Override
    public ShiftFunction getValue(StateContainer stateContainer) throws ExecutionASMException {
        if (originalString != null) {
            return new ShiftFunction(stateContainer, func);
        } else {
            return new ShiftFunction(null, (StateContainer, i) -> i);
        }
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        if (originalString != null) {
            if (!rrx) {
                argument.verify(stateSupplier);
            }

            super.verify(stateSupplier);
        }
    }

    public static class ShiftFunction {

        private final StateContainer stateContainer;
        private final BiFunction<StateContainer, Integer, Integer> shift;
        private boolean called;

        public ShiftFunction(StateContainer stateContainer, BiFunction<StateContainer, Integer, Integer> shift) {
            this.stateContainer = stateContainer;
            this.shift = shift;
            this.called = false;
        }

        public final int apply(int i) {
            if (called) throw new IllegalStateException("ShiftFunctions are single-use functions");
            int rtn = this.shift.apply(stateContainer, i);
            if (stateContainer != null) stateContainer.setAddressRegisterUpdateValue(rtn);
            called = true;
            return rtn;
        }
    }
}
