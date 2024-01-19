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

public class RotatedImmParserTest extends JArmEmuTest {
    private StateContainer stateContainer;
    private static final RotatedImmParser VALUE12 = new RotatedImmParser();

    @BeforeEach
    void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    void decTest() {
        assertEquals(87, VALUE12.parse(stateContainer,"#87"));
        assertEquals(65280, VALUE12.parse(stateContainer,"#65280"));
        assertEquals(1020, VALUE12.parse(stateContainer,"#1020"));
        assertEquals(-2147483648, VALUE12.parse(stateContainer,"#-2147483648"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer, "#258"));
    }

    @Test
    void hexTest() {
        assertEquals(-16777216, VALUE12.parse(stateContainer,"#0XFF000000"));
        assertEquals(1044480, VALUE12.parse(stateContainer,"#0X000FF000"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer, "#0X01020000"));
    }

    @Test
    void octTest() {
        assertEquals(63, VALUE12.parse(stateContainer,"#0077"));
        assertEquals(183500800, VALUE12.parse(stateContainer,"#001274000000"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer, "#002010000000"));
    }

    @Test
    void binTest() {
        assertEquals(87, VALUE12.parse(stateContainer,"#0B1010111"));
        assertEquals(-2147483648, VALUE12.parse(stateContainer,"#0B10000000000000000000000000000000"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer, "#0B01111111100000000000000000000000"));

    }

    @Test
    void failTest() {
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer,"#udhad"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer,"#0B0xff"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer,"#7440b"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer,"#"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer,"=#48"));
        assertThrows(SyntaxASMException.class, () -> VALUE12.parse(stateContainer,"-4"));
    }
}
