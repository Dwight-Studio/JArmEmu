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

package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.FilePos;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ParsedDirectivePack extends ParsedObject {

    ArrayList<ParsedObject> content;


    public ParsedDirectivePack() {
        content = new ArrayList<>();
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
        for (ParsedObject directive : content) {
            SyntaxASMException error = directive.verify(line, stateSupplier);
            if (error != null) return error;
        }

        return null;
    }

    public boolean add(ParsedObject directive) {
        if (directive instanceof ParsedDirective || directive instanceof ParsedDirectiveLabel || directive instanceof ParsedSection) {
            return content.add(directive);
        } else {
            throw new IllegalArgumentException("ParsedDirectivePack can only accept ParsedDirective or ParsedDirectiveLabel");
        }
    }

    /**
     * Vérifie le nombre d'entrées, et dés-encapsule le ParsedObject si nécessaire
     *
     * @return lui-même s'il y a plusieurs entrées, la première entrée si elle est seule ou null s'il est vide
     */
    public ParsedObject close() {
        if (content.isEmpty()) {
            return null;
        } else if (content.size() == 1) {
            return content.getFirst();
        } else {
            return this;
        }
    }

    /**
     * Application des directives
     *
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     * @param pos la position de la directive
     */
    public void apply(StateContainer stateContainer, FilePos pos) {
        for (ParsedObject directive : content) {
            if (directive instanceof ParsedDirective dir) {
                dir.apply(stateContainer, pos);
            } else if (directive instanceof ParsedDirectiveLabel label) {
                label.register(stateContainer, pos.getPos());
            }
        }
    }

    /**
     * Application des directives indifférentes à la section
     *
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     * @param pos la position de la directive
     */
    public void applySectionIndifferent(StateContainer stateContainer, FilePos pos) {
        for (ParsedObject directive : content) {
            if (directive instanceof ParsedDirective dir && dir.getDirective().isSectionIndifferent()) {
                dir.apply(stateContainer, pos);
            }
        }
    }

    /**
     * Application des directives sensibles à la section
     *
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     * @param pos la position de la directive
     */
    public void applySectionSensitive(StateContainer stateContainer, FilePos pos, Section section) {
        for (ParsedObject directive : content) {
            if (directive instanceof ParsedDirective dir && !dir.getDirective().isSectionIndifferent() && dir.getSection() == section) {
                dir.apply(stateContainer, pos);
            } else if (directive instanceof ParsedDirectiveLabel label && label.getSection() == section) {
                label.register(stateContainer, pos.getPos());
            }
        }
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }

    /**
     * @return vrai si le pack contient une directive générée, faux sinon
     */
    public boolean containsGenerated() {
        boolean flag = false;

        for (ParsedObject object : content) {
            if (object instanceof ParsedDirective directive) {
                flag = flag || directive.isGenerated();
            }
        }

        return flag;
    }

    @Override
    public String toString() {
        return "Directives";
    }

    public ParsedObject[] getContent() {
        return this.content.toArray(new ParsedObject[0]);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParsedDirectivePack pack)) return false;
        return pack.content.equals(content);
    }
}
