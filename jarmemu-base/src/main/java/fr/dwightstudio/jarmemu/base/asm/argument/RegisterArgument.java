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

package fr.dwightstudio.jarmemu.base.asm.argument;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;

import java.util.function.Function;

public class RegisterArgument extends ParsedArgument<Register> {
    
    private Function<StateContainer, Register> registerReference;

    public RegisterArgument(String originalString) throws SyntaxASMException {
        super(originalString);

        if (originalString == null) throw new BadArgumentASMException(JArmEmuApplication.formatMessage("%exception.argument.missingRegister"));

        registerReference = switch (originalString.toUpperCase()) {
            case "R0" -> stateContainer -> stateContainer.getRegister(0);
            case "R1" -> stateContainer -> stateContainer.getRegister(1);
            case "R2" -> stateContainer -> stateContainer.getRegister(2);
            case "R3" -> stateContainer -> stateContainer.getRegister(3);
            case "R4" -> stateContainer -> stateContainer.getRegister(4);
            case "R5" -> stateContainer -> stateContainer.getRegister(5);
            case "R6" -> stateContainer -> stateContainer.getRegister(6);
            case "R7" -> stateContainer -> stateContainer.getRegister(7);
            case "R8" -> stateContainer -> stateContainer.getRegister(8);
            case "R9" -> stateContainer -> stateContainer.getRegister(9);
            case "R10" -> stateContainer -> stateContainer.getRegister(10);
            case "FP", "R11" -> stateContainer -> stateContainer.getRegister(11);
            case "IP", "R12" -> stateContainer -> stateContainer.getRegister(12);
            case "SP", "R13" -> stateContainer -> stateContainer.getRegister(13);
            case "LR", "R14" -> StateContainer::getLR;
            case "PC", "R15" -> StateContainer::getPC;
            case "CPSR" -> StateContainer::getCPSR;
            case "SPSR" -> StateContainer::getSPSR;
            default -> throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.unknownRegister", originalString));
        };
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {

    }

    @Override
    public Register getValue(StateContainer stateContainer) throws ExecutionASMException {
        return registerReference.apply(stateContainer);
    }
}
