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

import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterArrayArgument extends ParsedArgument<Register[]> {

    protected final Logger logger = Logger.getLogger(getClass().getSimpleName());

    ArrayList<RegisterArgument> arguments;

    public RegisterArrayArgument(String originalString) throws SyntaxASMException {
        super(originalString);

        if (originalString == null) throw new BadArgumentASMException(JArmEmuApplication.formatMessage("%exception.argument.missingRegisterArray"));

        if (originalString.startsWith("{") && originalString.endsWith("}")) {
            arguments = new ArrayList<>();

            String arrayString = originalString.substring(1, originalString.length()-1);

            for (String regString : arrayString.split(",")) {
                if(regString.contains("-")){
                    String[] stringArray = regString.split("-");
                    if (stringArray.length != 2) throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.unexpectedArgumentRegisterArray", originalString));
                    int registerFirst = Integer.parseInt(stringArray[0].strip().substring(1));
                    int registerSecond = Integer.parseInt(stringArray[1].strip().substring(1));

                    if (registerFirst > registerSecond) {
                        throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.emptyArgumentRegisterArray", regString));
                    }

                    for (int i = registerFirst; i <= registerSecond; i++) {
                        arguments.add(new RegisterArgument("R" + i));
                    }
                } else {
                    arguments.add(new RegisterArgument(regString.strip()));
                }
            }

            if (arguments.isEmpty()) {
                throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.emptyArgumentRegisterArray", originalString));
            }

        } else {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.argument.unexpectedArgumentRegisterArray", originalString));
        }
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        for (RegisterArgument argument : arguments) {
            argument.contextualize(stateContainer);
        }
    }

    @Override
    public Register[] getValue(StateContainer stateContainer) throws ExecutionASMException {
        ArrayList<Register> rtn = new ArrayList<>();

        for (RegisterArgument argument : arguments) {
            Register reg = argument.getValue(stateContainer);
            if (!rtn.contains(reg)) {
                rtn.add(reg);
            } else {
                logger.log(Level.WARNING, "Duplicate register in array");
            }
        }

        return rtn.toArray(new Register[0]);
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        for (RegisterArgument argument : arguments) {
            argument.verify(stateSupplier);
        }

        super.verify(stateSupplier);
    }

    public ArrayList<RegisterArgument> getArguments() {
        return arguments;
    }
}
