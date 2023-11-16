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

// Correspond à "imm8"
public class RotatedImmParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        try {
            if (string.startsWith("#")) {
                String valueString = string.substring(1).strip();
                int value = stateContainer.evalWithConsts(valueString);
                checkOverflow(value, string);
                return value;

            } else if (string.startsWith("=")) {
                throw new IllegalArgumentException("Detecting unprocessed '=' Pseudo-Instruction");
            } else {
                throw new SyntaxASMException("Invalid 8bits rotated immediate value '" + string + "'");
            }
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException("Invalid 8bits rotated immediate value '" + string + "' (" + exception.getMessage() + ")");
        }
    }

    private void checkOverflow(int value, String string) {
        boolean valid = false;

        for (int i = 0 ; i < 32 ; i += 2) {
            int original = Integer.rotateLeft(value, i);

            if (Integer.numberOfLeadingZeros(original) >= 24) {
                valid = true;
                break;
            }
        }

        if (!valid) throw new SyntaxASMException("Overflowing 8bits rotated immediate value '" + string + "'");
    }

    @Override
    public Integer none() {
        throw new BadArgumentsASMException("missing immediate (12bits)");
    }
}
