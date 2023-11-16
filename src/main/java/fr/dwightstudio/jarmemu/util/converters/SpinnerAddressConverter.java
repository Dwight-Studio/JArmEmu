/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2023 Dwight Studio
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.dwightstudio.jarmemu.util.converters;

import javafx.application.Platform;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

public class SpinnerAddressConverter extends StringConverter<Integer> {
    protected static final String DATA_FORMAT = "%08x";

    private final SpinnerValueFactory<Integer> value;

    public SpinnerAddressConverter(SpinnerValueFactory<Integer> value) {
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
            int n = Integer.parseUnsignedInt(string, 16);
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
        return String.format(DATA_FORMAT, n).toUpperCase();
    }
}
