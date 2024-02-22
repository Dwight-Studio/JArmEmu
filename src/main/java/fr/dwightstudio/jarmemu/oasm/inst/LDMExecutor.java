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

package fr.dwightstudio.jarmemu.oasm.inst;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.exception.MemoryAccessMisalignedASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;

public class LDMExecutor implements InstructionExecutor<RegisterWithUpdateParser.UpdatableRegister, Register[], Object, Object> {
    @Override
    public void execute(StateContainer stateContainer, boolean forceExecution, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, RegisterWithUpdateParser.UpdatableRegister arg1, Register[] arg2, Object arg3, Object arg4) {
        int length = arg2.length;
        int value = 0;
        int address = arg1.getData();

        if (!forceExecution) {
            int dataLength = 4;
            if (address % dataLength != 0) throw new MemoryAccessMisalignedASMException();
        }

        switch (updateMode) {
            case FD, DB -> {
                for (int i = 0; i < length; i++) {
                    arg2[i].setData(stateContainer.getMemory().getWord(arg1.getData() + 4 * i));
                }
                value = 4 * length;
            }
            case FA, IB -> {
                for (int i = 0; i < length; i++) {
                    arg2[i].setData(stateContainer.getMemory().getWord(arg1.getData() - 4 * i));
                }
                value = - 4 * length;
            }
            case ED, DA -> {
                for (int i = 0; i < length; i++) {
                    arg2[i].setData(stateContainer.getMemory().getWord(arg1.getData() + 4 * (i + 1)));
                }
                value = 4 * length;
            }
            case EA, IA -> {
                for (int i = 0; i < length; i++) {
                    arg2[i].setData(stateContainer.getMemory().getWord(arg1.getData() - 4 * (i + 1)));
                }
                value = - 4 * length;
            }
        }
        arg1.update(value);
    }
}
