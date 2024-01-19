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

package fr.dwightstudio.jarmemu.sim.parse.args;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterWithUpdateParserTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private static final RegisterWithUpdateParser REGISTER_WITH_UPDATE = new RegisterWithUpdateParser();
    private static final RegisterArrayParser REGISTER_ARRAY = new RegisterArrayParser();

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    public void normalTest() {
        stateContainer.getRegister(0).setData(404);

        RegisterWithUpdateParser.UpdatableRegister reg = REGISTER_WITH_UPDATE.parse(stateContainer, "R0");
        assertEquals(stateContainer.getRegister(0).getData(), reg.getData());

        REGISTER_ARRAY.parse(stateContainer, "{R0,R1,R2}");
        reg.update(0);

        assertEquals(404, reg.getData());
    }

    @Test
    public void updateTest() {
        stateContainer.getRegister(0).setData(404);

        RegisterWithUpdateParser.UpdatableRegister reg = REGISTER_WITH_UPDATE.parse(stateContainer, "R0!");
        assertEquals(stateContainer.getRegister(0).getData(), reg.getData());

        REGISTER_ARRAY.parse(stateContainer, "{R0,R1,R2}");
        reg.update(-12);

        assertEquals(392, reg.getData());
    }

    @Test
    public void failTest() {
        assertThrows(SyntaxASMException.class, () -> REGISTER_WITH_UPDATE.parse(stateContainer, "!R1"));
        assertThrows(SyntaxASMException.class, () -> REGISTER_WITH_UPDATE.parse(stateContainer, "R16!"));
        assertThrows(SyntaxASMException.class, () -> REGISTER_WITH_UPDATE.parse(stateContainer, "R!"));
        assertThrows(SyntaxASMException.class, () -> REGISTER_WITH_UPDATE.parse(stateContainer, "R17"));
    }
}