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
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddressArgumentTest extends ArgumentTest<AddressArgument.UpdatableInteger> {

    private boolean testR4 = true;

    public AddressArgumentTest() {
        super(AddressArgument.class);
    }

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();

        for (int i = 0 ; i < 16 ; i++) {
            stateContainer.getRegister(i).setData(i);
        }

        stateContainer.getCPSR().setData(16);
        stateContainer.getSPSR().setData(17);
    }

    private void testAllRegister(Function<Integer, String> repFunc, Function<Integer, Integer> testFunc, boolean applyShift, BiConsumer<Integer, AddressArgument.UpdatableInteger> afterFunc) throws ASMException {
        for (int i = 0 ; i < 16 ; i++) {
            if (i == 4 && !testR4) continue;
            AddressArgument address = new AddressArgument(repFunc.apply(i));
            ShiftArgument shift = new ShiftArgument("LSL#2");
            
            address.contextualize(stateContainer);
            shift.contextualize(stateContainer);
            
            AddressArgument.UpdatableInteger integer = address.getValue(stateContainer);

            Assertions.assertEquals(testFunc.apply(i), integer.toInt());
            
            if (applyShift) shift.getValue(stateContainer).apply(integer.toInt());
            integer.update();
            afterFunc.accept(i, integer);
        }
    }

    @Test
    public void simpleTest() throws ASMException {
        testAllRegister(
                i -> "[R" + i + "]",
                i -> stateContainer.getRegister(i).getData(),
                false,
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i && in.toInt() == i));
    }

    @Test
    public void constOffsetTest() throws ASMException {
        testAllRegister(
                i -> "[R" + i + ",#4]",
                i -> stateContainer.getRegister(i).getData()+4,
                true,
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i && in.toInt() == i + 4));
    }

    @Test
    public void varOffsetTest() throws ASMException {
        testAllRegister(
                i -> "[R" + i + ",R4]",
                i -> stateContainer.getRegister(i).getData() + stateContainer.getRegister(4).getData(),
                true,
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i && in.toInt() == stateContainer.getRegister(i).getData() + stateContainer.getRegister(4).getData()));
    }

    @Test
    public void shiftedOffsetTest() throws ASMException {
        testAllRegister(
                i -> "[R" + i + ",R4,LSL#1]",
                i -> stateContainer.getRegister(i).getData() + (stateContainer.getRegister(4).getData() << 1),
                true,
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i && in.toInt() == stateContainer.getRegister(i).getData() + (stateContainer.getRegister(4).getData() << 1)));
    }

    @Test
    public void preUpdatedConstOffsetTest() throws ASMException {
        testAllRegister(
                i -> "[R" + i + ",#4]!",
                i -> i+4,
                true,
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i+4 && in.toInt() == i + 4));
    }

    @Test
    public void preUpdatedVarOffsetTest() throws ASMException {
        testR4 = false;
        testAllRegister(
                i -> "[R" + i + ",R4]!",
                i -> i + stateContainer.getRegister(4).getData(),
                true,
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i + stateContainer.getRegister(4).getData() && in.toInt() == i + stateContainer.getRegister(4).getData()));
    }

    @Test
    public void preUpdatedShiftedOffsetTest() throws ASMException {
        testR4 = false;
        testAllRegister(
                i -> "[R" + i + ",R4,LSL#1]!",
                i -> i + (stateContainer.getRegister(4).getData() << 1),
                true,
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i + (stateContainer.getRegister(4).getData() << 1) && in.toInt() == i + (stateContainer.getRegister(4).getData() << 1)));
    }

    @Test
    public void postUpdatedConstOffsetTest() throws ASMException {
        testR4 = false;
        testAllRegister(
                i -> "[R" + i + ",R4,LSL#1]!",
                i -> i + (stateContainer.getRegister(4).getData() << 1),
                true,
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i + (stateContainer.getRegister(4).getData() << 1) && in.toInt() == i + (stateContainer.getRegister(4).getData() << 1)));
    }

    @Test
    public void postUpdatedVarOffsetTest() throws ASMException {
        testR4 = false;
        testAllRegister(
                i -> "[R" + i + ",R4,LSL#1]!",
                i -> i + (stateContainer.getRegister(4).getData() << 1),
                true,
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i + (stateContainer.getRegister(4).getData() << 1) && in.toInt() == i + (stateContainer.getRegister(4).getData() << 1)));
    }

    @Test
    public void postUpdatedShiftedOffsetTest() throws ASMException {
        testR4 = false;
        testAllRegister(
                i -> "[R" + i + ",R4,LSL#1]!",
                i -> i + (stateContainer.getRegister(4).getData() << 1),
                true,
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i + (stateContainer.getRegister(4).getData() << 1) && in.toInt() == i + (stateContainer.getRegister(4).getData() << 1)));
    }
}
