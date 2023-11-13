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
