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
import fr.dwightstudio.jarmemu.base.sim.entity.RegisterOrImmediate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImmediateOrRegisterArgumentTest extends ArgumentTest<RegisterOrImmediate> {

    public ImmediateOrRegisterArgumentTest() {
        super(ImmediateOrRegisterArgument.class);
    }

    @BeforeEach
    public void setUp() {
        super.setUp();
        
        for (int i = 0 ; i < 16 ; i++) {
            stateContainer.getRegister(i).setData(i);
        }

        stateContainer.getCPSR().setData(16);
        stateContainer.getSPSR().setData(17);
    }

    @Test
    public void valueTest() throws ASMException {
        assertEquals(new RegisterOrImmediate(48), parse( "#48"));
        assertEquals(new RegisterOrImmediate(1), parse( "#0B01"));
        assertEquals(new RegisterOrImmediate(8), parse( "#0010"));
        assertEquals(new RegisterOrImmediate(16), parse( "#0X010"));

        assertThrows(SyntaxASMException.class, () -> parse( "#R14"));
        assertThrows(SyntaxASMException.class, () -> parse( "#0XR14"));
        assertThrows(SyntaxASMException.class, () -> parse( "#LR"));
    }

    @Test
    public void registerTest() throws ASMException {
        for (int i = 0 ; i < 16 ; i++) {
            assertEquals(new RegisterOrImmediate(stateContainer.getRegister(i), false), parse( "R" + i));
        }

        assertEquals(new RegisterOrImmediate(stateContainer.getSP(), false), parse( "SP"));
        assertEquals(new RegisterOrImmediate(stateContainer.getLR(), false), parse( "LR"));
        assertEquals(new RegisterOrImmediate(stateContainer.getPC(), false), parse( "PC"));

        assertThrows(SyntaxASMException.class, () -> parse( "CPSR"));
        assertThrows(SyntaxASMException.class, () -> parse( "SPSR"));

        assertThrows(SyntaxASMException.class, () -> parse( "R16"));
        assertThrows(SyntaxASMException.class, () -> parse( "48"));
        assertThrows(SyntaxASMException.class, () -> parse( "1R"));
        assertThrows(SyntaxASMException.class, () -> parse( "4LR"));
    }
}