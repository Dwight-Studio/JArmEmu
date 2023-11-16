package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.util.RegisterUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RegexSourceParserTest extends JArmEmuTest {

    StateContainer container;

    @BeforeEach
    public void setup() {
        container = new StateContainer();
    }

    @Test
    public void TestFormatLine() throws URISyntaxException, FileNotFoundException {
        File file = new File(Objects.requireNonNull(getClass().getResource("/singleLine.s")).toURI());

        RegexSourceParser parser = new RegexSourceParser(new SourceScanner(file));
        parser.currentSection.setValue(Section.TEXT);

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

        RegexSourceParser parser = new RegexSourceParser(new SourceScanner(file));
        parser.currentSection.setValue(Section.TEXT);

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

        RegexSourceParser parser = new RegexSourceParser(new SourceScanner(file));
        parser.currentSection.setValue(Section.TEXT);

        assertEquals(
                new ParsedInstruction(Instruction.SUB, Condition.AL, false, null, null, "r2", "r0", "r1", null),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.SUB, Condition.AL, false, null, null, "r0", "r1", null, null),
                parser.parseOneLine()
        );
    }

    @Test
    public void TestReadInstructionComplexer() throws URISyntaxException, FileNotFoundException {
        File file = new File(Objects.requireNonNull(getClass().getResource("/multipleLines.s")).toURI());

        RegexSourceParser parser = new RegexSourceParser(new SourceScanner(file));
        parser.currentSection.setValue(Section.TEXT);

        assertEquals(
                new ParsedInstruction(Instruction.ADD, Condition.CC, true, null, null, "r0", "r9", "#2", null),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.MLA, Condition.EQ, false, null, null, "r0", "r0", "r1", "r2"),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.SMLAL, Condition.AL, true, null, null, "r4", "r5", "r6", "r7"),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.BIC, Condition.LO, false, null, null, "r5", "r6", "#5", null),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.LDR, Condition.AL, false, DataMode.BYTE, null, "r0", "=x", null, null),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.STM, Condition.AL, false, null, UpdateMode.FD, "sp!", "{r0,r1,r2}", null, null),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.B, Condition.AL, false, null, null, "etiquette", null, null, null),
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

    @Test
    public void TestReadDirectives() throws URISyntaxException, FileNotFoundException {
        File file = new File(Objects.requireNonNull(getClass().getResource("/directiveMultipleLines.s")).toURI());

        RegexSourceParser parser = new RegexSourceParser(new SourceScanner(file));

        ParsedDirectivePack parsedDirectivePack = new ParsedDirectivePack();
        parsedDirectivePack.add(new ParsedSection(Section.BSS));
        parsedDirectivePack.add(new ParsedSection(Section.DATA));
        assertEquals(
                parsedDirectivePack.close(),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedDirective(Directive.GLOBAL, "ExEC", Section.DATA),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedDirectiveLabel("A", Section.DATA),
                parser.parseOneLine()
        );

        parsedDirectivePack = new ParsedDirectivePack();
        parsedDirectivePack.add(new ParsedDirectiveLabel("b", Section.DATA));
        parsedDirectivePack.add(new ParsedDirective(Directive.WORD, "3", Section.DATA));
        parsedDirectivePack.add(new ParsedDirective(Directive.BYTE, "'x'", Section.DATA));
        parsedDirectivePack.add(new ParsedDirective(Directive.GLOBAL, "Test", Section.DATA));
        assertEquals(
                parsedDirectivePack.close(),
                parser.parseOneLine()
        );

        parsedDirectivePack = new ParsedDirectivePack();
        parsedDirectivePack.add(new ParsedDirective(Directive.ASCII, "", Section.DATA));
        parsedDirectivePack.add(new ParsedDirective(Directive.ASCIZ, "\"\"", Section.DATA));
        assertEquals(
                parsedDirectivePack.close(),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedDirective(Directive.EQU, "laBEL, 'c'", Section.DATA),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedSection(Section.DATA),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedSection(Section.COMMENT),
                parser.parseOneLine()
        );

        assertNull(
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedDirective(Directive.ASCII, "\"Hey\"", Section.COMMENT),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedSection(Section.TEXT),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedInstruction(Instruction.LDR, Condition.AL, false, null, null, "R1", "=b", null, null),
                parser.parseOneLine()
        );

        assertEquals(
                new ParsedSection(Section.END),
                parser.parseOneLine()
        );
    }
}
