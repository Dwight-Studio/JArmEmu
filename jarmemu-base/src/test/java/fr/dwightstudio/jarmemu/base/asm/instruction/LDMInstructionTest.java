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

package fr.dwightstudio.jarmemu.base.asm.instruction;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.UpdateMode;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.UpdatableRegister;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LDMInstructionTest extends InstructionTest<UpdatableRegister, Register[], Object, Object> {
    LDMInstructionTest() {
        super(LDMInstruction.class);
    }

    @Test
    public void simpleLdmTest() throws ASMException {
        Register sp = stateContainer.getRegister(13);
        sp.setData(988);
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        stateContainer.getMemory().putWord(988, 54);
        stateContainer.getMemory().putWord(992, 12);
        stateContainer.getMemory().putWord(996, 65);
        legacyExecute(stateContainer, false, false, null, UpdateMode.FD, new UpdatableRegister(sp, true), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(1000, sp.getData());
        sp.setData(10988);
        stateContainer.getMemory().putWord(10988, 54);
        stateContainer.getMemory().putWord(10992, 12);
        stateContainer.getMemory().putWord(10996, 65);
        legacyExecute(stateContainer, false, false, null, UpdateMode.IA, new UpdatableRegister(sp, true), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(11000, sp.getData());

        sp.setData(2012);
        stateContainer.getMemory().putWord(2012, 54);
        stateContainer.getMemory().putWord(2008, 12);
        stateContainer.getMemory().putWord(2004, 65);
        legacyExecute(stateContainer, false, false, null, UpdateMode.FA, new UpdatableRegister(sp, true), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(2000, sp.getData());
        sp.setData(12012);
        stateContainer.getMemory().putWord(12012, 54);
        stateContainer.getMemory().putWord(12008, 12);
        stateContainer.getMemory().putWord(12004, 65);
        legacyExecute(stateContainer, false, false, null, UpdateMode.DA, new UpdatableRegister(sp, true), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(12000, sp.getData());

        sp.setData(2988);
        stateContainer.getMemory().putWord(2992, 54);
        stateContainer.getMemory().putWord(2996, 12);
        stateContainer.getMemory().putWord(3000, 65);
        legacyExecute(stateContainer, false, false, null, UpdateMode.ED, new UpdatableRegister(sp, true), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(3000, sp.getData());
        sp.setData(12988);
        stateContainer.getMemory().putWord(12992, 54);
        stateContainer.getMemory().putWord(12996, 12);
        stateContainer.getMemory().putWord(13000, 65);
        legacyExecute(stateContainer, false, false, null, UpdateMode.IB, new UpdatableRegister(sp, true), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(13000, sp.getData());

        sp.setData(4012);
        stateContainer.getMemory().putWord(4008, 54);
        stateContainer.getMemory().putWord(4004, 12);
        stateContainer.getMemory().putWord(4000, 65);
        legacyExecute(stateContainer, false, false, null, UpdateMode.EA, new UpdatableRegister(sp, true), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(4000, sp.getData());
        sp.setData(14012);
        stateContainer.getMemory().putWord(14008, 54);
        stateContainer.getMemory().putWord(14004, 12);
        stateContainer.getMemory().putWord(14000, 65);
        legacyExecute(stateContainer, false, false, null, UpdateMode.DB, new UpdatableRegister(sp, true), new Register[]{r0, r1, r2}, null, null);
        assertEquals(65, r2.getData());
        assertEquals(12, r1.getData());
        assertEquals(54, r0.getData());
        assertEquals(14000, sp.getData());
    }
}