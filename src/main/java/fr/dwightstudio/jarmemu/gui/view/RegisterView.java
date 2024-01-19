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

package fr.dwightstudio.jarmemu.gui.view;

import fr.dwightstudio.jarmemu.sim.obj.Register;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;

public class RegisterView {
    private final ObservableValue<Register> registerObservable;
    private final StringProperty nameProperty;
    private final IntegerProperty valueProperty;

    public RegisterView(Register register, String name) {
        this.registerObservable = new ObservableValueRegister(register);
        this.nameProperty = new ReadOnlyStringWrapper(name);
        this.valueProperty = register.getDataProperty();
    }

    public ReadOnlyStringProperty getNameProperty() {
        return nameProperty;
    }

    public IntegerProperty getValueProperty() {
        return valueProperty;
    }

    public Register getRegister() {
        return registerObservable.getValue();
    }

    public ObservableValue<Register> getRegisterObservable() {
        return registerObservable;
    }

    public class ObservableValueRegister extends ObservableValueBase<Register> {

        final private Register register;

        public ObservableValueRegister(Register register) {
            this.register = register;
            register.getDataProperty().addListener(((observableValue, oldVal, newVal) -> {
                this.fireValueChangedEvent();
            }));
        }

        @Override
        public Register getValue() {
            return register;
        }
    }
}
