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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BLInstructionTest extends InstructionTest<Integer, Object, Object, Object> {
    BLInstructionTest() {
        super(BLInstruction.class);
    }

    @Test
    public void simpleBlTest() throws ASMException {
        Register lr = stateContainer.getLR();
        Register pc = stateContainer.getPC();
        pc.setData(24);
        stateContainer.getAccessibleLabels().put("COUCOU", 20);
        Integer value =  label(stateContainer, "COUCOU");
        execute(stateContainer, false, false, null, null, value, null, null, null);
        assertEquals(20, pc.getData());
        assertEquals(28, lr.getData());
    }

    @Test
    public void BlExceptionTest() {
        Register pc = stateContainer.getPC();
        pc.setData(24);
        stateContainer.getAccessibleLabels().put("COUCOU", 24);
        Integer value =  label(stateContainer, "COUCOU");
        assertThrows(StuckExecutionASMException.class, () -> execute(stateContainer, false, false, null, null, value, null, null, null));
    }
}