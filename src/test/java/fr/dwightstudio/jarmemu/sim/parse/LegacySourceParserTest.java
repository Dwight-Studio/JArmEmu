package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.LegacySourceParser;
import fr.dwightstudio.jarmemu.util.RegisterUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class LegacySourceParserTest extends JArmEmuTest {

    StateContainer container;

    @BeforeEach
    public void setup() {
        container = new StateContainer();
    }

    @Test
    public void TestFormatLine() throws URISyntaxException, FileNotFoundException {
        File file = new File(Objects.requireNonNull(getClass().getResource("/singleLine.s")).toURI());

        LegacySourceParser parser = new LegacySourceParser(file);
        parser.currentSection = Section.TEXT;

        assertEquals(
                new ParsedInstruction(Instruction.ADD, Condition.AL, false, null, null, "R1", "R0", null, null),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.ADC, Condition.CC, true, null, null, "R2", "R1", "R3", null),
                parser.parseOneLine()
        );

    }

    @Test
    public void TestReadInstruction() throws URISyntaxException, FileNotFoundException {
        File file = new File(Objects.requireNonNull(getClass().getResource("/normalLine.s")).toURI());

        LegacySourceParser parser = new LegacySourceParser(file);
        parser.currentSection = Section.TEXT;

        assertEquals(
                new ParsedInstruction(Instruction.LDR, Condition.AL, false, null, null, "R1", "[R2]", null, null),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.ADD, Condition.CC, false, null, null, "R1", "[R2]", null, null),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.ADD, Condition.EQ, false, DataMode.BYTE, null, "R1", "[R2]", null, null),
                parser.parseOneLine()
        );

    }

    @Test
    public void TestReadInstructionSub() throws URISyntaxException, FileNotFoundException {
        File file = new File(Objects.requireNonNull(getClass().getResource("/subLine.s")).toURI());

        LegacySourceParser parser = new LegacySourceParser(file);
        parser.currentSection = Section.TEXT;

        assertEquals(
                new ParsedInstruction(Instruction.SUB, Condition.AL, false, null, null, "R2", "R0", "R1", null),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.SUB, Condition.AL, false, null, null, "R0", "R1", null, null),
                parser.parseOneLine()
        );
    }

    @Test
    public void TestReadInstructionComplexer() throws URISyntaxException, FileNotFoundException {
        File file = new File(Objects.requireNonNull(getClass().getResource("/multipleLines.s")).toURI());

        LegacySourceParser parser = new LegacySourceParser(file);
        parser.currentSection = Section.TEXT;

        assertEquals(
                new ParsedInstruction(Instruction.ADD, Condition.CC, true, null, null, "R0", "R9", "#2", null),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.MLA, Condition.EQ, false, null, null, "R0", "R0", "R1", "R2"),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.SMLAL, Condition.AL, true, null, null, "R4", "R5", "R6", "R7"),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.BIC, Condition.LO, false, null, null, "R5", "R6", "#5", null),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.LDR, Condition.AL, false, DataMode.BYTE, null, "R0", "=X", null, null),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.STM, Condition.AL, false, null, UpdateMode.FD, "SP!", "{R0,R1,R2}", null, null),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.B, Condition.AL, false, null, null, "ETIQUETTE", null, null, null),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedLabel("CECIESTUNEETIQUETTE", RegisterUtils.lineToPC(parser.getSourceScanner().getCurrentInstructionValue() + 1)),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.LDR, Condition.AL, false, null, null, "R1","[R0,R1,LSL#2]", null, null),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.LDR, Condition.AL, false, null, null, "R1","[R0]", "R1" ,"LSL#2"),
                parser.parseOneLine()
        );
    }

}
