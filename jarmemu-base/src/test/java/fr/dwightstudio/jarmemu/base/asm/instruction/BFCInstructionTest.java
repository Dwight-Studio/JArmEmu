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
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BFCInstructionTest extends InstructionTest<Register, Integer, Integer, Object> {
    protected BFCInstructionTest() {
        super(BFCInstruction.class);
    }

    @Test
    public void simpleBfcTest() throws ASMException {
        Random rand = new Random();
        Register r2 = stateContainer.getRegister(2);
        r2.setData(0b010101010101010);
        execute(stateContainer, false, false, null, null, r2, 3, 4, null);
        assertEquals(0b010101010000010, r2.getData());
        for (int i = 0; i < 1000; i++) {
            int value = rand.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
            r2.setData(value);
            execute(stateContainer, false, false, null, null, r2, 28, 4, null);
            assertEquals(value & 0xFFFFFFF, r2.getData());
            r2.setData(value);
            execute(stateContainer, false, false, null, null, r2, 0, 4, null);
            assertEquals(value & 0xFFFFFFF0, r2.getData());
        }
    }

    @Test
    public void edgeCaseTest() throws ASMException {
        Register r2 = stateContainer.getRegister(2);
        r2.setData(15);
        execute(stateContainer, false, false, null, null, r2, 0, 32, null);
        assertEquals(0, r2.getData());
        r2.setData(15);
        execute(stateContainer, false, false, null, null, r2, 1, 31, null);
        assertEquals(1, r2.getData());
    }

    @Test
    public void failAdrTest() {
        Register r2 = stateContainer.getRegister(2);
        r2.setData(0b010101010101010);
        assertThrows(SyntaxASMException.class, () -> execute(stateContainer, false, true, null, null, r2, -2, 4, null));
        assertThrows(SyntaxASMException.class, () -> execute(stateContainer, false, true, null, null, r2, 3, 32, null));
    }
}