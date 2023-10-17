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

            System.arraycopy(data, 0, this.data, BYTE_NUMBER - data.length, data.length);
        } else {
            throw new IllegalArgumentException("Expected 4 bytes, got " + data.length);
        }
    }
}
