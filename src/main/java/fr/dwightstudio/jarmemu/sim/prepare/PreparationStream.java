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
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

import java.util.logging.Logger;

public class PreparationStream {

    private static final Logger logger = Logger.getLogger(PreparationStream.class.getSimpleName());

    protected final ParsedFile file;

    public PreparationStream(ParsedFile file) {
        this.file = file;
    }

    public DirectivePreparationTask forDirectives() {
        return new DirectivePreparationTask(this);
    }

    public InstructionPreparationTask forInstructions() {
        return new InstructionPreparationTask(this);
    }

    public PseudoInstructionPreparationTask forPseudoInstructions() {
        return new PseudoInstructionPreparationTask(this);
    }

    public PreparationStream closeReadOnlyRange(StateContainer stateContainer) {
        logger.info("Setting read only range ending address to " + stateContainer.getCurrentFilePos());
        stateContainer.closeReadOnlyRange();
        return this;
    }

    public PreparationStream startPseudoInstructionRange(StateContainer stateContainer) {
        logger.info("Setting Pseudo-Instruction range starting address to " + stateContainer.getCurrentFilePos());
        stateContainer.startPseudoInstructionRange();
        return this;
    }

    public PreparationStream resetPos(StateContainer stateContainer) {
        logger.info("Resetting state data pointer");
        stateContainer.getCurrentFilePos().setPos(stateContainer.getSymbolsAddress());
        return this;
    }

    public PreparationStream goToPseudoInstructionPos(StateContainer stateContainer) {
        logger.info("Setting state data pointer to Pseudo-Instruction range starting address");
        stateContainer.getCurrentFilePos().setPos(stateContainer.getFirstAddressPIRange());
        return this;
    }
}
