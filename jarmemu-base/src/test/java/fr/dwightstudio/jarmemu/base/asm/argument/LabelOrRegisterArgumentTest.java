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

class LabelOrRegisterArgumentTest extends ArgumentTest<Integer> {

    public LabelOrRegisterArgumentTest() {
        super(LabelOrRegisterArgument.class);
    }

    @Test
    public void parseTest() throws ASMException {
        stateContainer.getAccessibleLabels().put("COUCOU", 23);
        stateContainer.getAccessibleLabels().put("R1", 43);
        stateContainer.getRegister(12).setData(13);
        stateContainer.getRegister(1).setData(14);

        assertEquals(23, parse("COUCOU"));
        assertEquals(13, parse("R12"));
        assertEquals(14, parse("R1"));
        assertThrows(SyntaxASMException.class, () -> parse("PASCOUCOU"));
        assertThrows(SyntaxASMException.class, () -> parse("R99"));
        assertThrows(SyntaxASMException.class, () -> parse("COUCOU:"));
    }
}