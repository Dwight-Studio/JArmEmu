package fr.dwightstudio.jarmemu.sim.obj;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Register {

    private final IntegerProperty dataProperty;

    public Register() {
        this.dataProperty = new SimpleIntegerProperty();
    }

    public int getData() {
        return dataProperty.get();
    }

    public void setData(int data) throws IllegalArgumentException {
        this.dataProperty.set(data);
    }

    public boolean get(int index) throws IllegalArgumentException {
        if (index >= 32) throw new IllegalArgumentException("Invalid index: " + index);

        return ((dataProperty.get() >> index) & 1) == 1;
    }

    public void set(int index, boolean value) {
        if (index >= 32) throw new IllegalArgumentException("Invalid index: " + index);

        if (value) {
            dataProperty.set(dataProperty.get() | (1 << index)); // set a bit to 1
        } else {
            dataProperty.set(dataProperty.get() & ~(1 << index)); // set a bit to 0
        }
    }

    public void add(int value) {
        this.dataProperty.add(value);
    }

    public boolean isPSR() {
        return false;
    }

    public IntegerProperty getProperty() {
        return dataProperty;
    }
}
