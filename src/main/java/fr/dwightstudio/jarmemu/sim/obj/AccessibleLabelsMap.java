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
    
    private final HashMap<String, Integer> labels;
    private final HashMap<String, FilePos> globals;
    private final int currentfileIndex;

    public AccessibleLabelsMap(HashMap<String, Integer> labels, HashMap<String, FilePos> globals, int currentfileIndex) {
        this.labels = labels;
        this.globals = globals;
        this.currentfileIndex = currentfileIndex;
    }

    @Override
    public int size() {
        int rtn = labels.size();

        for (Entry<String, FilePos> entry : globals.entrySet()) {
            if (entry.getValue().getFileIndex() != currentfileIndex) rtn++;
        }

        return rtn;
    }

    @Override
    public boolean isEmpty() {
        return labels.isEmpty() && globals.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (labels.containsKey(key)) {
            return true;
        } else {
            return globals.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        if (labels.containsValue(value)) {
            return true;
        } else {
            if (value instanceof Integer i) {
                return globals.values().stream().anyMatch(filePos -> filePos.getPos() == i);
            } else {
                return false;
            }
        }
    }

    @Override
    public Integer get(Object key) {
        if (labels.containsKey(key)) {
            return labels.get(key);
        } else {
            if (globals.containsKey(key)) {
                return globals.get(key).getPos();
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Integer put(String key, Integer value) {
        return labels.put(key, value);
    }

    @Override
    public Integer remove(Object key) {
        return labels.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends Integer> m) {
        labels.putAll(m);
    }

    @Override
    public void clear() {
        labels.clear();
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        Set<String> rtn = new HashSet<>(size());
        rtn.addAll(labels.keySet());

        for (Entry<String, FilePos> entry : globals.entrySet()) {
            if (entry.getValue().getFileIndex() != currentfileIndex) rtn.add(entry.getKey());
        }

        return rtn;
    }

    @NotNull
    @Override
    public Collection<Integer> values() {
        ArrayList<Integer> rtn = new ArrayList<>(size());
        rtn.addAll(labels.values());

        for (Entry<String, FilePos> entry : globals.entrySet()) {
            if (entry.getValue().getFileIndex() != currentfileIndex) rtn.add(entry.getValue().getPos());
        }

        return rtn;
    }

    @NotNull
    @Override
    public Set<Entry<String, Integer>> entrySet() {
        Set<Entry<String, Integer>> rtn = new HashSet<>(size());
        rtn.addAll(labels.entrySet());

        for (Entry<String, FilePos> entry : globals.entrySet()) {
            if (entry.getValue().getFileIndex() != currentfileIndex) rtn.add(MapUtils.extractPos(entry));
        }

        return rtn;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof AccessibleLabelsMap map
                && map.labels.equals(labels)
                && map.globals.equals(globals)
                && map.currentfileIndex == currentfileIndex);
    }

    @Override
    public int hashCode() {
        return labels.hashCode() * globals.hashCode();
    }
}
