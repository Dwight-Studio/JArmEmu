package fr.dwightstudio.jarmemu.gui.view;

import fr.dwightstudio.jarmemu.sim.obj.MemoryAccessor;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;

import java.util.ArrayList;

public class MemoryWordView {
    private final MemoryAccessor memoryAccessor;
    private final ReadOnlyIntegerProperty addressProperty;
    private final IntegerProperty valueProperty;
    private final BooleanProperty cursorProperty;
    private final MemoryByteProperty byte0;
    private final MemoryByteProperty byte1;
    private final MemoryByteProperty byte2;
    private final MemoryByteProperty byte3;

    public MemoryWordView(MemoryAccessor memoryAccessor, int address) {
        this.memoryAccessor = memoryAccessor;
        this.addressProperty = new ReadOnlyIntegerWrapper(address);
        this.valueProperty = memoryAccessor.getProperty(address);
        this.byte0 = new MemoryByteProperty(valueProperty, 0);
        this.byte1 = new MemoryByteProperty(valueProperty, 1);
        this.byte2 = new MemoryByteProperty(valueProperty, 2);
        this.byte3 = new MemoryByteProperty(valueProperty, 3);
        this.cursorProperty = null;
    }

    public MemoryWordView(MemoryAccessor memoryAccessor, int address, Register sp) {
        this.memoryAccessor = memoryAccessor;
        this.addressProperty = new ReadOnlyIntegerWrapper(address);
        this.valueProperty = memoryAccessor.getProperty(address);
        this.cursorProperty = new SimpleBooleanProperty(sp.getData() == address);
        this.byte0 = new MemoryByteProperty(valueProperty, 0);
        this.byte1 = new MemoryByteProperty(valueProperty, 1);
        this.byte2 = new MemoryByteProperty(valueProperty, 2);
        this.byte3 = new MemoryByteProperty(valueProperty, 3);

        sp.getDataProperty().addListener((obs, oldVal, newVal) -> this.cursorProperty.setValue((int) newVal == address));
    }

    public MemoryAccessor getMemoryAccessor() {
        return memoryAccessor;
    }

    public ReadOnlyIntegerProperty getAddressProperty() {
        return addressProperty;
    }

    public ReadOnlyIntegerProperty getValueProperty() {
        return valueProperty;
    }

    public BooleanProperty getCursorProperty() {
        return cursorProperty;
    }

    public ReadOnlyIntegerProperty getByte0Property() {
        return byte0;
    }

    public ReadOnlyIntegerProperty getByte1Property() {
        return byte1;
    }

    public ReadOnlyIntegerProperty getByte2Property() {
        return byte2;
    }
    public ReadOnlyIntegerProperty getByte3Property() {
        return byte3;
    }


    public static class MemoryByteProperty extends ReadOnlyIntegerProperty {

        ArrayList<ChangeListener<? super Number>> changeListeners;
        ArrayList<InvalidationListener> invalidationListeners;

        IntegerProperty valueProperty;
        final int n;
        final int lastVal;

        public MemoryByteProperty(IntegerProperty valueProperty, int n) {
            this.valueProperty = valueProperty;
            this.n = 3 - n;
            if (this.n > 3 || this.n < 0) throw new IllegalArgumentException("n must be between 0 and 3 included");
            changeListeners = new ArrayList<>();
            invalidationListeners = new ArrayList<>();

            valueProperty.addListener(((observableValue, oldVal, newVal) -> {
                changeListeners.forEach(changeListener -> changeListener.changed(this, getFrom(oldVal.intValue()), getFrom(newVal.intValue())));
            }));

            valueProperty.addListener(observable -> invalidationListeners.forEach(invalidationListener -> invalidationListener.invalidated(observable)));

            lastVal = get();
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
            return getFrom(valueProperty.get());
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
