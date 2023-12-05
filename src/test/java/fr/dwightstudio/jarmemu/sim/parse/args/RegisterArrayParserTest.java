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
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisterArrayParserTest extends JArmEmuTest {
    private StateContainer stateContainer;
    private static final RegisterArrayParser REGISTER_ARRAY = new RegisterArrayParser();

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    public void normalTest() {
        StringBuilder stringBuilder = new StringBuilder();
        Register[] registers = new Register[16];

        stringBuilder.append("{");

        for (int i = 0 ; i < 16 ; i++) {
            if (i != 0) stringBuilder.append(",");
            stringBuilder.append("R").append(i);
            registers[i] = stateContainer.getRegister(i);
        }

        stringBuilder.append("}");

        assertArrayEquals(registers, REGISTER_ARRAY.parse(stateContainer, stringBuilder.toString()));
    }

    @Test
    public void twoTypesTest() {
        String string = "{R0-R3, R4}";
        Register[] registers = new Register[5];

        System.arraycopy(stateContainer.getRegisters(), 0, registers, 0, 5);

        assertArrayEquals(registers, REGISTER_ARRAY.parse(stateContainer, string));
    }

    @Test
    public void duplicateTest() {
        StringBuilder stringBuilder = new StringBuilder();
        Register[] registers = new Register[16];

        stringBuilder.append("{");

        for (int i = 0 ; i < 64 ; i++) {
            if (i != 0) stringBuilder.append(",");
            stringBuilder.append("R").append(i % 16);
            if (i < 16) registers[i] = stateContainer.getRegister(i);
        }

        stringBuilder.append("}");

        REGISTER_ARRAY.logger.setLevel(Level.OFF);
        assertArrayEquals(registers, REGISTER_ARRAY.parse(stateContainer, stringBuilder.toString()));
        REGISTER_ARRAY.logger.setLevel(Level.INFO);
    }

    @Test
    public void failTest() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{");

        for (int i = 0 ; i < 32 ; i++) {
            if (i != 0) stringBuilder.append(",");
            stringBuilder.append("R").append(i);
        }

        stringBuilder.append("}");

        assertThrows(SyntaxASMException.class, () -> REGISTER_ARRAY.parse(stateContainer, stringBuilder.toString()));
    }
}