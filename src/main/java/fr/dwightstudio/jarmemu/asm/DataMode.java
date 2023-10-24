package fr.dwightstudio.jarmemu.asm;

public enum DataMode {
    HALF_WORD,
    BYTE;

    @Override
    public String toString() {
        return name().substring(0,1);
    }
}
