package fr.dwightstudio.jarmemu.util;

import javafx.application.Platform;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

public class SafeStringConverter extends StringConverter<Integer> {

    private final SpinnerValueFactory<Integer> value;

    public SafeStringConverter(SpinnerValueFactory<Integer> value) {
        this.value = value;
    }

    /**
     * Convertis les chaînes de caractères en nombre, en cas d'erreur renvoie la dernière valeur.
     *
     * @param string
     * @return
     */
    @Override
    public Integer fromString(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException exception) {
            int c = value.getValue();
            Platform.runLater(() -> value.setValue(c));
            return 0;
        }
    }

    @Override
    public String toString(Integer integer) {
        return String.valueOf(integer);
    }
}
