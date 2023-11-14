package fr.dwightstudio.jarmemu.util.converters;

import javafx.util.StringConverter;

public class HexStringConverter extends StringConverter<Number> {

    public static final String HEX_FORMAT = "%08x";

    @Override
    public String toString(Number number) {
        return String.format(HEX_FORMAT, (int) number).toUpperCase();
    }

    @Override
    public Number fromString(String s) {
        return Integer.parseUnsignedInt(s, 16);
    }
}
