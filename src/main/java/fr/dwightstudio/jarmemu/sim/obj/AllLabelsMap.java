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

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multiset.HashMultiSet;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AllLabelsMap implements MultiValuedMap<String, FilePos> {

    private final ArrayList<HashMap<String, Integer>> labels;

    public AllLabelsMap(ArrayList<HashMap<String, Integer>> labels) {
        this.labels = labels;
    }

    @Override
    public int size() {
        return labels.stream().reduce(0, (i, map) -> i + map.size(), Integer::sum);
    }

    @Override
    public boolean isEmpty() {
        return labels.stream().allMatch(Map::isEmpty);
    }

    @Override
    public boolean containsKey(Object key) {
        return labels.stream().anyMatch(map -> map.containsKey(key));
    }

    @Override
    public boolean containsValue(Object value) {
        if (!(value instanceof FilePos pos)) return false;

        return labels.size() > pos.getFileIndex() && labels.get(pos.getPos()).containsValue(pos.getPos());
    }

    @Override
    public boolean containsMapping(Object o, Object o1) {
        return false;
    }

    @Override
    public Collection<FilePos> get(String key) {
        if (!(key instanceof String s)) return null;

        ArrayList<FilePos> rtn = new ArrayList<>();

        for (int i = 0 ; i < labels.size() ; i++) {
            if (labels.get(i).containsKey(s)) {
                rtn.add(new FilePos(i, labels.get(i).get(s)));
            }
        }

        return rtn;
    }

    @Override
    public boolean put(String key, FilePos value) {
        return labels.get(value.getFileIndex()).put(key, value.getPos()) != null;
    }

    @Override
    public boolean putAll(String string, Iterable<? extends FilePos> iterable) {
        return false;
    }

    @Override
    public Collection<FilePos> remove(Object key) {
        if (!(key instanceof String s)) return (Collection<FilePos>) Collections.EMPTY_LIST;

        ArrayList<FilePos> rtn = new ArrayList<>();

        for (int i = 0 ; i < labels.size() ; i++) {
            if (labels.get(i).containsKey(s)) {
                rtn.add(new FilePos(i, labels.get(i).get(s)));
                labels.get(i).remove(s);
            }
        }

        return rtn;
    }

    @Override
    public boolean removeMapping(Object o, Object o1) {
        return false;
    }

    @Override
    public boolean putAll(@NotNull Map<? extends String, ? extends FilePos> m) {
        m.forEach(this::put);
        return true;
    }

    @Override
    public boolean putAll(MultiValuedMap<? extends String, ? extends FilePos> multiValuedMap) {
        multiValuedMap.entries().forEach(entry -> put(entry.getKey(), entry.getValue()));
        return true;
    }

    @Override
    public void clear() {
        labels.clear();
    }

    @Override
    public Collection<Map.Entry<String, FilePos>> entries() {
        return null;
    }

    @Override
    public MultiSet<String> keys() {
        HashMultiSet<String> rtn = new HashMultiSet<>();

        labels.forEach(map -> rtn.addAll(map.keySet()));

        return rtn;
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        HashSet<String> rtn = new HashSet<>(size());

        labels.forEach(map -> rtn.addAll(map.keySet()));

        return rtn;
    }

    @NotNull
    @Override
    public Collection<FilePos> values() {
        ArrayList<FilePos> rtn = new ArrayList<>(size());

        for (int i = 0 ; i < labels.size() ; i++) {
            for (Integer p : labels.get(i).values()) rtn.add(new FilePos(i, p));
        }

        return rtn;
    }

    @Override
    public Map<String, Collection<FilePos>> asMap() {
        HashMap<String, Collection<FilePos>> rtn = new HashMap<>();

        keys().forEach(s -> rtn.put(s, get(s)));

        return rtn;
    }

    @Override
    public MapIterator<String, FilePos> mapIterator() {
        return null;
    }
}
