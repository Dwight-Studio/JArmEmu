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

package fr.dwightstudio.jarmemu.asm;

import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Function;

public enum Condition {

    AL((state) -> true),
    EQ((state) -> state.cpsr.getZ()),
    NE((state) -> !state.cpsr.getZ()),
    CS((state) -> state.cpsr.getC()),
    CC((state) -> !state.cpsr.getC()),
    MI((state) -> state.cpsr.getN()),
    PL((state) -> !state.cpsr.getN()),
    VS((state) -> state.cpsr.getV()),
    VC((state) -> !state.cpsr.getV()),
    HS((state) -> state.cpsr.getC()),
    LO((state) -> !state.cpsr.getC()),
    HI((state) -> state.cpsr.getC() && !state.cpsr.getZ()),
    LS((state) -> !state.cpsr.getC() || state.cpsr.getZ()),
    GE((state) -> state.cpsr.getN() == state.cpsr.getV()),
    LT((state) -> state.cpsr.getN() != state.cpsr.getV()),
    GT((state) -> !state.cpsr.getZ() && (state.cpsr.getN() == state.cpsr.getV())),
    LE((state) -> state.cpsr.getZ() || (state.cpsr.getN() != state.cpsr.getV()));

    private final Function<StateContainer, Boolean> tester;

    Condition(Function<StateContainer, Boolean> tester) {
        this.tester = tester;
    }

    public boolean eval(StateContainer stateContainer) {
        return tester.apply(stateContainer);
    }

}
