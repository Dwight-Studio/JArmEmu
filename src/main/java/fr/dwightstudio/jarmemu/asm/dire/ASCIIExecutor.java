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

package fr.dwightstudio.jarmemu.asm.dire;

import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.FilePos;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class ASCIIExecutor implements DirectiveExecutor {
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

        if (!args.isBlank() && !section.allowDataInitialisation()) {
            throw new SyntaxASMException("Illegal data initialization (in " + section.name() + ")");
        }

        FilePos tempPos = currentPos.clone();

        if ((args.startsWith("\"") && args.endsWith("\"")) || (args.startsWith("'") && args.endsWith("'"))) {
            String del = String.valueOf(args.charAt(0));
            String str = args.substring(1, args.length()-1);
            if (str.contains(del)) throw new SyntaxASMException("Invalid argument '" + args + "' for ASCII directive");
            for (char c : str.toCharArray()) {
                DirectiveExecutors.BYTE.apply(stateContainer, String.valueOf((int) c), tempPos, section);
                tempPos.incrementPos();
            }
        } else {
            throw new SyntaxASMException("Invalid argument '" + args + "' for ASCII directive");
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
        String str = args.substring(1, args.length() - 1);
        currentPos.incrementPos(str.length());
    }
}
