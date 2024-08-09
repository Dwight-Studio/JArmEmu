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

package fr.dwightstudio.jarmemu.base.asm.modifier;

import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;

import java.util.function.Function;

public enum Condition implements ModifierParameter {

    EQ((state) -> state.getCPSR().getZ(), 0b0000),
    NE((state) -> !state.getCPSR().getZ(), 0b0001),
    CS((state) -> state.getCPSR().getC(), 0b0010), HS((state) -> state.getCPSR().getC(), 0b0010),
    CC((state) -> !state.getCPSR().getC(), 0b0011), LO((state) -> !state.getCPSR().getC(), 0b0011),
    MI((state) -> state.getCPSR().getN(), 0b0100),
    PL((state) -> !state.getCPSR().getN(), 0b0101),
    VS((state) -> state.getCPSR().getV(), 0b0110),
    VC((state) -> !state.getCPSR().getV(), 0b0111),
    HI((state) -> state.getCPSR().getC() && !state.getCPSR().getZ(), 0b1000),
    LS((state) -> !state.getCPSR().getC() || state.getCPSR().getZ(), 0b1001),
    GE((state) -> state.getCPSR().getN() == state.getCPSR().getV(), 0b1010),
    LT((state) -> state.getCPSR().getN() != state.getCPSR().getV(), 0b1011),
    GT((state) -> !state.getCPSR().getZ() && (state.getCPSR().getN() == state.getCPSR().getV()), 0b1100),
    LE((state) -> state.getCPSR().getZ() || (state.getCPSR().getN() != state.getCPSR().getV()), 0b1101),
    AL((state) -> true, 0b1110);

    private final Function<StateContainer, Boolean> conditionFunction;
    private final int code;

    Condition(Function<StateContainer, Boolean> conditionFunction, int code) {
        this.conditionFunction = conditionFunction;
        this.code = code;
    }

    public boolean eval(StateContainer stateContainer) {
        return conditionFunction.apply(stateContainer);
    }

    public int getCode() {
        return code;
    }
}
