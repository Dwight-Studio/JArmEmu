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
import fr.dwightstudio.jarmemu.sim.obj.FilePos;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class ParsedLabel extends ParsedObject {
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final String name;
    private ParsedInstruction instruction;
    private FilePos pos;
    private Integer erased;

    public ParsedLabel(String name) {
        this.name = name;
    }

    public ParsedLabel(String name, FilePos pos) {
        this.name = name;
        this.pos = pos.freeze();
    }

    public String getName() {
        return name;
    }

    @Override
    public SyntaxASMException verify(int line, Supplier<StateContainer> stateSupplier) {
        StateContainer container = stateSupplier.get();

        if (container.getRestrainedLabels().get(this.name.toUpperCase()) == null) {
            throw new IllegalStateException("Unable to verify label " + name + " (incorrectly registered in the StateContainer)");
        } else if (container.getRestrainedLabels().get(this.name) != this.pos.toByteValue()) {
            return new SyntaxASMException("Label '" + this.name + "' is already defined").with(line).with(this);
        }

        if (erased != null) return new SyntaxASMException("Label '" + this.name + "' is already defined").with(this);

        return null;
    }

    /**
     * Enregistre le label dans le conteneur d'état
     *
     * @param stateContainer le conteneur d'état
     */
    public void register(StateContainer stateContainer, FilePos pos) {
        this.pos = pos.freeze();
        erased = stateContainer.getAccessibleLabels().put(name.strip().toUpperCase(), this.pos.toByteValue());
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

    public ParsedInstruction getInstruction() {
        return instruction;
    }

    public void setInstruction(ParsedInstruction instruction) {
        this.instruction = instruction;
    }

    public ParsedLabel withInstruction(ParsedInstruction instruction) {
        this.instruction = instruction;
        return this;
    }
}
