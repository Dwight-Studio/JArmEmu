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

package fr.dwightstudio.jarmemu.sim.prepare;

import fr.dwightstudio.jarmemu.asm.ParsedLabel;
import fr.dwightstudio.jarmemu.asm.ParsedObject;
import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.asm.directive.ParsedDirective;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DirectivePreparationTask extends PreparationTask<ParsedDirective> {

    private Section section;
    private Section notSection;
    private Boolean generated;
    private Boolean contextBuilder;
    private Predicate<ParsedDirective> ifTrue;

    protected DirectivePreparationTask(PreparationStream stream) {
        super(stream);
        this.section = null;
        this.generated = null;
        this.contextBuilder = null;
        this.ifTrue = null;
    }

    @Override
    public PreparationStream contextualize(StateContainer container) throws ASMException {
        for (ParsedObject obj : stream.file) {
            if (obj instanceof ParsedDirective dir) {
                if (test(dir)) dir.contextualize(container);
            }
        }
        return stream;
    }

    /**
     * Exécute toutes les directives
     *
     * @param container le conteneur d'état sur lequel effectuer l'opération
     */
    public PreparationStream execute(StateContainer container) throws ASMException {
        for (ParsedObject obj : stream.file) {
            if (obj instanceof ParsedDirective dir) {
                if (test(dir))  {
                    dir.execute(container);
                    dir.offsetMemory(container);
                }
            }
        }
        return stream;
    }

    /**
     * Exécute toutes les directives et enregistre les labels
     *
     * @param container le conteneur d'état sur lequel effectuer l'opération
     */
    public PreparationStream executeAndRegisterLabels(StateContainer container) throws ASMException {
        for (ParsedObject obj : stream.file) {
            if (obj instanceof ParsedDirective dir) {
                if (test(dir))  {
                    dir.execute(container);
                    dir.offsetMemory(container);
                }
            }
        }
        return stream;
    }

    @Override
    public PreparationStream verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        for (ParsedObject obj : stream.file) {
            if (obj instanceof ParsedDirective dir) {
                if (test(dir)) dir.verify(stateSupplier);
            }
        }
        return stream;
    }

    @Override
    public PreparationStream perform(Consumer<ParsedDirective> consumer) {
        for (ParsedObject obj : stream.file) {
            if (obj instanceof ParsedDirective dir) {
                if (test(dir)) consumer.accept(dir);
            }
        }
        return stream;
    }

    protected boolean test(ParsedDirective dir) {
        if (section != null && dir.getSection() != section) return false;

        if (notSection != null && dir.getSection() == notSection) return false;

        if (generated != null && dir.isGenerated() != generated) return false;

        if (contextBuilder != null && dir.isContextBuilder() != contextBuilder) return false;

        if (ifTrue != null && !ifTrue.test(dir)) return false;

        return true;
    }

    protected boolean test(ParsedLabel label) {
        if (section != null && label.getSection() != section) return false;

        if (notSection != null && label.getSection() == notSection) return false;

        if (generated != null && label.isGenerated() != generated) return false;

        if (contextBuilder != null && contextBuilder) return false;

        if (ifTrue != null) return false;

        return true;
    }

    public DirectivePreparationTask inSection(Section section) {
        this.section = section;
        return this;
    }

    public DirectivePreparationTask notInSection(Section section) {
        this.notSection = section;
        return this;
    }

    public DirectivePreparationTask isGenerated(boolean b) {
        this.generated = b;
        return this;
    }

    public DirectivePreparationTask isContextBuilder(boolean b) {
        this.contextBuilder = b;
        return this;
    }

    public DirectivePreparationTask ifTrue(Predicate<ParsedDirective> predicate) {
        this.ifTrue = predicate;
        return this;
    }
}
