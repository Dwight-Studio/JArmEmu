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

import fr.dwightstudio.jarmemu.base.sim.entity.FilePos;

import java.util.Map;

public class MapUtils {

    public static Map.Entry<String, Integer> extractPos(Map.Entry<String, FilePos> entry) {
        return new Map.Entry<>() {
            @Override
            public String getKey() {
                return entry.getKey();
            }

            @Override
            public Integer getValue() {
                return entry.getValue().getPos();
            }

            @Override
            public Integer setValue(Integer value) {
                Integer old = entry.getValue().getPos();
                entry.getValue().setPos(value);
                return old;
            }

            @Override
            public boolean equals(Object o) {
                if (o instanceof Map.Entry<?, ?> entry) {
                    return entry.getValue().equals(getValue()) && entry.getKey().equals(getKey());
                } else {
                    return false;
                }
            }

            @Override
            public int hashCode() {
                return getKey().hashCode();
            }
        };
    }

    public static Map.Entry<String, Integer> entry(String key, Integer value) {
        return new Map.Entry<>() {
            @Override
            public String getKey() {
                return key;
            }

            @Override
            public Integer getValue() {
                return value;
            }

            @Override
            public Integer setValue(Integer value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean equals(Object o) {
                if (o instanceof Map.Entry<?, ?> entry) {
                    return entry.getValue().equals(getValue()) && entry.getKey().equals(getKey());
                } else {
                    return false;
                }
            }

            @Override
            public int hashCode() {
                return getKey().hashCode();
            }
        };
    }
}
