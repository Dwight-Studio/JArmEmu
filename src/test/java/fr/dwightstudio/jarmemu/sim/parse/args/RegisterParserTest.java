/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2023 Dwight Studio
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

package fr.dwightstudio.jarmemu.sim.parse.args;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RegisterParserTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private static final RegisterParser REGISTER = new RegisterParser();

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    public void allRegisterTest() {
        for (int i = 0 ; i < 16 ; i++) {
            assertEquals(stateContainer.registers[i], REGISTER.parse(stateContainer, "R" + i));
        }

        assertEquals(stateContainer.registers[13], REGISTER.parse(stateContainer, "SP"));
        assertEquals(stateContainer.registers[14], REGISTER.parse(stateContainer, "LR"));
        assertEquals(stateContainer.registers[15], REGISTER.parse(stateContainer, "PC"));
        assertEquals(stateContainer.cpsr, REGISTER.parse(stateContainer, "CPSR"));
        assertEquals(stateContainer.spsr, REGISTER.parse(stateContainer, "SPSR"));

        assertThrows(SyntaxASMException.class, () -> REGISTER.parse(stateContainer, "DAF"));
        assertThrows(SyntaxASMException.class, () -> REGISTER.parse(stateContainer, "R16"));
        assertThrows(SyntaxASMException.class, () -> REGISTER.parse(stateContainer, "R-1"));
        assertThrows(SyntaxASMException.class, () -> REGISTER.parse(stateContainer, "RL"));
        assertThrows(SyntaxASMException.class, () -> REGISTER.parse(stateContainer, "PCCPSR"));
        assertThrows(SyntaxASMException.class, () -> REGISTER.parse(stateContainer, "CPSR15"));
    }
}
