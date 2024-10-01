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

package fr.dwightstudio.jarmemu.base.sim.entity;

import java.util.Objects;

public class RegisterOrImmediate extends Number {
    private final Integer immediate;
    private final Register register;
    private boolean negative;

    public RegisterOrImmediate(int immediate) {
        this.immediate = immediate;
        this.register = null;
        this.negative = false;
    }

    public RegisterOrImmediate(Register register, boolean negative) {
        this.immediate = null;
        this.register = register;
        this.negative = negative;
    }

    public boolean isRegister() {
        return register != null;
    }

    private int getValue() {
        if (isRegister()) {
            return negative ? (-register.getData()) : register.getData();
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
