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
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AddressParserTest extends JArmEmuTest {
    private StateContainer stateContainer;
    private static final AddressParser ADDRESS = new AddressParser();
    private static final ShiftParser SHIFT = new ShiftParser();
    private boolean testR4 = true;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();

        for (int i = 0 ; i < 16 ; i++) {
            stateContainer.getRegister(i).setData(i);
        }

        stateContainer.getCPSR().setData(16);
        stateContainer.getSPSR().setData(17);
    }

    private void testAllRegister(Function<Integer, String> repFunc, Function<Integer, Integer> testFunc, BiConsumer<Integer, AddressParser.UpdatableInteger> beforeFunc, BiConsumer<Integer, AddressParser.UpdatableInteger> afterFunc) {
        for (int i = 0 ; i < 16 ; i++) {
            if (i == 4 && !testR4) continue;
            AddressParser.UpdatableInteger integer = ADDRESS.parse(stateContainer, repFunc.apply(i));
            assertEquals(testFunc.apply(i), integer.toInt());
            beforeFunc.accept(i, integer);
            integer.update();
            afterFunc.accept(i, integer);
        }
    }

    @Test
    public void simpleTest() {
        testAllRegister(
                i -> "[R" + i + "]",
                i -> stateContainer.getRegister(i).getData(),
                (i, in) -> {},
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i && in.toInt() == i));
    }

    @Test
    public void constOffsetTest() {
        testAllRegister(
                i -> "[R" + i + ",#4]",
                i -> stateContainer.getRegister(i).getData()+4,
                (i, in) -> SHIFT.parse(stateContainer, "LSL#2").apply(in.toInt()),
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i && in.toInt() == i + 4));
    }

    @Test
    public void varOffsetTest() {
        testAllRegister(
                i -> "[R" + i + ",R4]",
                i -> stateContainer.getRegister(i).getData() + stateContainer.getRegister(4).getData(),
                (i, in) -> SHIFT.parse(stateContainer, "LSL#2").apply(in.toInt()),
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i && in.toInt() == stateContainer.getRegister(i).getData() + stateContainer.getRegister(4).getData()));
    }

    @Test
    public void shiftedOffsetTest() {
        testAllRegister(
                i -> "[R" + i + ",R4,LSL#1]",
                i -> stateContainer.getRegister(i).getData() + (stateContainer.getRegister(4).getData() << 1),
                (i, in) -> SHIFT.parse(stateContainer, "LSL#2").apply(in.toInt()),
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i && in.toInt() == stateContainer.getRegister(i).getData() + (stateContainer.getRegister(4).getData() << 1)));
    }

    @Test
    public void preUpdatedConstOffsetTest() {
        testAllRegister(
                i -> "[R" + i + ",#4]!",
                i -> i+4,
                (i, in) -> SHIFT.parse(stateContainer, "LSL#2").apply(in.toInt()),
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i+4 && in.toInt() == i + 4));
    }

    @Test
    public void preUpdatedVarOffsetTest() {
        testR4 = false;
        testAllRegister(
                i -> "[R" + i + ",R4]!",
                i -> i + stateContainer.getRegister(4).getData(),
                (i, in) -> SHIFT.parse(stateContainer, "LSL#2").apply(in.toInt()),
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i + stateContainer.getRegister(4).getData() && in.toInt() == i + stateContainer.getRegister(4).getData()));
    }

    @Test
    public void preUpdatedShiftedOffsetTest() {
        testR4 = false;
        testAllRegister(
                i -> "[R" + i + ",R4,LSL#1]!",
                i -> i + (stateContainer.getRegister(4).getData() << 1),
                (i, in) -> SHIFT.parse(stateContainer, "LSL#2").apply(in.toInt()),
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i + (stateContainer.getRegister(4).getData() << 1) && in.toInt() == i + (stateContainer.getRegister(4).getData() << 1)));
    }

    @Test
    public void postUpdatedConstOffsetTest() {
        testR4 = false;
        testAllRegister(
                i -> "[R" + i + ",R4,LSL#1]!",
                i -> i + (stateContainer.getRegister(4).getData() << 1),
                (i, in) -> SHIFT.parse(stateContainer, "LSL#2").apply(in.toInt()),
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i + (stateContainer.getRegister(4).getData() << 1) && in.toInt() == i + (stateContainer.getRegister(4).getData() << 1)));
    }

    @Test
    public void postUpdatedVarOffsetTest() {
        testR4 = false;
        testAllRegister(
                i -> "[R" + i + ",R4,LSL#1]!",
                i -> i + (stateContainer.getRegister(4).getData() << 1),
                (i, in) -> SHIFT.parse(stateContainer, "LSL#2").apply(in.toInt()),
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i + (stateContainer.getRegister(4).getData() << 1) && in.toInt() == i + (stateContainer.getRegister(4).getData() << 1)));
    }

    @Test
    public void postUpdatedShiftedOffsetTest() {
        testR4 = false;
        testAllRegister(
                i -> "[R" + i + ",R4,LSL#1]!",
                i -> i + (stateContainer.getRegister(4).getData() << 1),
                (i, in) -> SHIFT.parse(stateContainer, "LSL#2").apply(in.toInt()),
                (i, in) -> assertTrue(stateContainer.getRegister(i).getData() == i + (stateContainer.getRegister(4).getData() << 1) && in.toInt() == i + (stateContainer.getRegister(4).getData() << 1)));
    }
}