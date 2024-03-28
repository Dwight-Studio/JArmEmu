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
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImmParserTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private static final ImmParser IMM = new ImmParser();

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    public void normalTest() {
        assertEquals(2047, IMM.parse(stateContainer, "#2047"));
        assertEquals(256, IMM.parse(stateContainer, "#256"));
        assertEquals(-2048, IMM.parse(stateContainer, "#-2048"));
        assertEquals(0, IMM.parse(stateContainer, "#00000"));
    }

    @Test
    public void overflowTest() {
        assertThrows(SyntaxASMException.class, () -> IMM.parse(stateContainer, "#-2049"));
        assertThrows(SyntaxASMException.class, () -> IMM.parse(stateContainer, "#4096"));
        assertThrows(SyntaxASMException.class, () -> IMM.parse(stateContainer, "#2048"));
        assertThrows(SyntaxASMException.class, () -> IMM.parse(stateContainer, "#4096"));
    }
}
