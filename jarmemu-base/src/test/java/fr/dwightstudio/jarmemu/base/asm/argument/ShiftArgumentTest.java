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
import fr.dwightstudio.jarmemu.base.sim.entity.ShiftFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShiftArgumentTest extends ArgumentTest<ShiftFunction> {
    public ShiftArgumentTest() {
        super(ShiftArgument.class);
    }

    @Test
    public void LSLTest() throws ASMException {
        int data = 0b00000000000000000000000000000001;
        stateContainer.getRegister(0).setData(0b0000000000000000000000000000010);
        stateContainer.getLR().setData(0b00000000000000000000000000000100);
        ShiftFunction f;

        f = parse("LSL#5");
        assertEquals(0b00000000000000000000000000100000, f.apply(data));

        f = parse("LSLR0");
        assertEquals(0b00000000000000000000000000000100, f.apply(data));

        f = parse("LSLLR");
        assertEquals(0b00000000000000000000000000010000, f.apply(data));
    }

    @Test
    public void LSRTest() throws ASMException {
        int data = 0b10000000000000000000000000000000;
        stateContainer.getRegister(0).setData(0b0000000000000000000000000000010);
        stateContainer.getLR().setData(0b00000000000000000000000000000100);
        ShiftFunction f;

        f = parse("LSR#5");
        assertEquals(0b00000100000000000000000000000000, f.apply(data));

        f = parse("LSRR0");
        assertEquals(0b00100000000000000000000000000000, f.apply(data));

        f = parse("LSRLR");
        assertEquals(0b00001000000000000000000000000000, f.apply(data));
    }

    @Test
    public void ASRTest() throws ASMException {
        int data = 0b10000000000000000000000000000000;
        stateContainer.getRegister(0).setData(0b0000000000000000000000000000010);
        stateContainer.getLR().setData(0b00000000000000000000000000000100);
        ShiftFunction f;

        f = parse("ASR#5");
        assertEquals(0b11111100000000000000000000000000, f.apply(data));

        f = parse("ASRR0");
        assertEquals(0b11100000000000000000000000000000, f.apply(data));

        f = parse("ASRLR");
        assertEquals(0b11111000000000000000000000000000, f.apply(data));

        data = 0b01000000000000000000000000000000;

        f = parse("ASR#5");
        assertEquals(0b00000010000000000000000000000000, f.apply(data));

        f = parse("ASRR0");
        assertEquals(0b00010000000000000000000000000000, f.apply(data));

        f = parse("ASRLR");
        assertEquals(0b00000100000000000000000000000000, f.apply(data));
    }

    @Test
    public void RORTest() throws ASMException {
        int data = 0b00000000000000000000000000001000;
        stateContainer.getRegister(0).setData(0b0000000000000000000000000000010);
        stateContainer.getLR().setData(0b00000000000000000000000000000100);
        ShiftFunction f;

        f = parse("ROR#5");
        assertEquals(0b01000000000000000000000000000000, f.apply(data));

        f = parse("RORR0");
        assertEquals(0b00000000000000000000000000000010, f.apply(data));

        f = parse("RORLR");
        assertEquals(0b10000000000000000000000000000000, f.apply(data));
    }

    @Test
    public void RRXTest() throws ASMException {
        int data;
        ShiftFunction f = parse("RRX");

        data = 0b00000000000000000000000000001000;
        stateContainer.getCPSR().setC(false);
        assertEquals(0b00000000000000000000000000000100, f.apply(data));
        assertFalse(stateContainer.getCPSR().getC());

        data = 0b00000000000000000000000000010000;
        stateContainer.getCPSR().setC(true);
        f = parse("RRX");
        assertEquals(0b10000000000000000000000000001000, f.apply(data));
        assertFalse(stateContainer.getCPSR().getC());

        data = 0b00000000000000000000000000001001;
        stateContainer.getCPSR().setC(false);
        f = parse("RRX");
        assertEquals(0b00000000000000000000000000000100, f.apply(data));
        assertTrue(stateContainer.getCPSR().getC());

        data = 0b00000000000000000000000000010001;
        stateContainer.getCPSR().setC(true);
        f = parse("RRX");
        assertEquals(0b10000000000000000000000000001000, f.apply(data));
        assertTrue(stateContainer.getCPSR().getC());
    }
}