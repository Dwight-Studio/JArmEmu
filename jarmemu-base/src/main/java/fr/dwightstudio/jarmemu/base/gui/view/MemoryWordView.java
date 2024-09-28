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

package fr.dwightstudio.jarmemu.base.gui.view;

import fr.dwightstudio.jarmemu.base.sim.entity.MemoryAccessor;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;

import java.util.ArrayList;

public class MemoryWordView implements MemoryView {
    private final MemoryAccessor memoryAccessor;
    private final ReadOnlyIntegerProperty addressProperty;
    private final UpdatableWrapper<Number> valueProperty;
    private final BooleanProperty cursorProperty;
    private final UpdatableWrapper<Number> byte0;
    private final UpdatableWrapper<Number> byte1;
    private final UpdatableWrapper<Number> byte2;
    private final UpdatableWrapper<Number> byte3;
    private final Register sp;

    public MemoryWordView(MemoryAccessor memoryAccessor, int address) {
        this.memoryAccessor = memoryAccessor;
        this.addressProperty = new ReadOnlyIntegerWrapper(address);
        this.valueProperty = new UpdatableWrapper<>(memoryAccessor.getProperty(address));
        this.sp = null;
        this.byte0 = new UpdatableWrapper<>(new MemoryByteProperty(0));
        this.byte1 = new UpdatableWrapper<>(new MemoryByteProperty(1));
        this.byte2 = new UpdatableWrapper<>(new MemoryByteProperty(2));
        this.byte3 = new UpdatableWrapper<>(new MemoryByteProperty(3));
        this.cursorProperty = null;
    }

    public MemoryWordView(MemoryAccessor memoryAccessor, int address, Register sp) {
        this.memoryAccessor = memoryAccessor;
        this.addressProperty = new ReadOnlyIntegerWrapper(address);
        this.valueProperty = new UpdatableWrapper<>(memoryAccessor.getProperty(address));
        this.sp = sp;
        this.cursorProperty = new SimpleBooleanProperty(this.sp.getData() == address);
        this.byte0 = new UpdatableWrapper<>(new MemoryByteProperty(0));
        this.byte1 = new UpdatableWrapper<>(new MemoryByteProperty(1));
        this.byte2 = new UpdatableWrapper<>(new MemoryByteProperty(2));
        this.byte3 = new UpdatableWrapper<>(new MemoryByteProperty(3));

        this.sp.getDataProperty().addListener((obs, oldVal, newVal) -> this.cursorProperty.setValue((int) newVal == address));
    }

    public MemoryAccessor getMemoryAccessor() {
        return memoryAccessor;
    }

    public ReadOnlyIntegerProperty getAddressProperty() {
        return addressProperty;
    }

    public UpdatableWrapper<Number> getValueProperty() {
        return valueProperty;
    }

    public BooleanProperty getCursorProperty() {
        return cursorProperty;
    }

    public UpdatableWrapper<Number> getByte0Property() {
        return byte0;
    }

    public UpdatableWrapper<Number> getByte1Property() {
        return byte1;
    }

    public UpdatableWrapper<Number> getByte2Property() {
        return byte2;
    }

    public UpdatableWrapper<Number> getByte3Property() {
        return byte3;
    }

    public Register getSP() {
        return sp;
    }


    public class MemoryByteProperty extends ReadOnlyIntegerProperty {

        private final ArrayList<ChangeListener<? super Number>> changeListeners;
        private final ArrayList<InvalidationListener> invalidationListeners;

        private final int n;

        public MemoryByteProperty(int n) {
            this.n = 3 - n;
            if (this.n > 3 || this.n < 0) throw new IllegalArgumentException("n must be between 0 and 3 included");
            changeListeners = new ArrayList<>();
            invalidationListeners = new ArrayList<>();

            valueProperty.addListener(((observable, oldVal, newVal) -> {
                if (oldVal == null || getFrom(oldVal.intValue()) != getFrom(newVal.intValue())) {
                    changeListeners.forEach(changeListener -> changeListener.changed(this, oldVal == null ? 0 : getFrom(oldVal.intValue()), getFrom(newVal.intValue())));
                    invalidationListeners.forEach(invalidationListener -> invalidationListener.invalidated(observable));
                }
            }));
        }

        @Override
        public Object getBean() {
            return MemoryWordView.class;
        }

        @Override
        public String getName() {
            return "memoryByte" + n;
        }

        private int getFrom(int i) {
            return (i >>> (n * 8)) & 0xFF;
        }

        @Override
        public int get() {
            return getFrom(valueProperty.getValue().intValue());
        }

        @Override
        public void addListener(ChangeListener<? super Number> changeListener) {
            changeListeners.add(changeListener);
        }

        @Override
        public void removeListener(ChangeListener<? super Number> changeListener) {
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
