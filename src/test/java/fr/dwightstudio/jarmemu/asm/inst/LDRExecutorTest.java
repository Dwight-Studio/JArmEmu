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

package fr.dwightstudio.jarmemu.asm.inst;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.AddressParser;
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.dwightstudio.jarmemu.asm.DataMode.BYTE;
import static fr.dwightstudio.jarmemu.asm.DataMode.HALF_WORD;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LDRExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private LDRExecutor ldrExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        ldrExecutor = new LDRExecutor();
    }

    @Test
    public void simpleLdrTest() {
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        stateContainer.memory.putWord(100, 54);
        stateContainer.memory.putHalf(104, (short) 54);
        stateContainer.memory.putByte(106, (byte) 54);
        ldrExecutor.execute(stateContainer, false, false, null, null, r0, new AddressParser.UpdatableInteger(100, stateContainer, false, false, null), 0, ArgumentParsers.SHIFT.none());
        assertEquals(54, r0.getData());
        ldrExecutor.execute(stateContainer, false, false, HALF_WORD, null, r1, new AddressParser.UpdatableInteger(104, stateContainer, false, false, null), 0, ArgumentParsers.SHIFT.none());
        assertEquals(54, r1.getData());
        ldrExecutor.execute(stateContainer, false, false, BYTE, null, r2, new AddressParser.UpdatableInteger(106, stateContainer, false, false, null), 0, ArgumentParsers.SHIFT.none());
        assertEquals(54, r2.getData());
    }

}
