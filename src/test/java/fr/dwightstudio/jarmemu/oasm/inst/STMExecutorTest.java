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

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.RegisterWithUpdateParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static fr.dwightstudio.jarmemu.asm.UpdateMode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class STMExecutorTest extends JArmEmuTest {
    private StateContainer stateContainer;
    private STMExecutor stmExecutor;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
        stmExecutor = new STMExecutor();
    }

    @Test
    public void simpleStmTest() {
        Register sp = stateContainer.getRegister(13);
        sp.setData(1000);
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        r0.setData(54);
        r1.setData(12);
        r2.setData(65);
        stmExecutor.execute(stateContainer, false, false, null, FD, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, stateContainer.getMemory().getWord(996));
        assertEquals(12, stateContainer.getMemory().getWord(992));
        assertEquals(54, stateContainer.getMemory().getWord(988));
        assertEquals(988, sp.getData());
        sp.setData(10000);
        r0.setData(54);
        r1.setData(12);
        r2.setData(65);
        stmExecutor.execute(stateContainer, false, false, null, DB, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, stateContainer.getMemory().getWord(9996));
        assertEquals(12, stateContainer.getMemory().getWord(9992));
        assertEquals(54, stateContainer.getMemory().getWord(9988));
        assertEquals(9988, sp.getData());

        sp.setData(2000);
        stmExecutor.execute(stateContainer, false, false, null, FA, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, stateContainer.getMemory().getWord(2004));
        assertEquals(12, stateContainer.getMemory().getWord(2008));
        assertEquals(54, stateContainer.getMemory().getWord(2012));
        assertEquals(2012, sp.getData());
        sp.setData(20000);
        stmExecutor.execute(stateContainer, false, false, null, IB, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, stateContainer.getMemory().getWord(20004));
        assertEquals(12, stateContainer.getMemory().getWord(20008));
        assertEquals(54, stateContainer.getMemory().getWord(20012));
        assertEquals(20012, sp.getData());

        sp.setData(3000);
        stmExecutor.execute(stateContainer, false, false, null, ED, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, stateContainer.getMemory().getWord(3000));
        assertEquals(12, stateContainer.getMemory().getWord(2996));
        assertEquals(54, stateContainer.getMemory().getWord(2992));
        assertEquals(2988, sp.getData());
        sp.setData(30000);
        stmExecutor.execute(stateContainer, false, false, null, DA, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, stateContainer.getMemory().getWord(30000));
        assertEquals(12, stateContainer.getMemory().getWord(29996));
        assertEquals(54, stateContainer.getMemory().getWord(29992));
        assertEquals(29988, sp.getData());

        sp.setData(4000);
        stmExecutor.execute(stateContainer, false, false, null, EA, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, stateContainer.getMemory().getWord(4000));
        assertEquals(12, stateContainer.getMemory().getWord(4004));
        assertEquals(54, stateContainer.getMemory().getWord(4008));
        assertEquals(4012, sp.getData());
        sp.setData(40000);
        stmExecutor.execute(stateContainer, false, false, null, IA, new RegisterWithUpdateParser.UpdatableRegister(sp, true, stateContainer), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, stateContainer.getMemory().getWord(40000));
        assertEquals(12, stateContainer.getMemory().getWord(40004));
        assertEquals(54, stateContainer.getMemory().getWord(40008));
        assertEquals(40012, sp.getData());
    }
}
