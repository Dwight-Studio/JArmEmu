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

package fr.dwightstudio.jarmemu.gui.view;

import fr.dwightstudio.jarmemu.sim.obj.FilePos;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.util.Map;

public class SymbolView {
    private final ReadOnlyStringWrapper name;
    private final ReadOnlyIntegerWrapper value;

    public SymbolView(Map.Entry<String, FilePos> label, boolean useFile) {
        this.name = new ReadOnlyStringWrapper(label.getKey());
        this.value = new ReadOnlyIntegerWrapper(useFile ? label.getValue().getFileIndex() : label.getValue().getPos());
    }

    public SymbolView(Map.Entry<String, Integer> symbol) {
        this.name = new ReadOnlyStringWrapper(symbol.getKey());
        this.value = new ReadOnlyIntegerWrapper(symbol.getValue());
    }


    public ReadOnlyStringProperty getNameProperty() {
        return name;
    }

    public ReadOnlyIntegerProperty getValueProperty() {
        return value;
    }
}
