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

import fr.dwightstudio.jarmemu.asm.ParsedFile;
import fr.dwightstudio.jarmemu.asm.ParsedObject;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.instruction.ParsedInstruction;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class InstructionPreparationTask extends PreparationTask<ParsedInstruction<?, ?, ?, ?>> {

    private static final Logger logger = Logger.getLogger(InstructionPreparationTask.class.getSimpleName());

    private Boolean modifyPC;
    private Boolean workingRegister;
    private Boolean generated;
    private Predicate<ParsedInstruction<?, ?, ?, ?>> ifTrue;

    protected InstructionPreparationTask(PreparationStream stream) {
        super(stream);
        this.modifyPC = null;
        this.workingRegister = null;
        this.generated = null;
        this.ifTrue = null;
    }

    @Override
    public PreparationStream contextualize(StateContainer container) throws ASMException {
        logger.info("Contextualizing instructions" + getDescription());
        container.getCurrentFilePos().setFileIndex(0);
        for (ParsedFile file : stream.files) {
            for (ParsedObject obj : file) {
                if (obj instanceof ParsedInstruction<?, ?, ?, ?> ins) {
                    if (test(ins)) {
                        logger.info("Contextualizing " + ins);
                        ins.contextualize(container);
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
        logger.info("Verifying instructions" + getDescription());
        int fi = 0;
        for (ParsedFile file : stream.files) {
            for (ParsedObject obj : file) {
                if (obj instanceof ParsedInstruction<?, ?, ?, ?> ins) {
                    if (test(ins)) {
                        int finalFi = fi;
                        logger.info("Verifying " + ins);
                        ins.verify(() -> {
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
    public PreparationStream perform(Consumer<ParsedInstruction<?, ?, ?, ?>> consumer) {
        logger.info("Performing operation on instructions" + getDescription());
        for (ParsedFile file : stream.files) {
            for (ParsedObject obj : file) {
                if (obj instanceof ParsedInstruction<?, ?, ?, ?> ins) {
                    if (test(ins)) {
                        logger.info("Performing on " + ins);
                        consumer.accept(ins);
                    }
                }
            }
        }
        logger.info("Done!");
        return stream;
    }

    protected boolean test(ParsedInstruction<?, ?, ?, ?> ins) {
        if (modifyPC != null && ins.doModifyPC() != modifyPC) return false;

        if (workingRegister != null && ins.hasWorkingRegister() != workingRegister) return false;

        if (generated != null && ins.isGenerated() != generated) return false;

        if (ifTrue != null && !ifTrue.test(ins)) return false;

        return true;
    }

    protected String getDescription() {
        StringBuilder builder = new StringBuilder();

        if (generated != null) builder.append(" which are ").append(generated ? "generated" : "not generated");

        if (modifyPC != null) {
            if (generated != null) builder.append(" and ");
            else builder.append(" which ");
            builder.append(modifyPC ? "do modify PC" : "don't modify PC");
        }

        if (workingRegister != null) {
            if (generated != null | modifyPC != null) builder.append(" and ");
            else builder.append(" which ");
            builder.append(workingRegister ? "have working register" : "haven't working register");
        }

        if (ifTrue != null) builder.append(" (filtered by condition)");

        return builder.toString();
    }

    public InstructionPreparationTask doModifyPC(boolean b) {
        this.modifyPC = b;
        return this;
    }

    public InstructionPreparationTask hasWorkingRegister(boolean b) {
        this.workingRegister = b;
        return this;
    }

    public InstructionPreparationTask isGenerated(boolean b) {
        this.generated = b;
        return this;
    }

    public InstructionPreparationTask ifTrue(Predicate<ParsedInstruction<?, ?, ?, ?>> predicate) {
        this.ifTrue = predicate;
        return this;
    }
}
