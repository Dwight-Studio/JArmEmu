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

package fr.dwightstudio.jarmemu.sim.obj;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Register {

    private final IntegerProperty dataProperty;

    public Register() {
        this.dataProperty = new SimpleIntegerProperty(0);
    }

    public int getData() {
        return dataProperty.get();
    }

    public void setData(int data) throws IllegalArgumentException {
        this.dataProperty.set(data);
    }

    public boolean get(int index) throws IllegalArgumentException {
        if (index >= 32) throw new IllegalArgumentException("Invalid index: " + index);

        int data = dataProperty.get();

        return ((data >> index) & 1) == 1;
    }

    public void set(int index, boolean value) {
        if (index >= 32) throw new IllegalArgumentException("Invalid index: " + index);

        int data = dataProperty.get();

        if (value) {
            data |= (1 << index); // set a bit to 1
        } else {
            data &= ~(1 << index); // set a bit to 0
        }

        dataProperty.set(data);
    }

    public void add(int value) {
        dataProperty.set(dataProperty.get() + value);
    }

    public boolean isPSR() {
        return false;
    }

    public IntegerProperty getDataProperty() {
        return dataProperty;
    }
}
