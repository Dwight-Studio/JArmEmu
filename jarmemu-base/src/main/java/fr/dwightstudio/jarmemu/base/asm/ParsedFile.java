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

package fr.dwightstudio.jarmemu.base.asm;

import fr.dwightstudio.jarmemu.base.sim.SourceScanner;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class ParsedFile implements Collection<ParsedObject> {

    public static final int INITIAL_SIZE = 256;

    private final SourceScanner sourceScanner;

    private ParsedObject[] content;
    private int size;

    public ParsedFile(SourceScanner sourceScanner) {
        this.sourceScanner = sourceScanner;
        this.content = new ParsedObject[INITIAL_SIZE];
        this.size = 0;
    }

    public int getIndex() {
        return sourceScanner.getFileIndex();
    }

    public String getName() {
        return this.sourceScanner.getName() == null ? "???" : this.sourceScanner.getName();
    }

    public SourceScanner getSourceScanner() {
        return sourceScanner;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        for (ParsedObject object : content) {
            if (Objects.equals(object, o)) return true;
        }
        return false;
    }

    @NotNull
    @Override
    public Iterator<ParsedObject> iterator() {
        return new Iterator<ParsedObject>() {

            int p = 0;

            @Override
            public boolean hasNext() {
                return p < size;
            }

            @Override
            public ParsedObject next() {
                return content[p++];
            }
        };
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        Object[] rtn = new Object[size];
        System.arraycopy(content, 0, rtn, 0, size);
        return rtn;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <T> T @NotNull [] toArray(T @NotNull [] a) {
        T[] rtn = (T[]) new Object[size];
        System.arraycopy(content, 0, rtn, 0, size);
        return rtn;
    }

    public ParsedObject[] get(int line) {
        ArrayList<ParsedObject> rtn = new ArrayList<>();
        this.forEach(parsedObject -> {
            if (parsedObject.getFilePos().getPos() == line) rtn.add(parsedObject);
        });

        return rtn.toArray(new ParsedObject[0]);
    }

    @Override
    public boolean add(ParsedObject parsedObject) {
        if (parsedObject == null) return false;

        parsedObject.setFile(this);

        if (content.length <= size) expand();

        if (size > 0) {
            size++;
            for (int i = size-2; i >= 0; i--) {
                if (content[i] != null) {
                    if (parsedObject.getFilePos().compareTo(content[i].getFilePos()) >= 0) {
                        content[i + 1] = parsedObject;
                        break;
                    } else {
                        content[i + 1] = content[i];
                    }
                }
            }
        } else {
            content[0] = parsedObject;
            size++;
        }

        return true;
    }

    private void expand() {
        ParsedObject[] tmp = new ParsedObject[content.length * 2];
        System.arraycopy(content, 0, tmp, 0, size);
        content = tmp;
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof ParsedObject parsedObject)) return false;

        int i;
        for (i = 0; i < size; i++) {
            if (content[i] == null) return false;
            if (content[i].equals(parsedObject)) {
                break;
            }
        }

        if (i == size - 1 && !content[i].equals(parsedObject)) return false;

        for (int e = i; e < size - 1; e++) {
            content[e] = content[e + 1];
        }

        return true;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return c.stream().allMatch(this::contains);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends ParsedObject> c) {
        AtomicBoolean rtn = new AtomicBoolean(false);
        c.forEach(e -> rtn.set(rtn.get() | add(e)));
        return rtn.get();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        AtomicBoolean rtn = new AtomicBoolean(false);
        c.forEach(e -> rtn.set(rtn.get() | remove(e)));
        return rtn.get();
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        AtomicBoolean rtn = new AtomicBoolean(false);
        c.forEach(e -> {
            if (contains(e)) rtn.set(rtn.get() | remove(e));
        });
        return rtn.get();
    }

    @Override
    public void clear() {
        this.content = new ParsedObject[INITIAL_SIZE];
        this.size = 0;
    }

    public void filter(Predicate<ParsedObject> predicate) {
        ArrayList<ParsedObject> toDelete = new ArrayList<>();
        this.forEach(obj -> {
            if (!predicate.test(obj)) toDelete.add(obj);
        });
        removeAll(toDelete);
    }
}
