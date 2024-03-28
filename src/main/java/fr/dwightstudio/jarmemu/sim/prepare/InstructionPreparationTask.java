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

import fr.dwightstudio.jarmemu.asm.ParsedObject;
import fr.dwightstudio.jarmemu.asm.directive.ParsedDirective;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.instruction.ParsedInstruction;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class InstructionPreparationTask extends PreparationTask<ParsedInstruction<?, ?, ?, ?>> {

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
        for (ParsedObject obj : stream.file) {
            if (obj instanceof ParsedInstruction<?, ?, ?, ?> ins) {
                if (test(ins)) ins.contextualize(container);
            }
        }
        return stream;
    }

    @Override
    public PreparationStream verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        for (ParsedObject obj : stream.file) {
            if (obj instanceof ParsedInstruction<?, ?, ?, ?> ins) {
                if (test(ins)) ins.verify(stateSupplier);
            }
        }
        return stream;
    }

    @Override
    public PreparationStream perform(Consumer<ParsedInstruction<?, ?, ?, ?>> consumer) {
        for (ParsedObject obj : stream.file) {
            if (obj instanceof ParsedInstruction<?, ?, ?, ?> ins) {
                if (test(ins)) consumer.accept(ins);
            }
        }
        return stream;
    }

    protected boolean test(ParsedInstruction<?, ?, ?, ?> ins) {
        if (modifyPC != null && ins.doModifyPC() != modifyPC) return false;

        if (workingRegister != null && ins.hasWorkingRegister() != workingRegister) return false;

        if (generated != null && ins.isGenerated() != generated) return false;

        if (ifTrue != null && !ifTrue.test(ins)) return false;

        return true;
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
