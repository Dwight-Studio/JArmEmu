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
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

// Correspond à "reg!", à utiliser avec ShiftParser
public class
RegisterWithUpdateParser implements ArgumentParser<RegisterWithUpdateParser.UpdatableRegister> {

    @Override
    public UpdatableRegister parse(@NotNull StateContainer stateContainer, @NotNull String string) {
        boolean update = false;

        if (string.endsWith("!")) {
            update = true;
            string = string.substring(0, string.length()-1);
        }

        return new UpdatableRegister(ArgumentParsers.REGISTER.parse(stateContainer, string), update, stateContainer);
    }

    @Override
    public UpdatableRegister none() {
        throw new BadArgumentASMException("missing register");
    }

    public static final class UpdatableRegister extends Register {
        private final Register register;
        private final StateContainer stateContainer;
        private boolean update;

        public UpdatableRegister(Register register, boolean update, StateContainer stateContainer) {
            this.register = register;
            this.update = update;
            this.stateContainer = stateContainer;
        }

        @Override
        public int getData() {
            return register.getData();
        }

        @Override
        public void setData(int data) throws IllegalArgumentException {
            register.setData(data);
        }

        @Override
        public boolean get(int index) throws IllegalArgumentException {
            return register.get(index);
        }

        @Override
        public void set(int index, boolean value) {
            register.set(index, value);
        }

        @Override
        public void add(int value) {
            register.add(value);
        }

        /**
         * Met à jour le registre en fonction du nombre de registres de l'argument RegisterArray
         */
        public void update(int value) {
            if (update) register.add(value);
            update = false;
        }
    }
}
