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
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PUSHInstructionTest extends InstructionTest<Register[], Object, Object, Object> {
    PUSHInstructionTest() {
        super(PUSHInstruction.class);
    }

    @Test
    public void simplePushTest() throws ASMException {
        PUSHInstruction pushInstruction = new PUSHInstruction(new Modifier(Condition.AL, false, null, null), "{r0-r2}", null, null, null);
        Register sp = stateContainer.getRegister(13);
        sp.setData(1000);
        Register r0 = stateContainer.getRegister(0);
        Register r1 = stateContainer.getRegister(1);
        Register r2 = stateContainer.getRegister(2);
        r0.setData(54);
        r1.setData(12);
        r2.setData(65);
        pushInstruction.contextualize(stateContainer);
        pushInstruction.execute(stateContainer, false);
        assertEquals(65, stateContainer.getMemory().getWord(996));
        assertEquals(12, stateContainer.getMemory().getWord(992));
        assertEquals(54, stateContainer.getMemory().getWord(988));
        assertEquals(988, sp.getData());
        sp.setData(10000);
        r0.setData(54);
        r1.setData(12);
        r2.setData(65);
        pushInstruction.contextualize(stateContainer);
        pushInstruction.execute(stateContainer, false);
        assertEquals(65, stateContainer.getMemory().getWord(9996));
        assertEquals(12, stateContainer.getMemory().getWord(9992));
        assertEquals(54, stateContainer.getMemory().getWord(9988));
        assertEquals(9988, sp.getData());
    }
}
