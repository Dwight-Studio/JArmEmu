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

public record Modifier(Condition condition, boolean doUpdateFlags, DataMode dataMode, UpdateMode updateMode) {
    public Modifier() {
        this(null, false, null, null);
    }

    public Modifier(Condition condition) {
        this(condition, false, null, null);
    }

    public Modifier withCondition(Condition condition) {
        return new Modifier(condition, doUpdateFlags, dataMode, updateMode);
    }

    public Modifier withUpdateFlags(boolean doUpdateFlags) {
        return new Modifier(condition, doUpdateFlags, dataMode, updateMode);
    }

    public Modifier withDataMode(DataMode dataMode) {
        return new Modifier(condition, doUpdateFlags, dataMode, updateMode);
    }

    public Modifier withUpdateMode(UpdateMode updateMode) {
        return new Modifier(condition, doUpdateFlags, dataMode, updateMode);
    }

    public Modifier with(ModifierParameter parameter) {
        return switch (parameter) {
            case Condition condition -> withCondition(condition);
            case UpdateFlags updateFlags -> withUpdateFlags(true);
            case DataMode dataMode -> withDataMode(dataMode);
            case UpdateMode updateMode -> withUpdateMode(updateMode);
            default -> this;
        };
    }

    @Override
    public String toString() {
        return (condition == null ? "" : condition.toString()) +
                (doUpdateFlags ? "S" : "") +
                (dataMode == null ? "" : dataMode.toString()) +
                (updateMode == null ? "" : updateMode.toString());
    }
}
