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

package fr.dwightstudio.jarmemu.base.sim.entity;

import fr.dwightstudio.jarmemu.base.util.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AccessibleValueMap implements Map<String, Integer> {
    
    private final ArrayList<HashMap<String, Integer>> allValues;
    private final HashMap<String, Integer> localvalues;
    private final HashMap<String, Integer> globals;
    private final int currentfileIndex;

    public AccessibleValueMap(ArrayList<HashMap<String, Integer>> allValues, HashMap<String, Integer> globals, int currentfileIndex) {
        this.allValues = allValues;
        this.localvalues = allValues.get(currentfileIndex);
        this.globals = globals;
        this.currentfileIndex = currentfileIndex;
    }

    @Override
    public int size() {
        int rtn = localvalues.size();

        for (Entry<String, Integer> entry : globals.entrySet()) {
            if (entry.getValue() != currentfileIndex && get(entry.getKey()) != null) rtn++;
        }

        return rtn;
    }

    @Override
    public boolean isEmpty() {
        return localvalues.isEmpty() && globals.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (localvalues.containsKey(key)) {
            return true;
        } else {
            return globals.containsKey(key) && get(key) != null;
        }
    }

    @Override
    public boolean containsValue(Object value) {
        if (localvalues.containsValue(value)) {
            return true;
        } else {
            return globals.values().stream().anyMatch(filePos -> allValues.get(filePos).containsValue(value));
        }
    }

    @Override
    public Integer get(Object key) {
        if (localvalues.containsKey(key)) {
            return localvalues.get(key);
        } else {
            if (globals.containsKey(key)) {
                return allValues.get(globals.get(key)).get(key);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Integer put(String key, Integer value) {
        return localvalues.put(key, value);
    }

    @Override
    public Integer remove(Object key) {
        if (localvalues.containsKey(key)) {
            if (globals.containsKey(key) && globals.get(key) == currentfileIndex) globals.remove(key);
            return localvalues.remove(key);
        }

        return null;
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends Integer> m) {
        localvalues.putAll(m);
    }

    @Override
    public void clear() {
        allValues.clear();
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        Set<String> rtn = new HashSet<>(size());
        rtn.addAll(localvalues.keySet());

        for (Entry<String, Integer> entry : globals.entrySet()) {
            if (entry.getValue() != currentfileIndex && get(entry.getKey()) != null) rtn.add(entry.getKey());
        }

        return rtn;
    }

    @NotNull
    @Override
    public Collection<Integer> values() {
        ArrayList<Integer> rtn = new ArrayList<>(size());
        rtn.addAll(localvalues.values());

        for (Entry<String, Integer> entry : globals.entrySet()) {
            if (entry.getValue() != currentfileIndex) rtn.add(allValues.get(entry.getValue()).get(entry.getKey()));
        }

        return rtn;
    }

    @NotNull
    @Override
    public Set<Entry<String, Integer>> entrySet() {
        Set<Entry<String, Integer>> rtn = new HashSet<>(size());
        rtn.addAll(localvalues.entrySet());

        for (Entry<String, Integer> entry : globals.entrySet()) {
            if (entry.getValue() != currentfileIndex && get(entry.getKey()) != null)
                rtn.add(MapUtils.entry(entry.getKey(), allValues.get(entry.getValue()).get(entry.getKey())));
        }

        return rtn;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof AccessibleValueMap map
                && map.allValues.equals(allValues)
                && map.globals.equals(globals)
                && map.currentfileIndex == currentfileIndex);
    }

    @Override
    public int hashCode() {
        return allValues.hashCode() * globals.hashCode();
    }
}
