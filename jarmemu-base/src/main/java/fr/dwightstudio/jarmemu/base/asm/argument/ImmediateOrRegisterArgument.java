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
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;

import java.util.Objects;
import java.util.function.Supplier;

public class ImmediateOrRegisterArgument extends ParsedArgument<ImmediateOrRegisterArgument.RegisterOrImmediate> {

    private boolean immediate;
    private ImmediateArgument immediateArgument;
    private RegisterArgument registerArgument;

    public ImmediateOrRegisterArgument(String originalString) throws SyntaxASMException {
        super(originalString);

        if (originalString != null) {
            immediate = originalString.startsWith("#") || originalString.startsWith("=") || originalString.startsWith("*");

            if (immediate) {
                immediateArgument = new ImmediateArgument(originalString);
            } else {
                registerArgument = new RegisterArgument(originalString);
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
    public ImmediateOrRegisterArgument.RegisterOrImmediate getValue(StateContainer stateContainer) throws ExecutionASMException {
        if (originalString != null) {
            if (immediate) {
                return new RegisterOrImmediate(immediateArgument.getValue(stateContainer));
            } else {
                return new RegisterOrImmediate(registerArgument.getValue(stateContainer));
            }
        } else {
            return new RegisterOrImmediate(0);
        }
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        if (originalString != null) {
            if (immediate) {
                immediateArgument.verify(stateSupplier);
            } else {
                registerArgument.verify(stateSupplier);
            }

            super.verify(stateSupplier);
        }
    }

    public static class RegisterOrImmediate extends Number {
        private final Integer immediate;
        private final Register register;

        public RegisterOrImmediate(int immediate) {
            this.immediate = immediate;
            this.register = null;
        }

        public RegisterOrImmediate(Register register) {
            this.immediate = null;
            this.register = register;
        }

        public boolean isRegister() {
            return register != null;
        }

        private int getValue() {
            if (isRegister()) {
                return register.getData();
            } else {
                return immediate;
            }
        }

        @Override
        public int intValue() {
            return getValue();
        }

        @Override
        public long longValue() {
            return getValue();
        }

        @Override
        public float floatValue() {
            return getValue();
        }

        @Override
        public double doubleValue() {
            return getValue();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof RegisterOrImmediate registerOrImmediate) {
                if (isRegister()) {
                    return Objects.equals(register, registerOrImmediate.register);
                } else {
                    return Objects.equals(immediate, registerOrImmediate.immediate);
                }
            } else if (obj instanceof Register reg) {
                return isRegister() && reg.equals(register);
            } else if (obj instanceof Number number) {
                return intValue() == number.intValue();
            }
            return false;
        }
    }
}
