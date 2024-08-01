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

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.FilePos;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;

import java.util.function.Supplier;

public abstract class ParsedObject {

    protected static final boolean VERBOSE = false;

    private ParsedFile file;
    private int lineNumber;
    private boolean generated;

    /**
     * Test the execution capacity
     *
     * @param stateSupplier the initial state container (pre-execution state)
     * @throws ASMException when an error is detected during the test
     */
    public abstract void verify(Supplier<StateContainer> stateSupplier) throws ASMException;

    public ParsedFile getFile() {
        return file;
    }

    public void setFile(ParsedFile file) {
        this.file = file;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @return the position in the program (file and line)
     */
    public FilePos getFilePos() {
        return new FilePos(file == null ? -1 : file.getIndex(), lineNumber).freeze();
    }

    /**
     * @return true if the object was generated (not directly included in the source code)
     */
    public boolean isGenerated() {
        return generated;
    }

    /**
     * Define the object as generated (not directly included in the source code)
     */
    public void setGenerated() {
        this.generated = true;
    }
}
