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
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.nio.ByteBuffer;

public class FillExecutor implements DirectiveExecutor {
    /**
     * Application de la directive
     *
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     * @param args           la chaine d'arguments
     * @param currentPos     la position actuelle dans la mémoire
     * @param section
     */
    @Override
    public void apply(StateContainer stateContainer, String args, int currentPos, Section section) {
        String[] arg = args.split(",");

        switch (arg.length) {
            case 1 -> apply(stateContainer, args + ", 0, 1", currentPos, section);

            case 2 -> apply(stateContainer, args + ", 1", currentPos, section);

            case 3 -> {
                int totalNum = stateContainer.evalWithConsts(arg[0]);
                int value = stateContainer.evalWithConsts(arg[1]);
                int valueSize = stateContainer.evalWithConsts(arg[2]);

                if (valueSize <= 0) throw new SyntaxASMException("Invalid value size '" + valueSize + "' (must be positive)");

                byte[] bytes = new byte[valueSize];

                switch (valueSize) {
                    case 1 -> ByteBuffer.wrap(bytes).put((byte) (value & 0xFF));

                    case 2 -> ByteBuffer.wrap(bytes).putShort((short) (value & 0xFFFF));

                    case 3 -> ByteBuffer.wrap(bytes).put((byte) ((value >> 16) & 0xFF)).putShort((short) (value & 0xFFFF));

                    default -> {
                        ByteBuffer buffer = ByteBuffer.wrap(bytes);

                        for (int i = 0 ; i < valueSize - 4 ; i++) {
                            buffer.put((byte) 0);
                        }

                        buffer.putInt(value);
                    }
                }

                for (int i = currentPos ; i < currentPos + totalNum ; i++) {
                    stateContainer.memory.putByte(currentPos + i, bytes[i % valueSize]);
                }
            }

            default -> throw new SyntaxASMException("Invalid arguments '" + args + "' for Fill directive");
        }
    }

    /**
     * Calcul de la taille prise en mémoire
     *
     * @param stateContainer Le conteneur d'état sur lequel calculer
     * @param args           la chaine d'arguments
     * @param currentPos     la position actuelle
     * @param section
     * @return la taille des données
     */
    @Override
    public int computeDataLength(StateContainer stateContainer, String args, int currentPos, Section section) {
        String[] arg = args.split(",");
        return stateContainer.evalWithConsts(arg[0]);
    }
}
