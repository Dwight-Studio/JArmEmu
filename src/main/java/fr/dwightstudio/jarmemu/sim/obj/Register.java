package fr.dwightstudio.jarmemu.sim.obj;

public class Register {

    private int data;

    public Register() {
        this.data = 0;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) throws IllegalArgumentException {
        this.data = data;
    }

    public boolean get(int index) throws IllegalArgumentException {
        if (index >= 32) throw new IllegalArgumentException("Invalid index: " + index);

        return ((data >> index) & 1) == 1;
    }

    public void set(int index, boolean value) {
        if (index >= 32) throw new IllegalArgumentException("Invalid index: " + index);

        if (value) {
            data |= (1 << index); // set a bit to 1
        } else {
            data &= ~(1 << index); // set a bit to 0
        }
    }

    public void add(int value) {
        this.data += value;
    }
}
