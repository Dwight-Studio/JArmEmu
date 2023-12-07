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

public class FileLine {
    public static final FileLine ZERO = new FileLine(0, 0).freeze();

    private final int file;
    private int pos;

    public FileLine(int file, int pos) {
        this.file = file;
        this.pos = pos;
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

    public int increment() {
        return ++pos;
    }

    public void set(int i) {
        pos = i;
    }

    public int toByteValue() {
        return pos * 4;
    }

    public FileLine freeze() {
        return new FrozenFileLine(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileLine position) {
            return position.getPos() == pos && position.getFileIndex() == file;
        } else {
            return false;
        }
    }

    public static class FrozenFileLine extends FileLine {

        public FrozenFileLine(FileLine fileLine) {
            super(fileLine.file, fileLine.pos);
        }

        @Override
        public int getFileIndex() {
            return super.getFileIndex();
        }

        @Override
        public int getPos() {
            return super.getPos();
        }

        @Override
        public int increment() {
            throw new UnsupportedOperationException("Can't modify frozen position");
        }

        @Override
        public void set(int i) {
            throw new UnsupportedOperationException("Can't modify frozen position");
        }

        @Override
        public int toByteValue() {
            return super.toByteValue();
        }

        @Override
        public FileLine freeze() {
            return this;
        }
    }
}
