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

import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class ParsedLabel extends ParsedObject {
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final String name;
    private int pos;

    public ParsedLabel(String name) {
        this.name = name;
    }

    public ParsedLabel(String name, int pos) {
        this.name = name;
        this.pos = pos;
    }

    public String getName() {
        return name;
    }

    @Override
    public SyntaxASMException verify(int line, Supplier<StateContainer> stateSupplier) {
        StateContainer container = stateSupplier.get();

        if (container.labels.get(this.name.toUpperCase()) == null) {
            throw new IllegalStateException("Unable to verify label (incorrectly registered in the StateContainer)");
        } else if (container.labels.get(this.name) != this.pos) {
            return new SyntaxASMException("Label '" + this.name + "' is already defined", line, this);
        }

        if (container.data.containsKey(this.name.toUpperCase())) {
            return new SyntaxASMException("Symbol '" + this.name + "' is already defined", line, this);
        }

        return null;
    }

    /**
     * Enregistre le label dans le conteneur d'état
     *
     * @param stateContainer le conteneur d'état
     */
    public void register(StateContainer stateContainer, int pos) {
        stateContainer.labels.put(name.strip().toUpperCase(), pos);
        this.pos = pos;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParsedLabel label)) return false;

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

        if (label.pos != this.pos) {
            if (VERBOSE) logger.info("Difference: Pos (" + label.pos + "/" + this.pos + ")");
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Label";
    }
}
