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
import fr.dwightstudio.jarmemu.base.asm.ParsedObject;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.instruction.ParsedInstruction;
import fr.dwightstudio.jarmemu.base.asm.instruction.PseudoInstruction;
import fr.dwightstudio.jarmemu.base.sim.entity.FilePos;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;

import java.util.ArrayList;
import java.util.logging.Logger;

public class PseudoInstructionPreparationTask extends InstructionPreparationTask {

    private static final Logger logger = Logger.getLogger(PseudoInstructionPreparationTask.class.getSimpleName());
    protected PseudoInstructionPreparationTask(PreparationStream stream) {
        super(stream);
    }

    public PreparationStream allocate(StateContainer container) throws ASMException {
        logger.info("Allocating for Pseudo-Instructions" + getDescription());
        container.getCurrentMemoryPos().setFileIndex(0);
        for (ParsedFile file : stream.files) {
            for (ParsedObject obj : file) {
                if (obj instanceof PseudoInstruction ins) {
                    if (test((ParsedInstruction<?, ?, ?, ?>) ins) && ins.isPseudoInstruction()) {
                        FilePos lastPos = container.getCurrentMemoryPos().freeze();
                        ins.allocate(container);
                        logger.info("Allocated memory for " + ins + " (" + lastPos + "->" + container.getCurrentMemoryPos() + ")");
                    }
                }
            }
            container.getCurrentMemoryPos().incrementFileIndex();
        }
        logger.info("Done!");
        return stream;
    }

    public PreparationStream generate(StateContainer container) throws ASMException {
        logger.info("Generating Pseudo-Instructions" + getDescription());
        container.getCurrentMemoryPos().setFileIndex(0);
        for (ParsedFile file : stream.files) {
            ArrayList<ParsedObject> objects = new ArrayList<>();
            for (ParsedObject obj : file) {
                if (obj instanceof PseudoInstruction ins) {
                    if (test((ParsedInstruction<?, ?, ?, ?>) ins) && ins.isPseudoInstruction()) {
                        logger.info("Generating for " + ins);
                        ParsedObject gen = ins.generate(container);
                        if (gen != null) objects.add(gen);
                    }
                }
            }
            file.addAll(objects);
            container.getCurrentMemoryPos().incrementFileIndex();
        }
        logger.info("Done!");
        return stream;
    }

    @Override
    protected boolean test(ParsedInstruction<?, ?, ?, ?> ins) {
        return ins instanceof PseudoInstruction && super.test(ins);
    }
}
