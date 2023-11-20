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

package fr.dwightstudio.jarmemu.asm;

import fr.dwightstudio.jarmemu.asm.dire.DirectiveExecutor;
import fr.dwightstudio.jarmemu.asm.dire.DirectiveExecutors;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public enum Directive {
    // Consts
    SET(DirectiveExecutors.EQUIVALENT, true), EQU(DirectiveExecutors.EQUIVALENT, true), EQUIV(DirectiveExecutors.EQUIVALENT, true), EQV(DirectiveExecutors.EQUIVALENT, true), // Définir une constante
    GLOBAL(DirectiveExecutors.GLOBAL, true), GLOBL(DirectiveExecutors.GLOBAL, true),

    // Data
    WORD(DirectiveExecutors.WORD, false), // Donnée sur 32bits
    HALF(DirectiveExecutors.HALF, false), // Donnée sur 16bits
    BYTE(DirectiveExecutors.BYTE, false), // Donnée sur 8bits
    SPACE(DirectiveExecutors.SPACE, false), SKIP(DirectiveExecutors.SPACE, false), // Vide sur nbits
    ASCII(DirectiveExecutors.ASCII, false), // Chaîne de caractères
    ASCIZ(DirectiveExecutors.ASCIZ, false), // Chaîne de caractère finissant par '\0'
    FILL(DirectiveExecutors.FILL, false), // Remplir n fois, un nombre de taille x, de valeur y

    // Other
    ALIGN(DirectiveExecutors.ALIGN, false); // Alignement des données sur la grille des 4 bytes

    private final DirectiveExecutor executor;
    private final boolean sectionIndifferent;

    Directive(DirectiveExecutor executor, boolean sectionIndifferent) {
        this.executor = executor;
        this.sectionIndifferent = sectionIndifferent;
    }

    /**
     * Calcul de la place en mémoire nécessaire pour cette directive
     *
     * @param stateContainer Le conteneur d'état sur lequel calculer
     * @param args la chaine d'arguments
     * @param currentPos la position actuelle dans la mémoire
     */
    public int computeDataLength(StateContainer stateContainer, String args, int currentPos, Section section) throws SyntaxASMException {
        return executor.computeDataLength(stateContainer, args, currentPos, section);
    }

    /**
     * Application de la directive
     *
     * 
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     * @param args la chaine d'arguments
     * @param currentPos     la position actuelle dans la mémoire
     */
    public void apply(StateContainer stateContainer, String args, int currentPos, Section section) {
        executor.apply(stateContainer, args, currentPos, section);
    }

    public boolean isSectionIndifferent() {
        return sectionIndifferent;
    }
}
