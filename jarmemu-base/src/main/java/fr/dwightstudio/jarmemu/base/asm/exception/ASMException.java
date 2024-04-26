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

package fr.dwightstudio.jarmemu.base.asm.exception;

import fr.dwightstudio.jarmemu.base.asm.ParsedFile;
import fr.dwightstudio.jarmemu.base.asm.ParsedObject;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;

public class ASMException extends Exception {
    int line;
    ParsedObject parsedObject;
    private ParsedFile file;

    public ASMException(String s) {
        super(s);
        line = -1;
    }

    public ASMException() {
        super();
        line = -1;
    }

    public String getTitle() {
        return JArmEmuApplication.formatMessage("%exception.base");
    }

    public boolean isLineSpecified() {
        return line != -1;
    }

    public boolean isFileSpecified() {
        return file != null;
    }

    public int getLine() {
        return line;
    }

    public ParsedFile getFile() {
        return file;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setFile(ParsedFile file) {
        this.file = file;
    }

    public ParsedObject getObject() {
        return parsedObject;
    }

    public void setObject(ParsedObject obj) {
        this.parsedObject = obj;
    }

    public ASMException with(ParsedObject parsedObject) {
        this.parsedObject = parsedObject;
        return this;
    }

    public ASMException with(int line) {
        this.line = line;
        return this;
    }

    public ASMException with(ParsedFile file) {
        this.file = file;
        return this;
    }
}
