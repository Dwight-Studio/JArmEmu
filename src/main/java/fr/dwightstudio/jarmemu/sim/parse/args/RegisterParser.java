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
            case "R0" -> stateContainer.registers[0];
            case "R1" -> stateContainer.registers[1];
            case "R2" -> stateContainer.registers[2];
            case "R3" -> stateContainer.registers[3];
            case "R4" -> stateContainer.registers[4];
            case "R5" -> stateContainer.registers[5];
            case "R6" -> stateContainer.registers[6];
            case "R7" -> stateContainer.registers[7];
            case "R8" -> stateContainer.registers[8];
            case "R9" -> stateContainer.registers[9];
            case "R10" -> stateContainer.registers[10];
            case "FP", "R11" -> stateContainer.registers[11];
            case "IP", "R12" -> stateContainer.registers[12];
            case "SP", "R13" -> stateContainer.registers[13];
            case "LR", "R14" -> stateContainer.registers[14];
            case "PC", "R15" -> stateContainer.registers[15];
            case "CPSR" -> stateContainer.cpsr;
            case "SPSR" -> stateContainer.spsr;
            default -> throw new SyntaxASMException("Unknown register '" + string + "'");
        };
    }

    @Override
    public Register none() {
        throw new BadArgumentsASMException("missing register");
    }
}
