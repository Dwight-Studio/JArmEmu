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

package fr.dwightstudio.jarmemu.base.sim.prepare;

import fr.dwightstudio.jarmemu.base.asm.ParsedFile;
import fr.dwightstudio.jarmemu.base.asm.ParsedLabel;
import fr.dwightstudio.jarmemu.base.asm.ParsedObject;
import fr.dwightstudio.jarmemu.base.asm.directive.ParsedDirective;
import fr.dwightstudio.jarmemu.base.asm.Section;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.FilePos;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class DirectivePreparationTask extends PreparationTask<ParsedDirective> {

    private static final Logger logger = Logger.getLogger(DirectivePreparationTask.class.getSimpleName());

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
        logger.info("Contextualizing directives" + getDescription());
        container.getCurrentFilePos().setFileIndex(0);
        for (ParsedFile file : stream.files) {
            for (ParsedObject obj : file) {
                if (obj instanceof ParsedDirective dir) {
                    if (test(dir)) {
                        logger.info("Contextualizing " + dir);
                        dir.contextualize(container);
                    }
                }
            }
            container.getCurrentFilePos().incrementFileIndex();
        }
        logger.info("Done!");
        return stream;
    }

    /**
     * Exécute toutes les directives
     *
     * @param container le conteneur d'état sur lequel effectuer l'opération
     */
    public PreparationStream execute(StateContainer container) throws ASMException {
        logger.info("Executing directives" + getDescription());
        container.getCurrentFilePos().setFileIndex(0);
        for (ParsedFile file : stream.files) {
            for (ParsedObject obj : file) {
                if (obj instanceof ParsedDirective dir) {
                    if (test(dir)) {
                        logger.info("Executing " + dir + " (" + container.getCurrentFilePos() + " in memory)");
                        dir.execute(container);
                        dir.offsetMemory(container);
                    }
                }
            }
            container.getCurrentFilePos().incrementFileIndex();
        }
        logger.info("Done!");
        return stream;
    }

    /**
     * Exécute toutes les directives et enregistre les labels
     *
     * @param container le conteneur d'état sur lequel effectuer l'opération
     */
    public PreparationStream registerLabels(StateContainer container) throws ASMException {
        logger.info("Registering directive labels" + getDescription());
        container.getCurrentFilePos().setFileIndex(0);
        for (ParsedFile file : stream.files) {
            for (ParsedObject obj : file) {
                if (obj instanceof ParsedLabel label) {
                    if (label.getSection() == Section.TEXT) continue;
                    if (test(label)) {
                        logger.info("Registering " + label + " (" + container.getCurrentFilePos() + " in memory)");
                        label.register(container, container.getCurrentFilePos());
                    }
                }
                if (obj instanceof ParsedDirective dir) {
                    if (dir.getSection() == Section.TEXT) continue;
                    if (test(dir)) {
                        FilePos lastPos = container.getCurrentFilePos().freeze();
                        dir.offsetMemory(container);
                        logger.info("Allocated memory for " + dir + " (" + lastPos + "->" + container.getCurrentFilePos() + ")");
                    }
                }
            }
            container.getCurrentFilePos().incrementFileIndex();
        }
        logger.info("Done!");
        return stream;
    }

    @Override
    public PreparationStream verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        logger.info("Verifying directives" + getDescription());
        int fi = 0;
        for (ParsedFile file : stream.files) {
            for (ParsedObject obj : file) {
                if (obj instanceof ParsedDirective dir) {
                    if (test(dir)) {
                        logger.info("Verifying " + dir);
                        int finalFi = fi;
                        dir.verify(() -> {
                            StateContainer container = stateSupplier.get();
                            container.getCurrentFilePos().setFileIndex(finalFi);
                            return container;
                        });
                    }
                }
            }
            fi++;
        }
        logger.info("Done!");
        return stream;
    }

    @Override
    public PreparationStream perform(Consumer<ParsedDirective> consumer) {
        logger.info("Performing operation on directives" + getDescription());
        for (ParsedFile file : stream.files) {
            for (ParsedObject obj : file) {
                if (obj instanceof ParsedDirective dir) {
                    if (test(dir)) {
                        logger.info("Performing on " + dir);
                        consumer.accept(dir);
                    }
                }
            }
        }
        logger.info("Done!");
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

    protected String getDescription() {
        StringBuilder builder = new StringBuilder();

        if (section != null) builder.append(" in ").append(section.name());

        if (notSection != null) builder.append(" not in ").append(notSection.name());

        if (generated != null) builder.append(" which are ").append(generated ? "generated" : "not generated");

        if (contextBuilder != null) {
            if (generated != null) builder.append(" and ");
            else builder.append(" which are ");
            builder.append(contextBuilder ? "context builders" : "not context builders");
        }

        if (ifTrue != null) builder.append(" (filtered by condition)");

        return builder.toString();
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
