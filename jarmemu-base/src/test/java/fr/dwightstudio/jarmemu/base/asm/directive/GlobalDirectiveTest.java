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

package fr.dwightstudio.jarmemu.base.asm.directive;

import fr.dwightstudio.jarmemu.base.asm.Section;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlobalDirectiveTest extends DirectiveTest {
    public GlobalDirectiveTest() {
        super(GlobalDirective.class);
    }

    @BeforeEach
    void setUp() {
        super.setUp();

        container.getAccessibleLabels().put("EXEMPLE", 1);
        container.getAccessibleLabels().put("AHHHHHHHHHHHHHHH", 1);
    }

    @Test
    void normalTest() throws ASMException {
        execute(container, Section.DATA, "ExEMpLE");
        assertEquals("EXEMPLE", container.getGlobals().getFirst());
        assertEquals(0, container.getCurrentMemoryPos().getPos());
    }

    @Test
    void failTest() {
        assertDoesNotThrow(() -> execute(container, Section.DATA, "AHHHHHHHHHHHHHHH"));
        assertThrows(SyntaxASMException.class, () -> execute(container, Section.DATA, "Bonjour"));
        assertThrows(SyntaxASMException.class, () -> execute(container, Section.DATA, ""));
        assertThrows(SyntaxASMException.class, () -> execute(container, Section.DATA, "/."));
    }
}