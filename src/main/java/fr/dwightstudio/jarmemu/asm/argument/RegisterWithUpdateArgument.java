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

package fr.dwightstudio.jarmemu.asm.argument;

import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.BadArgumentASMException;
import fr.dwightstudio.jarmemu.asm.exception.ExecutionASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.entity.Register;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;

import java.util.function.Supplier;

public class RegisterWithUpdateArgument extends ParsedArgument<RegisterWithUpdateArgument.UpdatableRegister> {

    boolean update;
    RegisterArgument argument;

    public RegisterWithUpdateArgument(String originalString) throws SyntaxASMException {
        super(originalString);

        if (originalString == null) throw new BadArgumentASMException(JArmEmuApplication.formatMessage("%exception.argument.missingRegister"));

        update = false;

        String string = originalString;

        if (string.endsWith("!")) {
            update = true;
            string = string.substring(0, string.length()-1);
        }

        argument = new RegisterArgument(string);
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        argument.contextualize(stateContainer);
    }

    @Override
    public UpdatableRegister getValue(StateContainer stateContainer) throws ExecutionASMException {
        return new UpdatableRegister(argument.getValue(stateContainer), update);
    }

    @Override
    public void verify(Supplier<StateContainer> stateSupplier) throws ASMException {
        argument.verify(stateSupplier);

        super.verify(stateSupplier);
    }

    public static final class UpdatableRegister extends Register {
        private final Register register;
        private boolean update;

        public UpdatableRegister(Register register, boolean update) {
            this.register = register;
            this.update = update;
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
