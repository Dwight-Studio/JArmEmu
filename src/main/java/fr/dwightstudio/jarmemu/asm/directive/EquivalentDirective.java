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

package fr.dwightstudio.jarmemu.asm.directive;

import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;
import org.jetbrains.annotations.NotNull;

public class EquivalentDirective extends ParsedDirective {

    private final String symbol;
    private final String argument;

    public EquivalentDirective(Section section, @NotNull String args) throws SyntaxASMException {
        super(section, args);

        String[] arg = args.split(",");

        if (arg.length == 2) {
            symbol = arg[0].toUpperCase();

            if (!symbol.matches("[A-Za-z_0-9]+")) {
                throw new SyntaxASMException("Invalid symbol name '" + symbol + "'");
            } else {
                argument = arg[1];
            }
        } else {
            throw new SyntaxASMException("Invalid arguments '" + args + "' for Equivalent directive");
        }
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        stateContainer.getAccessibleConsts().put(symbol, stateContainer.evalWithAccessibleConsts(argument));
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
