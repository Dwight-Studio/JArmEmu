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
import fr.dwightstudio.jarmemu.base.sim.entity.RegisterOrImmediate;
import fr.dwightstudio.jarmemu.base.sim.entity.ShiftFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MOVInstructionTest extends InstructionTest<Register, RegisterOrImmediate, ShiftFunction, Object> {
    MOVInstructionTest() {
        super(MOVInstruction.class);
    }

    @Test
    public void simpleMovTest() throws ASMException {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        legacyExecute(stateContainer, false, false, null, null, r0, new RegisterOrImmediate(5), shift(), null);
        legacyExecute(stateContainer, false, false, null, null, r1, new RegisterOrImmediate(r0), shift(), null);
        r1.setData(r1.getData()+1);
        legacyExecute(stateContainer, false, false, null, null, r2, new RegisterOrImmediate(r1), shift(), null);
        assertEquals(5, r0.getData());
        assertEquals(6, r1.getData());
        assertEquals(6, r2.getData());
    }

    @Test
    public void flagsTest() throws ASMException {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        legacyExecute(stateContainer, false, true, null, null, r0, new RegisterOrImmediate(0), shift(), null);
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
        legacyExecute(stateContainer, false, true, null, null, r1, new RegisterOrImmediate(-2), shift(), null);
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        r1.setData(4);
        legacyExecute(stateContainer, false, true, null, null, r2, new RegisterOrImmediate(r1), shift(), null);
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
    }
}