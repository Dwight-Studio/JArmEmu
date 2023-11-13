package fr.dwightstudio.jarmemu.util;

import javafx.util.StringConverter;

import java.util.function.Function;
import java.util.function.Supplier;

public class ValueHexStringConverter extends StringConverter<Number> {

    private final Function<Integer, String> formatter;

    public ValueHexStringConverter(Function<Integer, String> formatter) {
        this.formatter = formatter;
    }

    /**
     * Convertis les chaînes de caractères en nombre, en cas d'erreur renvoie la dernière valeur.
     *
     * @param string
     * @return
     */
    @Override
    public Integer fromString(String string) {
        return Integer.parseInt(string,16);
    }

    @Override
    public String toString(Number number) {
        return formatter.apply(number.intValue());
    }
}
