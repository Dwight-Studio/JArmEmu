package fr.dwightstudio.jarmemu.util.converters;

import fr.dwightstudio.jarmemu.util.MathUtils;
import javafx.util.StringConverter;

public class BinStringConverter extends StringConverter<Number> {
    @Override
    public String toString(Number number) {
        return MathUtils.toBinString((byte) ((int) number & 0xFF));
    }

    @Override
    public Number fromString(String s) {
        return Integer.parseUnsignedInt(s, 2);
    }
}
