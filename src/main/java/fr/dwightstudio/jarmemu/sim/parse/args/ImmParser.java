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

package fr.dwightstudio.jarmemu.sim.parse.args;

import fr.dwightstudio.jarmemu.sim.exceptions.BadArgumentsASMException;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "imm12"
public class ImmParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        try {
            if (string.startsWith("#")) {
                String valueString = string.substring(1).strip();

                int rtn = stateContainer.evalWithAccessibleConsts(valueString);



                if (Integer.numberOfLeadingZeros(Math.abs(rtn)) < 21 && rtn != -2048)
                    throw new SyntaxASMException("Overflowing 12bits value '" + string + "'");
                return rtn;

            } else if (string.startsWith("=")) {
                throw new IllegalArgumentException("Detecting unprocessed '=' Pseudo-Instruction");
            } else if (string.startsWith("*")) {
                    throw new SyntaxASMException("Detecting Pseudo-Instruction '" + string + "'");
            } else {
                throw new SyntaxASMException("Invalid 12bits immediate value '" + string + "'");
            }
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException("Invalid 12bits immediate value '" + string + "' (" + exception.getMessage() + ")");
        }
    }

    @Override
    public Integer none() {
        throw new BadArgumentsASMException("missing immediate (12bits)");
    }
}
