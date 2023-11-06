package fr.dwightstudio.jarmemu.sim.args;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.args.AddressParser;
import fr.dwightstudio.jarmemu.sim.args.ShiftParser;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class AddressParserTest extends JArmEmuTest {
    private StateContainer stateContainer;
    private static final AddressParser ADDRESS = new AddressParser();
    private static final ShiftParser SHIFT = new ShiftParser();
    private boolean testR4 = true;

    @BeforeEach
    public void setUp() {
        stateContainer = new StateContainer();

        for (int i = 0 ; i < 16 ; i++) {
            stateContainer.registers[i].setData(i);
        }

        stateContainer.cpsr.setData(16);
        stateContainer.spsr.setData(17);
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
                i -> stateContainer.registers[i].getData(),
                (i, in) -> {},
                (i, in) -> assertTrue(stateContainer.registers[i].getData() == i && in.toInt() == i));
    }

    @Test
    public void constOffsetTest() {
        testAllRegister(
                i -> "[R" + i + ",#4]",
                i -> stateContainer.registers[i].getData()+4,
                (i, in) -> SHIFT.parse(stateContainer, "LSL#2").apply(in.toInt()),
                (i, in) -> assertTrue(stateContainer.registers[i].getData() == i && in.toInt() == i + 4));
    }

    @Test
    public void varOffsetTest() {
        testAllRegister(
                i -> "[R" + i + ",R4]",
                i -> stateContainer.registers[i].getData() + stateContainer.registers[4].getData(),
                (i, in) -> SHIFT.parse(stateContainer, "LSL#2").apply(in.toInt()),
                (i, in) -> assertTrue(stateContainer.registers[i].getData() == i && in.toInt() == stateContainer.registers[i].getData() + stateContainer.registers[4].getData()));
    }

    @Test
    public void shiftedOffsetTest() {
        testAllRegister(
                i -> "[R" + i + ",R4,LSL#1]",
                i -> stateContainer.registers[i].getData() + (stateContainer.registers[4].getData() << 1),
                (i, in) -> SHIFT.parse(stateContainer, "LSL#2").apply(in.toInt()),
                (i, in) -> assertTrue(stateContainer.registers[i].getData() == i && in.toInt() == stateContainer.registers[i].getData() + (stateContainer.registers[4].getData() << 1)));
    }

    @Test
    public void preUpdatedConstOffsetTest() {
        testAllRegister(
                i -> "[R" + i + ",#4]!",
                i -> i+4,
                (i, in) -> SHIFT.parse(stateContainer, "LSL#2").apply(in.toInt()),
                (i, in) -> assertTrue(stateContainer.registers[i].getData() == i+4 && in.toInt() == i + 4));
    }

    @Test
    public void preUpdatedVarOffsetTest() {
        testR4 = false;
        testAllRegister(
                i -> "[R" + i + ",R4]!",
                i -> i + stateContainer.registers[4].getData(),
                (i, in) -> SHIFT.parse(stateContainer, "LSL#2").apply(in.toInt()),
                (i, in) -> assertTrue(stateContainer.registers[i].getData() == i + stateContainer.registers[4].getData() && in.toInt() == i + stateContainer.registers[4].getData()));
    }

    @Test
    public void preUpdatedShiftedOffsetTest() {
        testR4 = false;
        testAllRegister(
                i -> "[R" + i + ",R4,LSL#1]!",
                i -> i + (stateContainer.registers[4].getData() << 1),
                (i, in) -> SHIFT.parse(stateContainer, "LSL#2").apply(in.toInt()),
                (i, in) -> assertTrue(stateContainer.registers[i].getData() == i + (stateContainer.registers[4].getData() << 1) && in.toInt() == i + (stateContainer.registers[4].getData() << 1)));
    }

    @Test
    public void postUpdatedConstOffsetTest() {
        testR4 = false;
        testAllRegister(
                i -> "[R" + i + ",R4,LSL#1]!",
                i -> i + (stateContainer.registers[4].getData() << 1),
                (i, in) -> SHIFT.parse(stateContainer, "LSL#2").apply(in.toInt()),
                (i, in) -> assertTrue(stateContainer.registers[i].getData() == i + (stateContainer.registers[4].getData() << 1) && in.toInt() == i + (stateContainer.registers[4].getData() << 1)));
    }

    @Test
    public void postUpdatedVarOffsetTest() {
        testR4 = false;
        testAllRegister(
                i -> "[R" + i + ",R4,LSL#1]!",
                i -> i + (stateContainer.registers[4].getData() << 1),
                (i, in) -> SHIFT.parse(stateContainer, "LSL#2").apply(in.toInt()),
                (i, in) -> assertTrue(stateContainer.registers[i].getData() == i + (stateContainer.registers[4].getData() << 1) && in.toInt() == i + (stateContainer.registers[4].getData() << 1)));
    }

    @Test
    public void postUpdatedShiftedOffsetTest() {
        testR4 = false;
        testAllRegister(
                i -> "[R" + i + ",R4,LSL#1]!",
                i -> i + (stateContainer.registers[4].getData() << 1),
                (i, in) -> SHIFT.parse(stateContainer, "LSL#2").apply(in.toInt()),
                (i, in) -> assertTrue(stateContainer.registers[i].getData() == i + (stateContainer.registers[4].getData() << 1) && in.toInt() == i + (stateContainer.registers[4].getData() << 1)));
    }
}