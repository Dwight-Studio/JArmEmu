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

package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class ParsedDirectiveLabel extends ParsedObject {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final String name;
    private final Section section;

    public ParsedDirectiveLabel(String name, Section section) {
        this.name = name.toUpperCase();
        this.section = section;
    }

    /**
     * Vérifie la syntaxe de l'objet et renvoie les erreurs.
     *
     * @param line          le numéro de la ligne
     * @param stateSupplier un fournisseur de conteneur d'état
     * @return les erreurs détectées
     */
    @Override
    public SyntaxASMException verify(int line, Supplier<StateContainer> stateSupplier) {
        StateContainer container = stateSupplier.get();

        if (container.getAccessibleData().get(this.name) == null) {
            throw new IllegalStateException("Unable to verify directive label (incorrectly registered in the StateContainer)");
        }

        return null;
    }

    /**
     * Enregistre le label dans le conteneur d'état
     *
     * @param stateContainer le conteur d'état
     * @param currentPos la position actuelle dans la mémoire
     */
    public void register(StateContainer stateContainer, int currentPos) {
        stateContainer.getAccessibleData().put(name.strip().toUpperCase(), currentPos);
    }

    @Override
    public String toString() {
        return "DirectiveLabel";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParsedDirectiveLabel label)) return false;

        if (label.name == null) {
            if (this.name != null) {
                if (VERBOSE) logger.info("Difference: Name (Null)");
                return false;
            }
        } else {
            if (!(label.name.equalsIgnoreCase(this.name))) {
                if (VERBOSE) logger.info("Difference: Name");
                return false;
            }
        }

        if (label.section == null) {
            if (this.section != null) {
                if (VERBOSE) logger.info("Difference: Section (Null)");
                return false;
            }
        } else {
            if (!(label.section.equals(this.section))) {
                if (VERBOSE) logger.info("Difference: Section");
                return false;
            }
        }

        return true;
    }

    public Section getSection() {
        return section;
    }
}
