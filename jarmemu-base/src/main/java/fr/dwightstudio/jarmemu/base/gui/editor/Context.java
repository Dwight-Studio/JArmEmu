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

package fr.dwightstudio.jarmemu.base.gui.editor;

public enum Context {
    NONE,
    ERROR,
    COMMENT,

    INSTRUCTION,
    INSTRUCTION_ARGUMENT_1(0),
    INSTRUCTION_ARGUMENT_2(1),
    INSTRUCTION_ARGUMENT_3(2),
    INSTRUCTION_ARGUMENT_4(3),

    LABEL,

    SECTION,
    DIRECTIVE,
    DIRECTIVE_ARGUMENTS;

    private final int index;

    Context() {
        this.index = -1;
    }

    Context(final int index) {
        this.index = index;
    }

    public Context getNext() {
        if (index != -1) {
            for (Context context : Context.values()) {
                if (context.index == index + 1) {
                    return context;
                }
            }
        }
        return ERROR;
    }

    public int getIndex() {
        return index;
    }
}
