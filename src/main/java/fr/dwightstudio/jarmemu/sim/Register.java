package fr.dwightstudio.jarmemu.sim;

import org.jetbrains.annotations.NotNull;

public class Register {

    public static final int BYTE_NUMBER = 4;
    private byte[] data;

    public Register() {
        this.data = new byte[BYTE_NUMBER];
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte @NotNull [] data) throws IllegalArgumentException {
        if (data.length <= BYTE_NUMBER) {
            this.data = new byte[BYTE_NUMBER];

            System.arraycopy(data, 0, this.data, 0, data.length);
        } else {
            throw new IllegalArgumentException("Expected 4 bytes, got " + data.length);
        }
    }

    public boolean get(int index) throws IllegalArgumentException {
        if (index >= 32) throw new IllegalArgumentException("Invalid index: " + index);

        int byteIndex = index / 8;
        int internalIndex = index % 8;

        int value = (data[byteIndex] >> internalIndex) & 1;

        return value == 1;
    }

    public void set(int index, boolean value) {
        if (index >= 32) throw new IllegalArgumentException("Invalid index: " + index);

        int byteIndex = index / 8;
        int internalIndex = index % 8;

        if (value) {
            data[byteIndex] |= (byte) (1 << internalIndex); // set a bit to 1
        } else {
            data[byteIndex] &= (byte) ~(1 << internalIndex); // set a bit to 0
        }
    }
}
