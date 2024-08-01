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

import fr.dwightstudio.jarmemu.base.asm.Section;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import org.jetbrains.annotations.NotNull;

public class GlobalDirective extends ParsedDirective {
    public GlobalDirective(Section section, @NotNull String args) {
        super(section, args);
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        String name = args.toUpperCase().strip();
        if (stateContainer.getRestrainedData().containsKey(name) || stateContainer.getRestrainedLabels().containsKey(name)) {
            stateContainer.addGlobal(name, stateContainer.getCurrentMemoryPos().getFileIndex());
        } else {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.directive.invalidArgument", args, "Global"));
        }
    }

    @Override
    public void execute(StateContainer stateContainer) throws ASMException {

    }

    @Override
    public void offsetMemory(StateContainer stateContainer) throws ASMException {

    }

    @Override
    public boolean isContextBuilder() {
        return true;
    }
}
