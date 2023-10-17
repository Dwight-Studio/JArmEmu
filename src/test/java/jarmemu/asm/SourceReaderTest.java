package jarmemu.asm;

import fr.dwightstudio.jarmemu.asm.SourceReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SourceReaderTest {

    URI firstFile = Objects.requireNonNull(getClass().getResource("/singleLine.s")).toURI();
    URI secondFile = Objects.requireNonNull(getClass().getResource("/normalLine.s")).toURI();
    SourceReader sourceReader1;
    SourceReader sourceReader2;

    public SourceReaderTest() throws URISyntaxException {
    }

    @BeforeEach
    public void setup() throws FileNotFoundException {
        sourceReader1 = new SourceReader(firstFile);
        sourceReader2 = new SourceReader(secondFile);
    }

    @Test
    public void SourceReaderTestBlankAndComments(){
        assertEquals("ADD R1, R0", sourceReader1.readOneLine());
    }

    @Test
    public void SourceReaderTestNormalLine(){
        assertEquals("SUB R0, R2", sourceReader2.readOneLine());
    }

}
