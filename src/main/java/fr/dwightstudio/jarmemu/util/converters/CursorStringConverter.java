package fr.dwightstudio.jarmemu.util.converters;

import fr.dwightstudio.jarmemu.sim.obj.Register;
import javafx.util.StringConverter;

public class CursorStringConverter extends StringConverter<Boolean> {

    @Override
    public String toString(Boolean bool) {
        return bool ? "➤" : "";
    }

    @Override
    public Boolean fromString(String s) {
        return null;
    }
}
