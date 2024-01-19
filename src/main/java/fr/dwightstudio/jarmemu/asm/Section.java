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

package fr.dwightstudio.jarmemu.asm;

public enum Section {
    NONE(false, false),
    BSS(true, false), // Uninitialized read-write data.
    COMMENT(false, false), // Version control information.
    DATA(true, true), // Initialized read-write data.
    RODATA(true, true), // Read-only data.
    TEXT(false, false), // Executable instructions.
    NOTE(false, false), // Special information from vendors or system builders.
    END(false, false); // End of source file.

    private final boolean parseDirective;
    private final boolean dataInitialisation;

    Section(boolean parseDirective, boolean dataInitialisation) {
        this.parseDirective = parseDirective;
        this.dataInitialisation = dataInitialisation;
    }

    public boolean shouldParseDirective() {
        return parseDirective;
    }

    public boolean allowDataInitialisation() {
        return dataInitialisation;
    }
}
