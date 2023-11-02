package fr.dwightstudio.jarmemu.util;

import javafx.application.Platform;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

public class SafeAddressConverter extends StringConverter<Integer> {
    protected static final String DATA_FORMAT = "%08x";

    private final SpinnerValueFactory<Integer> value;

    public SafeAddressConverter(SpinnerValueFactory<Integer> value) {
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
            int n = Integer.parseInt(string, 16);
            return n - (n % 4);
        } catch (NumberFormatException exception) {
            int c = value.getValue();
            Platform.runLater(() -> value.setValue(c));
            return 0;
        }
    }

    @Override
    public String toString(Integer integer) {
        int n = (integer == null ? 0 : integer);
        return String.format(DATA_FORMAT, n);
    }
}
