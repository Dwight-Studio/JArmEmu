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
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.base.util.RegisterUtils;

import java.util.function.Supplier;

public class RegisterArgument extends ParsedArgument<Register> implements OptionalRegister {
    
    private final RegisterUtils register;

    public RegisterArgument(String originalString) throws SyntaxASMException {
        super(originalString);

        if (originalString == null) throw new BadArgumentASMException(JArmEmuApplication.formatMessage("%exception.argument.missingRegister"));

        try {
            register = RegisterUtils.valueOf(originalString.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.unknownRegister", originalString));
        }

        if (register.isSpecial()) {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.invalidRegister", originalString));
        }
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {

    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        super.verify(stateSupplier);
    }

    @Override
    public Register getValue(StateContainer stateContainer) {
        return stateContainer.getRegister(register.getN());
    }

    public int getRegisterNumber() {
        return register.getN();
    }
}
