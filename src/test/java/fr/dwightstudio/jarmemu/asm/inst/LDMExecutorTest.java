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
import fr.dwightstudio.jarmemu.sim.parse.args.RegisterWithUpdateParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.dwightstudio.jarmemu.asm.UpdateMode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LDMExecutorTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private LDMExecutor ldmExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        ldmExecutor = new LDMExecutor();
    }

    @Test
    public void simpleLdmTest() {
        Register sp = stateContainer.registers[13];
        sp.setData(988);
        Register r0 = stateContainer.registers[0];
        Register r1 = stateContainer.registers[1];
        Register r2 = stateContainer.registers[2];
        stateContainer.memory.putWord(988, 54);
        stateContainer.memory.putWord(992, 12);
        stateContainer.memory.putWord(996, 65);
        ldmExecutor.execute(stateContainer, false, false, null, FD, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(1000, sp.getData());
        sp.setData(10988);
        stateContainer.memory.putWord(10988, 54);
        stateContainer.memory.putWord(10992, 12);
        stateContainer.memory.putWord(10996, 65);
        ldmExecutor.execute(stateContainer, false, false, null, DB, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(11000, sp.getData());

        sp.setData(2012);
        stateContainer.memory.putWord(2012, 54);
        stateContainer.memory.putWord(2008, 12);
        stateContainer.memory.putWord(2004, 65);
        ldmExecutor.execute(stateContainer, false, false, null, FA, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(2000, sp.getData());
        sp.setData(12012);
        stateContainer.memory.putWord(12012, 54);
        stateContainer.memory.putWord(12008, 12);
        stateContainer.memory.putWord(12004, 65);
        ldmExecutor.execute(stateContainer, false, false, null, IB, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(12000, sp.getData());

        sp.setData(2988);
        stateContainer.memory.putWord(2992, 54);
        stateContainer.memory.putWord(2996, 12);
        stateContainer.memory.putWord(3000, 65);
        ldmExecutor.execute(stateContainer, false, false, null, ED, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(3000, sp.getData());
        sp.setData(12988);
        stateContainer.memory.putWord(12992, 54);
        stateContainer.memory.putWord(12996, 12);
        stateContainer.memory.putWord(13000, 65);
        ldmExecutor.execute(stateContainer, false, false, null, DA, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(13000, sp.getData());

        sp.setData(4012);
        stateContainer.memory.putWord(4008, 54);
        stateContainer.memory.putWord(4004, 12);
        stateContainer.memory.putWord(4000, 65);
        ldmExecutor.execute(stateContainer, false, false, null, EA, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(4000, sp.getData());
        sp.setData(14012);
        stateContainer.memory.putWord(14008, 54);
        stateContainer.memory.putWord(14004, 12);
        stateContainer.memory.putWord(14000, 65);
        ldmExecutor.execute(stateContainer, false, false, null, IA, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(14000, sp.getData());
    }
}
