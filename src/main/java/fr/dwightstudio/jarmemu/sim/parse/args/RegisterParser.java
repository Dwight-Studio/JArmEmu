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

package fr.dwightstudio.jarmemu.sim.parse.args;

import fr.dwightstudio.jarmemu.sim.exceptions.BadArgumentsASMException;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "reg"
public class RegisterParser implements ArgumentParser<Register> {
    @Override
    public Register parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        return switch (string) {
            case "R0" -> stateContainer.getRegister(0);
            case "R1" -> stateContainer.getRegister(1);
            case "R2" -> stateContainer.getRegister(2);
            case "R3" -> stateContainer.getRegister(3);
            case "R4" -> stateContainer.getRegister(4);
            case "R5" -> stateContainer.getRegister(5);
            case "R6" -> stateContainer.getRegister(6);
            case "R7" -> stateContainer.getRegister(7);
            case "R8" -> stateContainer.getRegister(8);
            case "R9" -> stateContainer.getRegister(9);
            case "R10" -> stateContainer.getRegister(10);
            case "FP", "R11" -> stateContainer.getRegister(11);
            case "IP", "R12" -> stateContainer.getRegister(12);
            case "SP", "R13" -> stateContainer.getRegister(13);
            case "LR", "R14" -> stateContainer.getLR();
            case "PC", "R15" -> stateContainer.getPC();
            case "CPSR" -> stateContainer.getCPSR();
            case "SPSR" -> stateContainer.getSPSR();
            default -> throw new SyntaxASMException("Unknown register '" + string + "'");
        };
    }

    @Override
    public Register none() {
        throw new BadArgumentsASMException("missing register");
    }
}
