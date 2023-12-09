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

package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.FilePos;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class ByteExecutor implements DirectiveExecutor {
    /**
     * Application de la directive
     *
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     * @param args           la chaine d'arguments
     * @param currentPos     la position actuelle dans la mémoire
     * @param section
     */
    @Override
    public void apply(StateContainer stateContainer, String args, FilePos currentPos, Section section) {

        if (args.isBlank()) {
            return;
        } else if (!section.allowDataInitialisation()) {
            throw new SyntaxASMException("Illegal data initialization (in " + section.name() + ")");
        }

        try {
            String[] arg = args.split(",");

            FilePos tempPos = currentPos.clone();

            for (String string : arg) {
                int data = stateContainer.evalWithAccessible(string.strip());
                if (Integer.numberOfLeadingZeros(data) >= 24) {
                    byte half = (byte) data;
                    stateContainer.getMemory().putByte(tempPos.getPos(), half);
                    tempPos.incrementPos(1);
                } else {
                    throw new SyntaxASMException("Overflowing Byte value '" + args + "'");
                }
            }
        } catch (NumberFormatException exception) {
            throw new SyntaxASMException("Invalid Byte value '" + args + "'");
        }
    }

    /**
     * Calcul de la taille prise en mémoire
     *
     * @param stateContainer Le conteneur d'état sur lequel calculer
     * @param args           la chaine d'arguments
     * @param currentPos     la position actuelle
     * @param section
     */
    @Override
    public void computeDataLength(StateContainer stateContainer, String args, FilePos currentPos, Section section) {
        if (args.isBlank()) currentPos.incrementPos();
        String[] arg = args.split(",");
        currentPos.incrementPos(arg.length);
    }
}
