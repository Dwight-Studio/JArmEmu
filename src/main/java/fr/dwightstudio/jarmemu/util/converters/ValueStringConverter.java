package fr.dwightstudio.jarmemu.util.converters;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import javafx.util.StringConverter;

public class ValueStringConverter extends StringConverter<Number> {

    private final JArmEmuApplication application;

    public ValueStringConverter(JArmEmuApplication application) {
        this.application = application;
    }

    @Override
    public Integer fromString(String string) {
        switch (application.getSettingsController().getDataFormat()) {
            case 0 -> {
                return Integer.parseUnsignedInt(string, 16);
            }

            case 1 -> {
                return Integer.parseInt(string);
            }

            case 2 -> {
                return Integer.parseUnsignedInt(string);
            }

            default -> throw new IllegalStateException("Invalid format");
        }
    }

    @Override
    public String toString(Number number) {
        return application.getFormattedData((int) number);
    }
}
