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

package fr.dwightstudio.jarmemu.base.asm;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;

import java.util.function.Supplier;

public class ParsedSection extends ParsedObject {

    private final Section section;

    public ParsedSection(Section section) {
        this.section = section;
    }

    public Section getSection() {
        return section;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParsedSection parsedSection)) return false;

        return parsedSection.section == section;
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {

    }

    public ParsedSection withLineNumber(int lineNumber) {
        setLineNumber(lineNumber);
        return this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " at " + getFilePos();
    }
}
