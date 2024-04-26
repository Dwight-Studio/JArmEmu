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

package fr.dwightstudio.jarmemu.base.asm.directive;

import fr.dwightstudio.jarmemu.base.asm.Contextualized;
import fr.dwightstudio.jarmemu.base.asm.ParsedObject;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.instruction.ParsedInstruction;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;

import java.util.function.Supplier;
import java.util.logging.Logger;

public abstract class ParsedDirective extends ParsedObject implements Contextualized {

    private static final Logger logger = Logger.getLogger(ParsedInstruction.class.getSimpleName());

    protected final Section section;
    protected final String args;
    protected String hash;

    public ParsedDirective(Section section, String args) {
        this.args = args;
        this.section = section;
    }

    /**
     * Contextualise la directive dans le conteneur d'état initial, après définition des constantes.
     *
     * @param stateContainer le conteneur d'état initial
     */
    public abstract void contextualize(StateContainer stateContainer) throws ASMException;

    /**
     * @param stateContainer le conteneur d'état sur lequel appliquer la directive
     */
    public abstract void execute(StateContainer stateContainer) throws ASMException;

    /**
     * Alloue la place nécessaire dans la mémoire, en fonction des données analysées.
     *
     * @param stateContainer le conteneur d'état sur lequel appliquer la directive
     */
    public abstract void offsetMemory(StateContainer stateContainer) throws ASMException;

    /**
     * @return vrai si la directive est responsable de la construction du contexte.
     */
    public abstract boolean isContextBuilder();

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        try {
            execute(stateSupplier.get());
        } catch (ASMException exception) {
            exception.with(this);
        }
    }

    public Section getSection() {
        return section;
    }

    public ParsedDirective withLineNumber(int lineNumber) {
        setLineNumber(lineNumber);
        return this;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " at " + getFilePos();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParsedDirective dir)) return false;

        if (!(dir.args.equals(this.args))) {
            if (VERBOSE) logger.info("Difference: Args");
            return false;
        }

        if (dir.isGenerated() != this.isGenerated()) {
            if (VERBOSE) logger.info("Difference: Generated flag");
            return false;
        }

        if (dir.hash == null) {
            if (this.hash != null) {
                if (VERBOSE) logger.info("Difference: Hash (Null)");
                return false;
            }
        } else {
            if (!(dir.hash.equalsIgnoreCase(this.hash))) {
                if (VERBOSE) logger.info("Difference: Hash");
                return false;
            }
        }

        if (dir.section == null) {
            if (this.section != null) {
                if (VERBOSE) logger.info("Difference: Section (Null)");
                return false;
            }
        } else {
            if (!(dir.section.equals(this.section))) {
                if (VERBOSE) logger.info("Difference: Section");
                return false;
            }
        }

        return true;
    }
}
