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

package fr.dwightstudio.jarmemu.sim.obj;

import fr.dwightstudio.jarmemu.util.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AccessibleLabelsMap implements Map<String, Integer> {
    
    private final ArrayList<HashMap<String, Integer>> allLabels;
    private final HashMap<String, Integer> localLabels;
    private final HashMap<String, Integer> globals;
    private final int currentfileIndex;

    public AccessibleLabelsMap(ArrayList<HashMap<String, Integer>> allLabels, HashMap<String, Integer> globals, int currentfileIndex) {
        this.allLabels = allLabels;
        this.localLabels = allLabels.get(currentfileIndex);
        this.globals = globals;
        this.currentfileIndex = currentfileIndex;
    }

    @Override
    public int size() {
        int rtn = localLabels.size();

        for (Entry<String, Integer> entry : globals.entrySet()) {
            if (entry.getValue() != currentfileIndex) rtn++;
        }

        return rtn;
    }

    @Override
    public boolean isEmpty() {
        return localLabels.isEmpty() && globals.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (localLabels.containsKey(key)) {
            return true;
        } else {
            return globals.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        if (localLabels.containsValue(value)) {
            return true;
        } else {
            return globals.values().stream().anyMatch(filePos -> allLabels.get(filePos).containsValue(value));
        }
    }

    @Override
    public Integer get(Object key) {
        if (localLabels.containsKey(key)) {
            return localLabels.get(key);
        } else {
            if (globals.containsKey(key)) {
                return allLabels.get(globals.get(key)).get(key);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Integer put(String key, Integer value) {
        return localLabels.put(key, value);
    }

    @Override
    public Integer remove(Object key) {
        if (localLabels.containsKey(key)) {
            if (globals.containsKey(key) && globals.get(key) == currentfileIndex) globals.remove(key);
            return localLabels.remove(key);
        }

        return null;
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends Integer> m) {
        localLabels.putAll(m);
    }

    @Override
    public void clear() {
        allLabels.clear();
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        Set<String> rtn = new HashSet<>(size());
        rtn.addAll(localLabels.keySet());

        for (Entry<String, Integer> entry : globals.entrySet()) {
            if (entry.getValue() != currentfileIndex) rtn.add(entry.getKey());
        }

        return rtn;
    }

    @NotNull
    @Override
    public Collection<Integer> values() {
        ArrayList<Integer> rtn = new ArrayList<>(size());
        rtn.addAll(localLabels.values());

        for (Entry<String, Integer> entry : globals.entrySet()) {
            if (entry.getValue() != currentfileIndex) rtn.add(allLabels.get(entry.getValue()).get(entry.getKey()));
        }

        return rtn;
    }

    @NotNull
    @Override
    public Set<Entry<String, Integer>> entrySet() {
        Set<Entry<String, Integer>> rtn = new HashSet<>(size());
        rtn.addAll(localLabels.entrySet());

        for (Entry<String, Integer> entry : globals.entrySet()) {
            if (entry.getValue() != currentfileIndex)
                rtn.add(Map.entry(entry.getKey(), allLabels.get(entry.getValue()).get(entry.getKey())));
        }

        return rtn;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof AccessibleLabelsMap map
                && map.allLabels.equals(allLabels)
                && map.globals.equals(globals)
                && map.currentfileIndex == currentfileIndex);
    }

    @Override
    public int hashCode() {
        return allLabels.hashCode() * globals.hashCode();
    }
}
