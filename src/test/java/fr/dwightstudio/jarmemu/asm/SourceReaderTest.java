package fr.dwightstudio.jarmemu.asm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class SourceReaderTest {

    public SourceReaderTest() throws URISyntaxException {
    }

    @BeforeEach
    public void setup() throws FileNotFoundException {

    }

    @Test
    public void TestFormatLine() throws URISyntaxException, FileNotFoundException {
        URI fileURI = Objects.requireNonNull(getClass().getResource("/singleLine.s")).toURI();

        SourceReader reader = new SourceReader(fileURI);

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
        URI fileURI = Objects.requireNonNull(getClass().getResource("/normalLine.s")).toURI();

        SourceReader reader = new SourceReader(fileURI);
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList("R1", "[R2]"));

        reader.readOneLine();
        assertEquals(Instruction.LDR, reader.instruction);
        assertNull(reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        reader.readOneLine();
        assertEquals(Instruction.LDR, reader.instruction);
        assertEquals(Condition.CC, reader.conditionExec);
        assertNull(reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

        reader.readOneLine();
        assertEquals(Instruction.LDR, reader.instruction);
        assertEquals(Condition.EQ, reader.conditionExec);
        assertEquals(DataMode.BYTE, reader.dataMode);
        assertNull(reader.updateMode);
        assertFalse(reader.updateFlags);
        assertEquals(arguments, reader.arguments);

    }

}
