/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2024 Dwight Studio
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

package fr.dwightstudio.jarmemu.base.util;

import java.util.*;

public class EnumUtils {


    /**
     * Gets all values' name from enum.
     *
     * @param list the array of all values
     * @param addEmpty true if it has to include an empty string
     * @return an array containing all the values' name
     * @param <T> the type of the enum
     */
    public static <T extends Enum<T>> String[] valuesToString(T[] list, boolean addEmpty) {
        List<String> rtn = new ArrayList<>();

        for (T elmt : list) {
            String elmtStr = elmt.toString().toUpperCase();
            if (!elmtStr.isEmpty()) rtn.add(elmtStr);
        }

        rtn.sort(Comparator.comparing(String::length));
        rtn = rtn.reversed();

        if (addEmpty) rtn.add("");

        return rtn.toArray(new String[0]);
    }

    /**
     * Gets all values' name from enum.
     *
     * @param list the array of all values
     * @return an array containing all the values' name
     * @param <T> the type of the enum
     */
    public static <T extends Enum<T>> String[] valuesToString(T[] list) {
        return valuesToString(list, false);
    }

    /**
     * Gets all values' name from enum without some values.
     *
     * @param list the array of all values
     * @param without the array of values to exclude
     * @return an array containing all the values' name
     * @param <T> the type of the enum
     */
    @SafeVarargs
    public static <T extends Enum<T>> String[] valuesToString(T[] list, T ... without) {
        String[] withoutString = valuesToString(without);
        return Arrays.stream(valuesToString(list, false)).filter(el -> !Arrays.asList(withoutString).contains(el)).toArray(String[]::new);
    }

    /**
     * Maps all values from enum to its name.
     *
     * @param list the array of all values
     * @param addEmpty true if it has to map an empty string
     * @return a map containing all the values
     * @param <T> the type of the enum
     */
    public static <T extends Enum<T>> Map<String, T> valuesToMap(T[] list, boolean addEmpty) {
        Map<String, T> rtn = new HashMap<>();

        for (T elmt : list) {
            String elmtStr = elmt.toString().toUpperCase();
            if (!elmtStr.isEmpty()) rtn.put(elmtStr, elmt);
        }

        if (addEmpty) rtn.put("", null);

        return rtn;
    }

    /**
     * Maps all values from enum to its name.
     *
     * @param list the array of all values
     * @return a map containing all the values
     * @param <T> the type of the enum
     */
    public static <T extends Enum<T>> Map<String, T> valuesToMap(T[] list) {
        return valuesToMap(list, false);
    }
}
