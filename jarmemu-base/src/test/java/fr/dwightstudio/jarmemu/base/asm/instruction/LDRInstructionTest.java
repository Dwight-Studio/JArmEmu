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

import fr.dwightstudio.jarmemu.base.asm.argument.AddressArgument;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.DataMode;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.RegisterOrImmediate;
import fr.dwightstudio.jarmemu.base.sim.entity.ShiftFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LDRInstructionTest extends InstructionTest<Register, AddressArgument.UpdatableInteger, RegisterOrImmediate, ShiftFunction> {
    LDRInstructionTest() {
        super(LDRInstruction.class);
    }

    @Test
    public void simpleLdrTest() throws ASMException {
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        stateContainer.getMemory().putWord(100, 54);
        stateContainer.getMemory().putHalf(104, (short) 54);
        stateContainer.getMemory().putByte(106, (byte) 54);
        legacyExecute(stateContainer, false, false, null, null, r0, new AddressArgument.UpdatableInteger(100, false, false, null), new RegisterOrImmediate(0), shift());
        assertEquals(54, r0.getData());
        legacyExecute(stateContainer, false, false, DataMode.H, null, r1, new AddressArgument.UpdatableInteger(104, false, false, null), new RegisterOrImmediate(0), shift());
        assertEquals(54, r1.getData());
        legacyExecute(stateContainer, false, false, DataMode.B, null, r2, new AddressArgument.UpdatableInteger(106, false, false, null), new RegisterOrImmediate(0), shift());
        assertEquals(54, r2.getData());
        assertThrows(SyntaxASMException.class, () -> legacyExecute(stateContainer, false, false, null, null, r2, new AddressArgument.UpdatableInteger(r2.getData(), false, false, null), new RegisterOrImmediate(5), shift()));
    }
}