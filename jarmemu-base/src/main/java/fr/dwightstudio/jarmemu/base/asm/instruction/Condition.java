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

package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;

import java.util.function.Function;

public enum Condition {

    AL((state) -> true),
    EQ((state) -> state.getCPSR().getZ()),
    NE((state) -> !state.getCPSR().getZ()),
    CS((state) -> state.getCPSR().getC()),
    CC((state) -> !state.getCPSR().getC()),
    MI((state) -> state.getCPSR().getN()),
    PL((state) -> !state.getCPSR().getN()),
    VS((state) -> state.getCPSR().getV()),
    VC((state) -> !state.getCPSR().getV()),
    HS((state) -> state.getCPSR().getC()),
    LO((state) -> !state.getCPSR().getC()),
    HI((state) -> state.getCPSR().getC() && !state.getCPSR().getZ()),
    LS((state) -> !state.getCPSR().getC() || state.getCPSR().getZ()),
    GE((state) -> state.getCPSR().getN() == state.getCPSR().getV()),
    LT((state) -> state.getCPSR().getN() != state.getCPSR().getV()),
    GT((state) -> !state.getCPSR().getZ() && (state.getCPSR().getN() == state.getCPSR().getV())),
    LE((state) -> state.getCPSR().getZ() || (state.getCPSR().getN() != state.getCPSR().getV()));

    private final Function<StateContainer, Boolean> tester;

    Condition(Function<StateContainer, Boolean> tester) {
        this.tester = tester;
    }

    public boolean eval(StateContainer stateContainer) {
        return tester.apply(stateContainer);
    }

}
