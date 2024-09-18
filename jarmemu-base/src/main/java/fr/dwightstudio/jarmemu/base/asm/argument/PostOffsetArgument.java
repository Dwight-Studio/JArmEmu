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

package fr.dwightstudio.jarmemu.base.asm.argument;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.entity.RegisterOrImmediate;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;

import java.util.function.Supplier;

public class PostOffsetArgument extends ParsedArgument<RegisterOrImmediate> {

    private boolean immediate;
    private boolean negative;
    private ImmediateArgument immediateArgument;
    private RegisterArgument registerArgument;

    public PostOffsetArgument(String originalString) throws SyntaxASMException {
        super(originalString);

        if (originalString != null) {
            immediate = originalString.startsWith("#") || originalString.startsWith("=") || originalString.startsWith("*");

            if (immediate) {
                immediateArgument = new ImmediateArgument(originalString);
            } else {
                String valueString = originalString;

                if (valueString.startsWith("-")) {
                    negative = true;
                    valueString = valueString.substring(1);
                } else if (valueString.startsWith("+")) {
                    negative = false;
                    valueString = valueString.substring(1);
                } else {
                    negative = false;
                }

                registerArgument = new RegisterArgument(valueString);
            }
        }
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        if (originalString != null) {
            if (immediate) {
                immediateArgument.contextualize(stateContainer);
            } else {
                registerArgument.contextualize(stateContainer);
            }
        }
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        if (originalString != null) {
            if (immediate) {
                immediateArgument.verify(stateSupplier);

                int value = immediateArgument.getValue(stateSupplier.get());

                if (Integer.numberOfLeadingZeros(Math.abs(value)) < 21 && value != -2048) {
                    throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.overflowingValue", originalString));
                }
            } else {
                registerArgument.verify(stateSupplier);
            }
        }
    }

    @Override
    public RegisterOrImmediate getValue(StateContainer stateContainer) throws ExecutionASMException {
        if (originalString != null) {
            if (immediate) {
                return new RegisterOrImmediate(immediateArgument.getValue(stateContainer));
            } else {
                return new RegisterOrImmediate(registerArgument.getValue(stateContainer), negative);
            }
        } else {
            return new RegisterOrImmediate(0);
        }
    }

    public RegisterArgument getRegisterArgument() {
        return registerArgument;
    }

    public boolean isNegative() {
        return negative;
    }
}
