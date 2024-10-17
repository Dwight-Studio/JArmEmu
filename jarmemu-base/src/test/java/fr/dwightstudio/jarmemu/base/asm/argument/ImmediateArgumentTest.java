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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ImmediateArgumentTest extends ArgumentTest<Integer> {
    public ImmediateArgumentTest() {
        super(ImmediateArgument.class);
    }

    @Test
    public void normalTest() throws ASMException {
        Assertions.assertEquals(2047, parse("#2047"));
        Assertions.assertEquals(4095, parse("#4095"));
        Assertions.assertEquals(256, parse("#256"));
        Assertions.assertEquals(-2048, parse("#-2048"));
        Assertions.assertEquals(0, parse("#00000"));
    }

    @Test
    public void overflowTest() {
        Assertions.assertThrows(SyntaxASMException.class, () -> parse("#-2049"));
        Assertions.assertThrows(SyntaxASMException.class, () -> parse("#4096"));
        Assertions.assertThrows(SyntaxASMException.class, () -> parse("#4096"));
    }
}
