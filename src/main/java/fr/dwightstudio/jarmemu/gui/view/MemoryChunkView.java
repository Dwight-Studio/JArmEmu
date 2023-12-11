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

import fr.dwightstudio.jarmemu.sim.obj.MemoryAccessor;
import fr.dwightstudio.jarmemu.util.converters.WordASCIIStringConverter;
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;

public class MemoryChunkView {
    private final MemoryAccessor memoryAccessor;
    private final ReadOnlyIntegerProperty addressProperty;
    private final IntegerProperty value0Property;
    private final IntegerProperty value1Property;
    private final IntegerProperty value2Property;
    private final IntegerProperty value3Property;

    public MemoryChunkView(MemoryAccessor memoryAccessor, int address) {
        this.memoryAccessor = memoryAccessor;
        this.addressProperty = new ReadOnlyIntegerWrapper(address);
        this.value0Property = memoryAccessor.getProperty(address);
        this.value1Property = memoryAccessor.getProperty(address + 4);
        this.value2Property = memoryAccessor.getProperty(address + 8);
        this.value3Property = memoryAccessor.getProperty(address + 12);
    }

    public MemoryAccessor getMemoryAccessor() {
        return memoryAccessor;
    }

    public ReadOnlyIntegerProperty getAddressProperty() {
        return addressProperty;
    }

    public IntegerProperty getValue0Property() {
        return value0Property;
    }

    public IntegerProperty getValue1Property() {
        return value1Property;
    }

    public IntegerProperty getValue2Property() {
        return value2Property;
    }

    public IntegerProperty getValue3Property() {
        return value3Property;
    }

    public ObservableValue<String> getASCIIProperty() {
        return new ChunkASCIIProperty();
    }

    public class ChunkASCIIProperty extends ReadOnlyStringProperty {

        private final ArrayList<ChangeListener<? super String>> changeListeners;
        private final ArrayList<InvalidationListener> invalidationListeners;

        private WordASCIIStringConverter converter;
        private String lastVal;

        public ChunkASCIIProperty() {
            converter = new WordASCIIStringConverter();
            changeListeners = new ArrayList<>();
            invalidationListeners = new ArrayList<>();

            value0Property.addListener(((observableValue, oldVal, newVal) -> {
                notifyChange();
            }));

            value1Property.addListener(((observableValue, oldVal, newVal) -> {
                notifyChange();
            }));

            value2Property.addListener(((observableValue, oldVal, newVal) -> {
                notifyChange();
            }));

            value3Property.addListener(((observableValue, oldVal, newVal) -> {
                notifyChange();
            }));

            lastVal = get();
        }

        @Override
        public Object getBean() {
            return MemoryWordView.class;
        }

        @Override
        public String getName() {
            return "chunkASCII";
        }

        @Override
        public String get() {
            return converter.toString(value0Property.get())
                    + converter.toString(value1Property.get())
                    + converter.toString(value2Property.get())
                    + converter.toString(value3Property.get());
        }

        private void notifyChange() {
            String newVal = get();

            if (newVal.equals(lastVal)) return;

            changeListeners.forEach(changeListener -> changeListener.changed(this, lastVal, newVal));
            invalidationListeners.forEach(invalidationListener -> invalidationListener.invalidated(this));
        }

        @Override
        public void addListener(ChangeListener<? super String> changeListener) {
            changeListeners.add(changeListener);
        }

        @Override
        public void removeListener(ChangeListener<? super String> changeListener) {
            changeListeners.remove(changeListener);
        }

        @Override
        public void addListener(InvalidationListener invalidationListener) {
            invalidationListeners.add(invalidationListener);
        }

        @Override
        public void removeListener(InvalidationListener invalidationListener) {
            invalidationListeners.remove(invalidationListener);
        }
    }
}
