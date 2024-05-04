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

import fr.dwightstudio.jarmemu.base.asm.argument.ImmediateOrRegisterArgument;
import fr.dwightstudio.jarmemu.base.asm.argument.ShiftArgument;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MVNInstructionTest extends InstructionTest<Register, ImmediateOrRegisterArgument.RegisterOrImmediate, ShiftArgument.ShiftFunction, Object> {
    MVNInstructionTest() {
        super(MVNInstruction.class);
    }

    @Test
    public void simpleMvnTest() throws ASMException {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        legacyExecute(stateContainer, false, false, null, null, r0, new ImmediateOrRegisterArgument.RegisterOrImmediate(Integer.MIN_VALUE), shift(), null);
        assertEquals(Integer.MAX_VALUE, r0.getData());
        legacyExecute(stateContainer, false, false, null, null, r1, new ImmediateOrRegisterArgument.RegisterOrImmediate(r0), shift(), null);
        assertEquals(Integer.MIN_VALUE, r1.getData());
        legacyExecute(stateContainer, false, false, null, null, r2, new ImmediateOrRegisterArgument.RegisterOrImmediate(0b10101010101010101010010001000001), shift(), null);
        assertEquals(~0b10101010101010101010010001000001, r2.getData());
    }

    @Test
    public void flagsTest() throws ASMException {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        legacyExecute(stateContainer, false, true, null, null, r0, new ImmediateOrRegisterArgument.RegisterOrImmediate(0), shift(), null);
        assertTrue(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        legacyExecute(stateContainer, false, true, null, null, r1, new ImmediateOrRegisterArgument.RegisterOrImmediate(-2), shift(), null);
        assertFalse(stateContainer.getCPSR().getN());
        assertFalse(stateContainer.getCPSR().getZ());
        r1.setData(-1);
        legacyExecute(stateContainer, false, true, null, null, r2, new ImmediateOrRegisterArgument.RegisterOrImmediate(r1), shift(), null);
        assertFalse(stateContainer.getCPSR().getN());
        assertTrue(stateContainer.getCPSR().getZ());
    }
}