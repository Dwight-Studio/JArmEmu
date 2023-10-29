package fr.dwightstudio.jarmemu.asm;

public enum DataMode {
    HALF_WORD,
    BYTE;

    @Override
    public String toString() {
        return name().substring(0,1);
    }

    public static DataMode customValueOf(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        } else if (name.equalsIgnoreCase("H")) {
            return HALF_WORD;
        } else if (name.equalsIgnoreCase("B")) {
            return BYTE;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
