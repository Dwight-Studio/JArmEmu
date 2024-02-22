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

package fr.dwightstudio.jarmemu.sim.parse.args;

import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

// Correspond à "mreg"
public class RegisterArrayParser implements ArgumentParser<Register[]> {

    protected final Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public Register[] parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        if (string.startsWith("{") && string.endsWith("}")) {
            String arrayString = string.substring(1, string.length()-1);
            ArrayList<Register> rtn = new ArrayList<>();

            for (String regString : arrayString.split(",")) {
                if(regString.contains("-")){
                    String[] stringArray = regString.split("-");
                    if (stringArray.length!=2) throw new SyntaxASMException("Unexpected value '" + string + "' (expected a Register Array)");
                    int registerFirst = Integer.parseInt(stringArray[0].strip().substring(1));
                    int registerSecond = Integer.parseInt(stringArray[1].strip().substring(1));
                    for (int i = registerFirst; i <= registerSecond; i++) {
                        Register reg = ArgumentParsers.REGISTER.parse(stateContainer, "R" + i);
                        if (!rtn.contains(reg)) {
                            rtn.add(reg);
                        } else {
                            logger.log(Level.WARNING, "Duplicate register in array");
                        }
                    }
                } else {
                    Register reg = ArgumentParsers.REGISTER.parse(stateContainer, regString.strip());
                    if (!rtn.contains(reg)) {
                        rtn.add(reg);
                    } else {
                        logger.log(Level.WARNING, "Duplicate register in array");
                    }
                }
            }

            return rtn.toArray(new Register[0]);
        } else {
            throw new SyntaxASMException("Unexpected value '" + string + "' (expected a Register Array)");
        }
    }

    @Override
    public Register[] none() {
        throw new BadArgumentASMException("missing register array");
    }
}
