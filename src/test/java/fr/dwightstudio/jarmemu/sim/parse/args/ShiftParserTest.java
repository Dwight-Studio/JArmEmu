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
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShiftParserTest extends JArmEmuTest {

    private StateContainer stateContainer;
    private static final ShiftParser SHIFT = new ShiftParser();

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();
    }

    @Test
    public void LSLTest() {
        int data = 0b00000000000000000000000000000001;
        stateContainer.getRegister(0).setData(0b0000000000000000000000000000010);
        stateContainer.getLR().setData(0b00000000000000000000000000000100);
        ShiftParser.ShiftFunction f;

        f = SHIFT.parse(stateContainer, "LSL#5");
        assertEquals(0b00000000000000000000000000100000, f.apply(data));

        f = SHIFT.parse(stateContainer, "LSLR0");
        assertEquals(0b00000000000000000000000000000100, f.apply(data));

        f = SHIFT.parse(stateContainer, "LSLLR");
        assertEquals(0b00000000000000000000000000010000, f.apply(data));
    }

    @Test
    public void LSRTest() {
        int data = 0b10000000000000000000000000000000;
        stateContainer.getRegister(0).setData(0b0000000000000000000000000000010);
        stateContainer.getLR().setData(0b00000000000000000000000000000100);
        ShiftParser.ShiftFunction f;

        f = SHIFT.parse(stateContainer, "LSR#5");
        assertEquals(0b00000100000000000000000000000000, f.apply(data));

        f = SHIFT.parse(stateContainer, "LSRR0");
        assertEquals(0b00100000000000000000000000000000, f.apply(data));

        f = SHIFT.parse(stateContainer, "LSRLR");
        assertEquals(0b00001000000000000000000000000000, f.apply(data));
    }

    @Test
    public void ASRTest() {
        int data = 0b10000000000000000000000000000000;
        stateContainer.getRegister(0).setData(0b0000000000000000000000000000010);
        stateContainer.getLR().setData(0b00000000000000000000000000000100);
        ShiftParser.ShiftFunction f;

        f = SHIFT.parse(stateContainer, "ASR#5");
        assertEquals(0b11111100000000000000000000000000, f.apply(data));

        f = SHIFT.parse(stateContainer, "ASRR0");
        assertEquals(0b11100000000000000000000000000000, f.apply(data));

        f = SHIFT.parse(stateContainer, "ASRLR");
        assertEquals(0b11111000000000000000000000000000, f.apply(data));

        data = 0b01000000000000000000000000000000;

        f = SHIFT.parse(stateContainer, "ASR#5");
        assertEquals(0b00000010000000000000000000000000, f.apply(data));

        f = SHIFT.parse(stateContainer, "ASRR0");
        assertEquals(0b00010000000000000000000000000000, f.apply(data));

        f = SHIFT.parse(stateContainer, "ASRLR");
        assertEquals(0b00000100000000000000000000000000, f.apply(data));
    }

    @Test
    public void RORTest() {
        int data = 0b00000000000000000000000000001000;
        stateContainer.getRegister(0).setData(0b0000000000000000000000000000010);
        stateContainer.getLR().setData(0b00000000000000000000000000000100);
        ShiftParser.ShiftFunction f;

        f = SHIFT.parse(stateContainer, "ROR#5");
        assertEquals(0b01000000000000000000000000000000, f.apply(data));

        f = SHIFT.parse(stateContainer, "RORR0");
        assertEquals(0b00000000000000000000000000000010, f.apply(data));

        f = SHIFT.parse(stateContainer, "RORLR");
        assertEquals(0b10000000000000000000000000000000, f.apply(data));
    }

    @Test
    public void RRXTest() {
        int data;
        ShiftParser.ShiftFunction f = SHIFT.parse(stateContainer, "RRX");

        data = 0b00000000000000000000000000001000;
        stateContainer.getCPSR().setC(false);
        assertEquals(0b00000000000000000000000000000100, f.apply(data));
        assertFalse(stateContainer.getCPSR().getC());

        data = 0b00000000000000000000000000010000;
        stateContainer.getCPSR().setC(true);
        f = SHIFT.parse(stateContainer, "RRX");
        assertEquals(0b10000000000000000000000000001000, f.apply(data));
        assertFalse(stateContainer.getCPSR().getC());

        data = 0b00000000000000000000000000001001;
        stateContainer.getCPSR().setC(false);
        f = SHIFT.parse(stateContainer, "RRX");
        assertEquals(0b00000000000000000000000000000100, f.apply(data));
        assertTrue(stateContainer.getCPSR().getC());

        data = 0b00000000000000000000000000010001;
        stateContainer.getCPSR().setC(true);
        f = SHIFT.parse(stateContainer, "RRX");
        assertEquals(0b10000000000000000000000000001000, f.apply(data));
        assertTrue(stateContainer.getCPSR().getC());
    }
}
