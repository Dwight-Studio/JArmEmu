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

import fr.dwightstudio.jarmemu.asm.Directive;
import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.AddressParser;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class ParsedDirective extends ParsedObject {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private final Directive directive;
    private final String args;
    private boolean generated;
    private String hash;
    private final Section section;

    public ParsedDirective(@NotNull Directive directive, @NotNull String args, Section section) {
        this.directive = directive;
        this.args = args;
        this.section = section;
    }

    @Override
    public SyntaxASMException verify(int line, Supplier<StateContainer> stateSupplier) {
        StateContainer stateContainer = stateSupplier.get();

        try {
            apply(stateContainer, 0);
            return null;
        } catch (SyntaxASMException exception) {
            return exception.with(this);
        } finally {
            AddressParser.reset(stateContainer);
        }
    }

    /**
     * Application de la directive
     * @param stateContainer Le conteneur d'état sur lequel appliquer la directive
     */
    public int apply(StateContainer stateContainer, int currentPos) {
        int symbolAddress = stateContainer.getSymbolsAddress();
        directive.apply(stateContainer, this.args, currentPos + symbolAddress, section);

        if (generated) {
            stateContainer.getPseudoData().put(hash, currentPos);
        }

        return directive.computeDataLength(stateContainer, args, currentPos, section) + currentPos;
    }

    public Directive getDirective() {
        return directive;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(String hash) {
        this.hash = hash;
        this.generated = true;
    }

    @Override
    public String toString() {
        return directive.name();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParsedDirective dir)) return false;

        if (!(dir.args.equals(this.args))) {
            if (VERBOSE) logger.info("Difference: Args");
            return false;
        }

        if (dir.directive != this.directive) {
            if (VERBOSE) logger.info("Difference: Directive");
            return false;
        }

        if (dir.generated != this.generated) {
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

    public Section getSection() {
        return section;
    }
}
