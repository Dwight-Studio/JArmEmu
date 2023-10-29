package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.asm.Condition;
import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.Instruction;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.obj.ParsedInstruction;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
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

public class RegexSourceParserTest {

    StateContainer container;

    @BeforeEach
    public void setup() {
        container = new StateContainer();
    }

    @Test
    public void TestFormatLine() throws URISyntaxException, FileNotFoundException {
        File file = new File(getClass().getResource("/singleLine.s").toURI());

        RegexSourceParser parser = new RegexSourceParser(new SourceScanner(file));

        assertEquals(
                new ParsedInstruction(Instruction.ADD, Condition.AL, false, null, null, "R1", "R0", null, null),
                parser.parseOneLine(container)
        );

        assertEquals(
                new ParsedInstruction(Instruction.ADC, Condition.CC, false, null, null, "R2", "R1", "R3", null),
                parser.parseOneLine(container)
        );
    }

    @Test
    public void TestReadInstruction() throws URISyntaxException, FileNotFoundException {
        File file = new File(Objects.requireNonNull(getClass().getResource("/normalLine.s")).toURI());

        LegacySourceParser reader = new LegacySourceParser(file);
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList("R1", "[R2]"));

        reader.readOneLineASM();
        Assertions.assertEquals(Instruction.LDR, reader.instruction);
        assertEquals(Condition.AL, reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        reader.readOneLineASM();
        assertEquals(Instruction.LDR, reader.instruction);
        assertEquals(Condition.CC, reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        reader.readOneLineASM();
        assertEquals(Instruction.LDR, reader.instruction);
        assertEquals(Condition.EQ, reader.conditionExec);
        Assertions.assertEquals(DataMode.BYTE, reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

    }

    @Test
    public void TestReadInstructionSub() throws URISyntaxException, FileNotFoundException {
        File file = new File(Objects.requireNonNull(getClass().getResource("/subLine.s")).toURI());
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList("R2", "R0", "R1"));

        LegacySourceParser reader = new LegacySourceParser(file);
        reader.readOneLineASM();
        assertEquals(Instruction.SUB, reader.instruction);
        assertEquals(Condition.AL, reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);
    }

    @Test
    public void TestReadInstructionComplexer() throws URISyntaxException, FileNotFoundException {
        File file = new File(Objects.requireNonNull(getClass().getResource("/multipleLines.s")).toURI());
        ArrayList<String> arguments;

        LegacySourceParser reader = new LegacySourceParser(file);

        arguments = new ArrayList<>(Arrays.asList("R0", "R9", "#2"));
        reader.readOneLineASM();
        assertEquals(Instruction.ADD, reader.instruction);
        assertEquals(Condition.CC, reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertTrue(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        arguments = new ArrayList<>(Arrays.asList("R0", "R0", "R1", "R2"));
        reader.readOneLineASM();
        assertEquals(Instruction.MLA, reader.instruction);
        assertEquals(Condition.EQ, reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        arguments = new ArrayList<>(Arrays.asList("R4", "R5", "R6", "R7"));
        reader.readOneLineASM();
        assertEquals(Instruction.SMLAL, reader.instruction);
        assertEquals(Condition.AL, reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertTrue(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        arguments = new ArrayList<>(Arrays.asList("R5", "R6", "#5"));
        reader.readOneLineASM();
        assertEquals(Instruction.BIC, reader.instruction);
        assertEquals(Condition.LO, reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        arguments = new ArrayList<>(Arrays.asList("R0", "=X"));
        reader.readOneLineASM();
        assertEquals(Instruction.LDR, reader.instruction);
        assertEquals(Condition.AL, reader.conditionExec);
        assertEquals(DataMode.BYTE, reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        arguments = new ArrayList<>(Arrays.asList("SP!","{R0,R1,R2}"));
        reader.readOneLineASM();
        assertEquals(Instruction.STM, reader.instruction);
        assertEquals(Condition.AL, reader.conditionExec);
        assertNull(reader.dataMode);
        Assertions.assertEquals(UpdateMode.FD, reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        arguments = new ArrayList<>(List.of("ETIQUETTE"));
        reader.readOneLineASM();
        assertEquals(Instruction.B, reader.instruction);
        assertEquals(Condition.AL, reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        arguments = new ArrayList<>(List.of("CECIESTUNEETIQUETTE:"));
        reader.readOneLineASM();
        assertNull(reader.instruction);
        assertEquals(Condition.AL, reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        arguments = new ArrayList<>(Arrays.asList("LDR","R1","[R0,R1,LSL#2]"));
        reader.readOneLineASM();
        assertEquals(Instruction.LDR, reader.instruction);
        assertEquals(Condition.AL, reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);
    }

}
