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
import fr.dwightstudio.jarmemu.base.asm.exception.StuckExecutionASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BXInstructionTest extends InstructionTest<Register, Object, Object, Object> {
    BXInstructionTest() {
        super(BXInstruction.class);
    }

    @Test
    public void simpleBxTest() throws ASMException {
        Register lr = stateContainer.getRegister(0);
        Register pc = stateContainer.getPC();
        lr.setData(24);
        pc.setData(48);
        legacyExecute(stateContainer, false, false, null, null, lr, null, null, null);
        assertEquals(24, pc.getData());
        assertFalse(stateContainer.getCPSR().getT());
        lr.setData(45);
        legacyExecute(stateContainer, false, false, null, null, lr, null, null, null);
        assertEquals(45, pc.getData());
        assertTrue(stateContainer.getCPSR().getT());
    }

    @Test
    public void BxExceptionTest() {
        Register pc = stateContainer.getPC();
        assertThrows(StuckExecutionASMException.class, () -> legacyExecute(stateContainer, false, false, null, null, pc, null, null, null));
    }
}