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
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class FillDirective extends ParsedDirective {

    private final String[] arg;
    int totalNum;
    int value;
    int valueSize;
    private byte[] bytes;

    public FillDirective(Section section, @NotNull String args) {
        super(section, args);

        totalNum = 0;
        value = 0;
        valueSize = 1;

        arg = args.split(",");
    }

    @Override
    public void contextualize(StateContainer stateContainer) throws ASMException {
        switch (arg.length) {
            case 1 -> totalNum = stateContainer.evalWithAccessibleConsts(arg[0]);

            case 2 -> {
                totalNum = stateContainer.evalWithAccessibleConsts(arg[0]);
                value = stateContainer.evalWithAccessibleConsts(arg[1]);
            }

            case 3 -> {
                totalNum = stateContainer.evalWithAccessibleConsts(arg[0]);
                value = stateContainer.evalWithAccessibleConsts(arg[1]);
                valueSize = stateContainer.evalWithAccessibleConsts(arg[2]);
            }

            default -> throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.directive.invalidArgument", args, "Fill"));
        }

        if (!section.allowDataInitialisation() && value != 0) {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.directive.illegalDataInitialization", "Fill", section.name()));
        }

        if (valueSize <= 0) throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.directive.invalidValueSize", valueSize));

        bytes = new byte[valueSize];

        switch (valueSize) {
            case 1 -> ByteBuffer.wrap(bytes).put((byte) (value & 0xFF));

            case 2 -> ByteBuffer.wrap(bytes).putShort((short) (value & 0xFFFF));

            case 3 -> ByteBuffer.wrap(bytes).put((byte) ((value >> 16) & 0xFF)).putShort((short) (value & 0xFFFF));

            default -> {
                ByteBuffer buffer = ByteBuffer.wrap(bytes);

                for (int i = 0 ; i < valueSize - 4 ; i++) {
                    buffer.put((byte) 0);
                }

                buffer.putInt(value);
            }
        }
    }

    @Override
    public void execute(StateContainer stateContainer) throws ASMException {
        for (int i = stateContainer.getCurrentMemoryPos().getPos(); i < stateContainer.getCurrentMemoryPos().getPos() + totalNum ; i++) {
            stateContainer.getMemory().putByte(stateContainer.getCurrentMemoryPos().getPos() + i, bytes[i % valueSize]);
        }
    }

    @Override
    public void offsetMemory(StateContainer stateContainer) throws ASMException {
        stateContainer.getCurrentMemoryPos().incrementPos(totalNum);
    }

    @Override
    public boolean isContextBuilder() {
        return false;
    }
}
