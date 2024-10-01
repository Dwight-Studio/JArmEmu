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

package fr.dwightstudio.jarmemu.base.asm.argument;

import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterArgumentTest extends ArgumentTest<Register> {
    public RegisterArgumentTest() {
        super(RegisterArgument.class);
    }

    @Test
    public void allRegisterTest() throws ASMException {
        for (int i = 0 ; i < 16 ; i++) {
            assertEquals(stateContainer.getRegister(i), parse("R" + i));
        }

        assertEquals(stateContainer.getRegister(13), parse("SP"));
        assertEquals(stateContainer.getLR(), parse("LR"));
        assertEquals(stateContainer.getPC(), parse("PC"));

        assertThrows(SyntaxASMException.class, () -> parse("CPSR"));
        assertThrows(SyntaxASMException.class, () -> parse("SPSR"));

        assertThrows(SyntaxASMException.class, () -> parse("DAF"));
        assertThrows(SyntaxASMException.class, () -> parse("R16"));
        assertThrows(SyntaxASMException.class, () -> parse("R-1"));
        assertThrows(SyntaxASMException.class, () -> parse("RL"));
        assertThrows(SyntaxASMException.class, () -> parse("PCCPSR"));
        assertThrows(SyntaxASMException.class, () -> parse("CPSR15"));
    }
}