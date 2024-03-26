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

package fr.dwightstudio.jarmemu.asm.directive;

import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.FilePos;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

public class WordDirective extends ParsedDirective {

    private final String[] arg;
    private final int[] intArray;

    public WordDirective(Section section, @NotNull String args) throws SyntaxASMException {
        super(section, args);

        if (!args.isBlank() && !section.allowDataInitialisation()) {
            throw new SyntaxASMException("Illegal data initialization (in " + section.name() + ")");
        }

        arg = args.split(",");
        intArray = new int[args.split(",").length];
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        try {
            for (int i = 0; i < arg.length; i++) {
                intArray[i] = stateContainer.evalWithAccessible(arg[i].strip());
            }
        } catch (NumberFormatException exception) {
            throw new SyntaxASMException("Invalid Word value '" + args + "'");
        }
    }

    @Override
    public void execute(StateContainer stateContainer, FilePos currentPos) throws ASMException {
        FilePos tempPos = currentPos.clone();
        for (int i : intArray) {
            stateContainer.getMemory().putWord(tempPos.getPos(), i);
            tempPos.incrementPos(4);
        }
    }

    @Override
    public void offsetMemory(StateContainer stateContainer, FilePos currentPos) throws ASMException {
        if (args.isBlank()) currentPos.incrementPos(4);
        currentPos.incrementPos(arg.length * 4);
    }

    @Override
    public boolean isContextBuilder() {
        return false;
    }
}
