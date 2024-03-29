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

package fr.dwightstudio.jarmemu.asm;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.entity.FilePos;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class ParsedLabel extends ParsedObject {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private final Section section;
    private final String name;
    private FilePos memoryPos;

    public ParsedLabel(Section section, String name) {
        this.section = section;
        this.name = name.strip().toUpperCase();
    }

    /**
     * Enregistre le label dans le conteneur d'état
     *
     * @param stateContainer le conteneur d'état
     * @param pos la position dans le programme ou dans la mémoire
     */
    public void register(StateContainer stateContainer, FilePos pos) throws ASMException {
        if (section != Section.TEXT) {
            stateContainer.getAccessibleData().put(name, pos.getPos());
        } else {
            this.memoryPos = pos.freeze();
            if (stateContainer.getAccessibleLabels().put(name, this.memoryPos.toByteValue()) != null)
                throw new SyntaxASMException("Label '" + this.name + "' is already defined").with(this);
        }
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        StateContainer container = stateSupplier.get();

        if (container.getRestrainedLabels().get(this.name) == null) {
            throw new IllegalStateException("Unable to verify label " + name + " (incorrectly registered in the StateContainer)");
        } else if (container.getRestrainedLabels().get(this.name) != this.memoryPos.toByteValue()) {
            throw new SyntaxASMException("Label '" + this.name + "' is already defined").with(this);
        }
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

        if (label.memoryPos != this.memoryPos) {
            if (VERBOSE) logger.info("Difference: Pos (" + label.memoryPos + "/" + this.memoryPos + ")");
            return false;
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

    public ParsedLabel withLineNumber(int lineNumber) {
        setLineNumber(lineNumber);
        return this;
    }

    public Section getSection() {
        return section;
    }

    public String getName() {
        return this.name;
    }
}
