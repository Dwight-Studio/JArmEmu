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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RotatedImmediateArgumentTest extends ArgumentTest<Integer> {
    public RotatedImmediateArgumentTest() {
        super(RotatedImmediateArgument.class);
    }

    @Test
    void decTest() throws ASMException {
        assertEquals(87, parse("#87"));
        assertEquals(65280, parse("#65280"));
        assertEquals(1020, parse("#1020"));
        assertEquals(-2147483648, parse("#-2147483648"));
        assertThrows(SyntaxASMException.class, () -> parse( "#258"));
    }

    @Test
    void hexTest() throws ASMException {
        assertEquals(-16777216, parse("#0XFF000000"));
        assertEquals(1044480, parse("#0X000FF000"));
        assertThrows(SyntaxASMException.class, () -> parse( "#0X01020000"));
    }

    @Test
    void octTest() throws ASMException {
        assertEquals(63, parse("#0077"));
        assertEquals(183500800, parse("#001274000000"));
        assertThrows(SyntaxASMException.class, () -> parse( "#002010000000"));
    }

    @Test
    void binTest() throws ASMException {
        assertEquals(87, parse("#0B1010111"));
        assertEquals(-2147483648, parse("#0B10000000000000000000000000000000"));
        assertThrows(SyntaxASMException.class, () -> parse( "#0B01111111100000000000000000000000"));

    }

    @Test
    void failTest() {
        assertThrows(SyntaxASMException.class, () -> parse("#udhad"));
        assertThrows(SyntaxASMException.class, () -> parse("#0B0xff"));
        assertThrows(SyntaxASMException.class, () -> parse("#7440b"));
        assertThrows(SyntaxASMException.class, () -> parse("#"));
        assertThrows(IllegalStateException.class, () -> parse("=#48"));
        assertThrows(SyntaxASMException.class, () -> parse("-4"));
    }
}