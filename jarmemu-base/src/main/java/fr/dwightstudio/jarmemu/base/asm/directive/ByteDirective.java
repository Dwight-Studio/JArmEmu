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

package fr.dwightstudio.jarmemu.base.asm.directive;

import fr.dwightstudio.jarmemu.base.asm.Section;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.entity.FilePos;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.base.util.WordUtils;
import org.jetbrains.annotations.NotNull;

public class ByteDirective extends ParsedDirective {

    private final String[] arg;
    private final byte[] byteArray;

    public ByteDirective(Section section, @NotNull String args) throws SyntaxASMException {
        super(section, args);

        if (!args.isBlank() && !section.allowDataInitialisation()) {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.directive.illegalDataInitialization", "Byte", section.name()));
        }

        if (args.isBlank()) {
            arg = new String[0];
            byteArray = new byte[0];
        } else {
            arg = args.split(",");
            byteArray = new byte[arg.length];
        }
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        try {
            for (int i = 0; i < arg.length; i++) {
                long data = stateContainer.evalWithAccessible(arg[i].strip());
                if (!WordUtils.overflows(data, 8)) {
                        byteArray[i] = (byte) data;
                } else {
                    throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.directive.overflowingByteValue", args));
                }
            }
        } catch (NumberFormatException exception) {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.directive.invalidArgument", args, "Byte"));
        }
    }

    @Override
    public void execute(StateContainer stateContainer) throws ASMException {
        FilePos tempPos = stateContainer.getCurrentMemoryPos().clone();
        for (byte b : byteArray) {
            stateContainer.getMemory().putByte(tempPos.getPos(), b);
            tempPos.incrementPos();
        }
    }

    @Override
    public void offsetMemory(StateContainer stateContainer) throws ASMException {
        if (args.isBlank()) stateContainer.getCurrentMemoryPos().incrementPos();
        stateContainer.getCurrentMemoryPos().incrementPos(arg.length);
    }

    @Override
    public boolean isContextBuilder() {
        return false;
    }
}
