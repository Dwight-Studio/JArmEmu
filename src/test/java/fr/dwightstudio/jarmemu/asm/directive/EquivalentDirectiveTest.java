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

package fr.dwightstudio.jarmemu.asm.directive;

import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.entity.FilePos;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EquivalentDirectiveTest extends DirectiveTest {
    public EquivalentDirectiveTest() {
        super(EquivalentDirective.class);
    }

    @Test
    void normalTest() throws ASMException {
        Random random = new Random();
        FilePos posZ = FilePos.ZERO.clone();

        container.getCurrentFilePos().setPos(0);

        for (int i = 0 ; i < 32 ; i++) {
            int r = random.nextInt();
            FilePos pos = new FilePos(0, r);
            String s = RandomStringUtils.randomAlphabetic(i+1).toUpperCase();
            execute(container, Section.DATA, s + ", " + r);
            assertEquals(r, container.getAccessibleConsts().get(s));
        }

        assertEquals(0, container.getCurrentFilePos().getPos());
    }

    @Test
    void failTest() {
        assertThrows(SyntaxASMException.class, () -> execute(container, Section.DATA, "HEY,"));
        assertThrows(SyntaxASMException.class, () -> execute(container, Section.DATA, "HEY, p"));
        assertThrows(SyntaxASMException.class, () -> execute(container, Section.DATA, "/, 3"));
        assertThrows(SyntaxASMException.class, () -> execute(container, Section.DATA, ", 0"));
    }
}