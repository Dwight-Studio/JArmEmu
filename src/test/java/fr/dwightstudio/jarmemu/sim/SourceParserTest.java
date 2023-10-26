package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.asm.Condition;
import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.Instruction;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SourceParserTest {

    public SourceParserTest() throws URISyntaxException {
    }

    @BeforeEach
    public void setup() throws FileNotFoundException {

    }

    @Test
    public void TestFormatLine() throws URISyntaxException, FileNotFoundException {
        File file = new File(getClass().getResource("/singleLine.s").toURI());

        SourceParser reader = new SourceParser(file);

        try {
            reader.readOneLine();
        } catch (IllegalStateException ignored) {}
        assertEquals("ADD R1, R0", reader.currentLine);

        try {
            reader.readOneLine();
        } catch (IllegalStateException ignored) {}
        assertEquals("ADCBCS R2, R1, R3", reader.currentLine);

    }

    @Test
    public void TestReadInstruction() throws URISyntaxException, FileNotFoundException {
        File file = new File(getClass().getResource("/normalLine.s").toURI());

        SourceParser reader = new SourceParser(file);
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList("R1", "[R2]"));

        reader.readOneLine();
        Assertions.assertEquals(Instruction.LDR, reader.instruction);
        assertNull(reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        reader.readOneLine();
        assertEquals(Instruction.LDR, reader.instruction);
        Assertions.assertEquals(Condition.CC, reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        reader.readOneLine();
        assertEquals(Instruction.LDR, reader.instruction);
        assertEquals(Condition.EQ, reader.conditionExec);
        Assertions.assertEquals(DataMode.BYTE, reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

    }

    @Test
    public void TestReadInstructionComplexer() throws URISyntaxException, FileNotFoundException {
        File file = new File(getClass().getResource("/multipleLines.s").toURI());
        ArrayList<String> arguments;

        SourceParser reader = new SourceParser(file);

        arguments = new ArrayList<>(Arrays.asList("R0", "R9", "#2"));
        reader.readOneLine();
        assertEquals(Instruction.ADD, reader.instruction);
        assertEquals(Condition.CC, reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertTrue(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        arguments = new ArrayList<>(Arrays.asList("R0", "R0", "R1", "R2"));
        reader.readOneLine();
        assertEquals(Instruction.MLA, reader.instruction);
        assertEquals(Condition.EQ, reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        arguments = new ArrayList<>(Arrays.asList("R4", "R5", "R6", "R7"));
        reader.readOneLine();
        assertEquals(Instruction.SMLAL, reader.instruction);
        assertEquals(Condition.AL, reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertTrue(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        arguments = new ArrayList<>(Arrays.asList("R5", "R6", "#5"));
        reader.readOneLine();
        assertEquals(Instruction.BIC, reader.instruction);
        assertEquals(Condition.LO, reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        arguments = new ArrayList<>(Arrays.asList("R0", "=X"));
        reader.readOneLine();
        assertEquals(Instruction.LDR, reader.instruction);
        assertNull(reader.conditionExec);
        assertEquals(DataMode.BYTE, reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        arguments = new ArrayList<>(Arrays.asList("SP!","{R0,R1,R2}"));
        reader.readOneLine();
        assertEquals(Instruction.STM, reader.instruction);
        assertNull(reader.conditionExec);
        assertNull(reader.dataMode);
        Assertions.assertEquals(UpdateMode.FD, reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        arguments = new ArrayList<>(List.of("ETIQUETTE"));
        reader.readOneLine();
        assertEquals(Instruction.B, reader.instruction);
        assertEquals(Condition.AL, reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        arguments = new ArrayList<>(List.of("CECIESTUNEETIQUETTE:"));
        reader.readOneLine();
        assertNull(reader.instruction);
        assertNull(reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

    }

}
