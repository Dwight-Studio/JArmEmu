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

package fr.dwightstudio.jarmemu.base.asm.argument;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;

public class RotatedImmediateArgument extends ParsedArgument<Integer> {

    private int value;
    private int originalValue;
    private int rotationValue;

    public RotatedImmediateArgument(String originalString) throws BadArgumentASMException {
        super(originalString);

        if (originalString == null) throw new BadArgumentASMException(JArmEmuApplication.formatMessage("%exception.argument.missingRotatedValue"));
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        try {
            if (originalString.startsWith("#")) {
                String valueString = originalString.substring(1).strip();

                value = (int) stateContainer.evalWithAccessible(valueString);
                checkOverflow(value, originalString);

            } else if (originalString.startsWith("=")) {
                throw new IllegalStateException(JArmEmuApplication.formatMessage("%exception.argument.unprocessedPseudo"));
            } else {
                throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.invalidRotatedValue", originalString));
            }
        } catch (IllegalArgumentException exception) {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.invalidRotatedValue", originalString) + " (" + exception.getMessage() + ")");
        }
    }

    @Override
    public Integer getValue(StateContainer stateContainer) throws ExecutionASMException {
        return value;
    }

    private void checkOverflow(int value, String string) throws SyntaxASMException {
        boolean valid = false;

        for (int i = 0 ; i < 32 ; i += 2) {
            originalValue = Integer.rotateLeft(value, i);

            if (Integer.numberOfLeadingZeros(originalValue) >= 24) {
                valid = true;
                rotationValue = i;
                break;
            }
        }

        if (!valid) throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.overflowingRotatedValue", string));
    }

    public int getOriginalValue() {
        return originalValue;
    }

    public int getRotationValue() {
        return rotationValue;
    }
}
