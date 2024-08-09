package fr.dwightstudio.jarmemu.base.asm.argument;

public enum Shift {
    LSL(0b00),
    LSR(0b01),
    ASR(0b10),
    ROR(0b11),
    RRX(0b11);

    final int code;

    Shift(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
