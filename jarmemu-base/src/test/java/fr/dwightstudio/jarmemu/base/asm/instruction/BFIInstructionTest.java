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
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BFIInstructionTest extends InstructionTest<Register, Register, Integer, Integer> {

    protected BFIInstructionTest() {
        super(BFIInstruction.class);
    }

    @Test
    public void simpleBfiTest() throws ASMException {
        Register r2 = stateContainer.getRegister(2);
        Register r3 = stateContainer.getRegister(3);
        r2.setData(13);
        r3.setData(1);
        legacyExecute(stateContainer, false, false, null, null, r2, r3, 0, 1);
        assertEquals(13, r2.getData());
        legacyExecute(stateContainer, false, false, null, null, r2, r3, 1, 1);
        assertEquals(15, r2.getData());
        r2.setData(13);
        legacyExecute(stateContainer, false, false, null, null, r2, r3, 0, 3);
        assertEquals(9, r2.getData());
        r2.setData(13);
        r3.setData(2);
        legacyExecute(stateContainer, false, false, null, null, r2, r3, 1, 1);
        assertEquals(13, r2.getData());
        legacyExecute(stateContainer, false, false, null, null, r2, r3, 0, 1);
        assertEquals(12, r2.getData());
        r2.setData(13);
        legacyExecute(stateContainer, false, false, null, null, r2, r3, 0, 2);
        assertEquals(14, r2.getData());
        r2.setData(13);
        r3.setData(3);
        legacyExecute(stateContainer, false, false, null, null, r2, r3, 0, 2);
        assertEquals(15, r2.getData());
    }

    @Test
    public void edgeCaseTest() throws ASMException {
        Register r2 = stateContainer.getRegister(2);
        Register r3 = stateContainer.getRegister(3);
        r2.setData(654651635);
        r3.setData(0);
        legacyExecute(stateContainer, false, false, null, null, r2, r3, 0, 32);
        assertEquals(0, r2.getData());
    }
}