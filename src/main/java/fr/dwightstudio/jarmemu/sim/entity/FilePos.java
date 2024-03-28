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

package fr.dwightstudio.jarmemu.sim.entity;

import org.jetbrains.annotations.NotNull;

public class FilePos implements Comparable<FilePos> {
    public static final FilePos ZERO = new FilePos(0, 0).freeze();

    private int file;
    private int pos;

    public FilePos(int file, int pos) {
        this.file = file;
        this.pos = pos;
    }

    public FilePos(FilePos filePos) {
        this.file = filePos.file;
        this.pos = filePos.pos;
    }

    @Override
    public String toString() {
        return file + ":" + pos;
    }

    public int getFileIndex() {
        return file;
    }

    public int getPos() {
        return pos;
    }

    public int incrementPos(int i) {
        return pos += i;
    }

    public int incrementPos() {
        return incrementPos(1);
    }

    public void setPos(int i) {
        pos = i;
    }

    public int incrementFileIndex(int i) {
        return file += i;
    }

    public int incrementFileIndex() {
        return incrementFileIndex(1);
    }

    public void setFileIndex(int i) {
        file = i;
    }

    public int toByteValue() {
        return pos * 4;
    }

    public FilePos freeze(int offset) {
        return new FrozenFilePos(this, offset);
    }

    public FilePos freeze() {
        return new FrozenFilePos(this, 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FilePos position) {
            return position.getPos() == pos && position.getFileIndex() == file;
        } else {
            return false;
        }
    }

    public FilePos clone() {
        return new FilePos(this);
    }

    @Override
    public int compareTo(@NotNull FilePos o) {
        if (file == o.file) {
            return Integer.compare(pos, o.pos);
        } else {
            return Integer.compare(file, o.file);
        }
    }

    public static class FrozenFilePos extends FilePos {

        private final static String ERROR_MESSAGE = "Can't modify frozen position";

        private final int offset;


        public FrozenFilePos(FilePos filePos, int offset) {
            super(filePos.file, filePos.pos);
            this.offset = offset;
        }

        @Override
        public int getFileIndex() {
            return super.getFileIndex();
        }

        @Override
        public int getPos() {
            return super.getPos() + offset;
        }

        @Override
        public int incrementPos(int i) {
            throw new UnsupportedOperationException(ERROR_MESSAGE);
        }

        @Override
        public void setPos(int i) {
            throw new UnsupportedOperationException(ERROR_MESSAGE);
        }

        @Override
        public int incrementFileIndex(int i) {
            throw new UnsupportedOperationException(ERROR_MESSAGE);
        }

        @Override
        public void setFileIndex(int i) {
            throw new UnsupportedOperationException(ERROR_MESSAGE);
        }

        @Override
        public int toByteValue() {
            return getPos() * 4;
        }

        @Override
        public FilePos freeze() {
            return this;
        }
    }
}
