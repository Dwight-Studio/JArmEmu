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

package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.sim.SourceScanner;

import java.util.TreeMap;

public class ParsedFile{

    private final TreeMap<Integer, ParsedObject> parsedObjects;
    private SourceScanner sourceScanner;

    public ParsedFile(SourceScanner sourceScanner) {
        this.sourceScanner = sourceScanner;
        this.parsedObjects = new TreeMap<>();
    }

    public ParsedFile(SourceScanner sourceScanner, TreeMap<Integer, ParsedObject> parsedObjects) {
        this.sourceScanner = sourceScanner;
        this.parsedObjects = parsedObjects;
    }

    public int getIndex() {
        return sourceScanner.getFileIndex();
    }

    public TreeMap<Integer, ParsedObject> getParsedObjects() {
        return this.parsedObjects;
    }

    public SourceScanner getSourceScanner() {
        return this.sourceScanner;
    }

    public String getName() {
        return this.sourceScanner.getName() == null ? "Unknown" : this.sourceScanner.getName();
    }
}
