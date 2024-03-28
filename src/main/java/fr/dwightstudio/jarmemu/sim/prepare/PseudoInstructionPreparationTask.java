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
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.instruction.ParsedInstruction;
import fr.dwightstudio.jarmemu.asm.instruction.PseudoInstruction;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

import java.util.ArrayList;

public class PseudoInstructionPreparationTask extends InstructionPreparationTask {
    protected PseudoInstructionPreparationTask(PreparationStream stream) {
        super(stream);
    }

    public PreparationStream allocate(StateContainer container) throws ASMException {
        for (ParsedObject obj : stream.file) {
            if (obj instanceof PseudoInstruction ins) {
                if (test((ParsedInstruction<?, ?, ?, ?>) ins)) ins.allocate(container);
            }
        }
        return stream;
    }

    public PreparationStream generate(StateContainer container) throws ASMException {
        ArrayList<ParsedObject> objects = new ArrayList<>();
        for (ParsedObject obj : stream.file) {
            if (obj instanceof PseudoInstruction ins) {
                if (test((ParsedInstruction<?, ?, ?, ?>) ins) && ins.isPseudoInstruction()) {
                    ParsedObject gen = ins.generate(container);
                    objects.add(gen);
                }
            }
        }
        stream.file.addAll(objects);
        return stream;
    }

    @Override
    protected boolean test(ParsedInstruction<?, ?, ?, ?> ins) {
        return ins instanceof PseudoInstruction && super.test(ins);
    }
}
