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

import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;

import java.util.function.BiFunction;

public class ShiftFunction {

    private final boolean identity;
    private final StateContainer stateContainer;
    private final BiFunction<StateContainer, Integer, Integer> shift;
    private boolean called;

    public ShiftFunction(StateContainer stateContainer, BiFunction<StateContainer, Integer, Integer> shift) {
        this.identity = false;
        this.stateContainer = stateContainer;
        this.shift = shift;
        if (shift == null) throw new IllegalArgumentException("shift function requires a non-null state");
        this.called = false;
    }

    public ShiftFunction(StateContainer stateContainer) {
        this.identity = true;
        this.stateContainer = stateContainer;
        this.shift = null;
        this.called = false;
    }

    /**
     * Checks if the RegisterOrImmediate can be shifted
     */
    public void check(RegisterOrImmediate i) throws SyntaxASMException {
        if (!identity && !i.isRegister())
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.registerShift"));
    }

    public final int apply(RegisterOrImmediate i) {
        if (called) throw new IllegalStateException("ShiftFunctions are single-use functions");
        int rtn = i.intValue();
        if (!identity) {
            if (!i.isRegister()) throw new IllegalStateException("Immediate can't be shifted");
            rtn = this.shift.apply(stateContainer, rtn);
        }
        called = true;
        return rtn;
    }

    public final int apply(int i) {
        if (called) throw new IllegalStateException("ShiftFunctions are single-use functions");
        int rtn = i;
        if (!identity) {
            rtn = this.shift.apply(stateContainer, rtn);
        }
        called = true;
        return rtn;
    }
}
