/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2023 Dwight Studio
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

package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.sim.exceptions.StuckExecutionASMException;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.LegacySourceParser;
import fr.dwightstudio.jarmemu.sim.parse.RegexSourceParser;
import fr.dwightstudio.jarmemu.sim.parse.SourceParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

    SourceParser parser;
    CodeInterpreter codeInterpreter;

    public void load(String name) {
        try {
            parser = new RegexSourceParser(new SourceScanner(new File(Objects.requireNonNull(getClass().getResource(name)).toURI())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void loadLegacy(String name) {
        try {
            parser = new LegacySourceParser(new SourceScanner(new File(Objects.requireNonNull(getClass().getResource(name)).toURI())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void assertEqualsMemory(String memoryDumpFileName) {
        HashMap<Integer, Byte> expectedMemory = new HashMap<>();

        Scanner scanner;
        try {
            scanner = new Scanner(new File(Objects.requireNonNull(getClass().getResource(memoryDumpFileName)).toURI()));
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

    private void execute() {
        // Parse
        assertArrayEquals(new SyntaxASMException[0], codeInterpreter.load(parser, StateContainer.DEFAULT_STACK_ADDRESS, StateContainer.DEFAULT_SYMBOLS_ADDRESS));
        codeInterpreter.restart();

        // Execution
        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            boolean flag = true;
            while (codeInterpreter.hasNext() && flag) {
                try {
                    codeInterpreter.executeCurrentLine(false);
                } catch (StuckExecutionASMException e) {
                    flag = false;
                }
            }
        });
    }

    @BeforeEach
    public void setUp() {
        codeInterpreter = new CodeInterpreter();
    }

    @Test
    public void factorialTest() {
        load("/complete/factorial.s");

        execute();

        assertEqualsMemory("/complete/factorial-memory.d");
    }

    @Test
    public void factorialLegacyTest() {
        loadLegacy("/complete/factorial.s");

        // Parse
        execute();

        assertEqualsMemory("/complete/factorial-memory.d");
    }

    @Test
    public void matrixTest() {
        load("/complete/matrix.s");

        // Parse
        execute();

        assertEqualsMemory("/complete/matrix-memory.d");
    }

    @Test
    public void matrixLegacyTest() {
        loadLegacy("/complete/matrix.s");

        // Parse
        execute();

        assertEqualsMemory("/complete/matrix-memory.d");
    }

    @Test
    public void pgcdTest() {
        load("/complete/pgcd.s");

        // Parse
        execute();

        assertEqualsMemory("/complete/pgcd-memory.d");
    }

    @Test
    public void pgcdLegacyTest() {
        loadLegacy("/complete/pgcd.s");

        // Parse
        execute();

        assertEqualsMemory("/complete/pgcd-memory.d");
    }

    @Test
    public void helloworldIntTest() {
        load("/complete/helloworldInt.s");

        // Parse
        execute();

        assertEqualsMemory("/complete/helloworldInt-memory.d");
    }

    @Test
    public void helloworldIntLegacyTest() {
        loadLegacy("/complete/helloworldInt.s");

        // Parse
        execute();

        assertEqualsMemory("/complete/helloworldInt-memory.d");
    }

    @Test
    public void helloworldAsciiTest() {
        load("/complete/helloworldAscii.s");

        // Parse
        execute();

        assertEqualsMemory("/complete/helloworldAscii-memory.d");
    }

    @Test
    public void helloworldAsciiLegacyTest() {
        loadLegacy("/complete/helloworldAscii.s");

        // Parse
        execute();

        assertEqualsMemory("/complete/helloworldAscii-memory.d");
    }

}
