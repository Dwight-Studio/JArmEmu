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

import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;

// Correspond à "mem", à utiliser avec Value12OrRegisterParser et ShiftParser
public class AddressParser implements ArgumentParser<AddressParser.UpdatableInteger> {
    protected static HashMap<StateContainer, Integer> updateValue = new HashMap<>();

    public static void reset(StateContainer stateContainer) {
        updateValue.remove(stateContainer);
    }

    @Override
    public UpdatableInteger parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        if (string.startsWith("*")) {
            String symbol = string.substring(1).strip().toUpperCase();
            int rtn = stateContainer.getPseudoData().get(symbol);
            return new UpdatableInteger(rtn, stateContainer, false, false, null);
        } else if (!string.startsWith("[")) {
            throw new SyntaxASMException("Invalid address '" + string + "'");
        }

        boolean updateNow = string.endsWith("!");

        if (updateNow) string = string.substring(0, string.length() - 1);

        if (string.endsWith("]")) {
            String mem = string.substring(1, string.length() - 1);
            String[] mems = mem.split(",");

            mems = Arrays.stream(mems).map(String::strip).toArray(String[]::new);

            Register reg = ArgumentParsers.REGISTER.parse(stateContainer, mems[0]);

            if (mems.length == 1) {
                return new UpdatableInteger(reg.getData(),
                        stateContainer,
                        true,
                        updateNow,
                        reg);

            } else if (mems.length == 2) {
                if (mems[1].startsWith("#")) {
                    return new UpdatableInteger(reg.getData() + ArgumentParsers.IMM.parse(stateContainer, mems[1]),
                            stateContainer,
                            false,
                            updateNow,
                            reg);
                } else {
                    return new UpdatableInteger(reg.getData() + ArgumentParsers.REGISTER.parse(stateContainer, mems[1]).getData(),
                            stateContainer,
                            false,
                            updateNow,
                            reg);
                }

            } else if (mems.length == 3) {
                ShiftParser.ShiftFunction sf = ArgumentParsers.SHIFT.parse(stateContainer, mems[2]);
                return new UpdatableInteger(reg.getData() + sf.apply(ArgumentParsers.REGISTER.parse(stateContainer, mems[1]).getData()),
                        stateContainer,
                        false,
                        updateNow,
                        reg);
            } else {
                throw new SyntaxASMException("Invalid address '" + string + "'");
            }

        } else {
            throw new SyntaxASMException("Invalid address '" + string + "'");
        }
    }

    @Override
    public AddressParser.UpdatableInteger none() {
        return null;
    }
}