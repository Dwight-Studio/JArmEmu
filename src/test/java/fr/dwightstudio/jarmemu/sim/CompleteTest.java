package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.RegexSourceParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class CompleteTest extends JArmEmuTest {

    public static final String HEX_REGEX = "[0-9A-Fa-f]+";
    public static final String BIN_REGEX = "[0-1]+";

    public static final Pattern HEX_PATTERN = Pattern.compile(
            "(?i)^" +
                    "(?<ADDRESS>" + HEX_REGEX + ")[ \t]*:[ \t]*" +
                    "(?<VALUE>" + HEX_REGEX + ")[ \t]*" +
                    "$(?-i)"
    );
    public static final Pattern BIN_PATTERN = Pattern.compile(
            "(?i)^" +
                    "(?<ADDRESS>" + HEX_REGEX + ")[ \t]*:[ \t]*" +
                    "(?<BYTE3>" + BIN_REGEX + ")[ \t]+" +
                    "(?<BYTE2>" + BIN_REGEX + ")[ \t]+" +
                    "(?<BYTE1>" + BIN_REGEX + ")[ \t]+" +
                    "(?<BYTE0>" + BIN_REGEX + ")[ \t]*" +
                    "$(?-i)"
    );

    private final Logger logger = Logger.getLogger(getClass().getName());

    RegexSourceParser parser;
    CodeInterpreter codeInterpreter;

    public void load(String name) {
        try {
            parser = new RegexSourceParser(new SourceScanner(new File(getClass().getResource(name).toURI())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void assertEqualsMemory(String memoryDumpFileName) {
        HashMap<Integer, Byte> expectedMemory = new HashMap<>();

        Scanner scanner;
        try {
            scanner = new Scanner(new File(getClass().getResource(memoryDumpFileName).toURI()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().toUpperCase();
            if (line.isEmpty()) continue;

            Matcher matcher = BIN_PATTERN.matcher(line);

            if (matcher.find()) {
                int address = Integer.parseInt(matcher.group("ADDRESS"), 16);
                byte byte3 = (byte) Integer.parseInt(matcher.group("BYTE3"), 2);
                byte byte2 = (byte) Integer.parseInt(matcher.group("BYTE2"), 2);
                byte byte1 = (byte) Integer.parseInt(matcher.group("BYTE1"), 2);
                byte byte0 = (byte) Integer.parseInt(matcher.group("BYTE0"), 2);

                expectedMemory.put(address, byte3);
                expectedMemory.put(address + 1, byte2);
                expectedMemory.put(address + 2, byte1);
                expectedMemory.put(address + 3, byte0);

                continue;
            }

            matcher = HEX_PATTERN.matcher(line);

            if (matcher.find()) {
                int address = Integer.parseInt(matcher.group("ADDRESS"), 16);
                int value = Integer.parseInt(matcher.group("VALUE"), 16);

                byte[] bytes = new byte[4];
                ByteBuffer.wrap(bytes).putInt(value);

                for (int j = 0; j < bytes.length; j++) {
                    expectedMemory.put(address + j, bytes[j]);
                }

                continue;
            }

            logger.severe("Can't match in line '" + line + "'");
        }

        for (Map.Entry<Integer, Byte> entry : expectedMemory.entrySet()) {
            assertEquals(codeInterpreter.stateContainer.memory.getByte(entry.getKey()), (byte) entry.getValue());
        }
    }

    @BeforeEach
    public void setUp() {
        codeInterpreter = new CodeInterpreter();
    }

    @Test
    public void factorialTest() {
        load("/complete/factorial.s");

        // Parse
        codeInterpreter.load(parser);
        codeInterpreter.resetState(StateContainer.DEFAULT_STACK_ADDRESS, StateContainer.DEFAULT_SYMBOLS_ADDRESS);
        codeInterpreter.restart();

        // Execution
        while (codeInterpreter.hasNextLine()) {
            codeInterpreter.nextLine();
            codeInterpreter.executeCurrentLine();
        }

        assertEqualsMemory("/complete/factorial-memory.d");
    }

    @Test
    public void matrixTest() {
        load("/complete/matrix.s");

        // Parse
        codeInterpreter.load(parser);
        codeInterpreter.resetState(StateContainer.DEFAULT_STACK_ADDRESS, StateContainer.DEFAULT_SYMBOLS_ADDRESS);
        codeInterpreter.restart();

        // Execution
        while (codeInterpreter.hasNextLine()) {
            codeInterpreter.nextLine();
            codeInterpreter.executeCurrentLine();
        }

        assertEqualsMemory("/complete/matrix-memory.d");
    }

}
