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

package fr.dwightstudio.jarmemu.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EnumUtils {
    public static <T extends Enum<T>> String[] getFromEnum(T[] list, boolean addEmpty) {
        List<String> rtn = new ArrayList<>();

        for (T elmt : list) {
            rtn.add(elmt.toString().toUpperCase());
        }

        rtn.sort(Comparator.comparing(String::length));
        rtn = rtn.reversed();

        if (addEmpty) rtn.add("");

        return rtn.toArray(new String[0]);
    }

    public static <T extends Enum<T>> String[] getFromEnum(T[] list) {
        return getFromEnum(list, false);
    }
}
