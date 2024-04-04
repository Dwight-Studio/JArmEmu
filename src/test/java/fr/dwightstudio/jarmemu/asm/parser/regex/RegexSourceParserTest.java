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

package fr.dwightstudio.jarmemu.asm.parser.regex;

import fr.dwightstudio.jarmemu.JArmEmuTest;
import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.asm.directive.*;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.instruction.*;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.sim.entity.StateContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNull;

public class RegexSourceParserTest extends JArmEmuTest {

    RegexSourceParser parser;
    StateContainer container;
    Object[] ins;

    private void test(String s) throws URISyntaxException, IOException, ASMException {
        File file = new File(Objects.requireNonNull(getClass().getResource(s)).toURI());
        Assertions.assertArrayEquals(ins, parser.parse(new SourceScanner(file, 0)).toArray());
    }

    @BeforeEach
    public void setup() {
        container = new StateContainer();
        parser = new RegexSourceParser();
        parser.currentSection = Section.TEXT;
    }

    @Test
    public void TestFormatLine() throws URISyntaxException, IOException, ASMException {
        ins = new Object[3];
        ins[0] = new ParsedSection(Section.TEXT);
        ins[1] = new ADDInstruction(Condition.AL, false, null, null, "R1", "R0", null, null);
        ins[2] = new ADCInstruction(Condition.CC, true, null, null, "R2", "R1", "R3", null);

        test("/formatLines.s");
    }

    @Test
    public void TestReadInstruction() throws URISyntaxException, IOException, ASMException {
        ins = new Object[4];
        ins[0] = new ParsedSection(Section.TEXT);
        ins[1] = new LDRInstruction(Condition.AL, false, null, null, "R1", "[R2]", null, null);
        ins[2] = new LDRInstruction(Condition.CC, false, null, null, "R1", "[R2]", null, null);
        ins[3] = new LDRInstruction(Condition.EQ, false, DataMode.BYTE, null, "R1", "[R2]", null, null);

        test("/normalLines.s");
    }

    @Test
    public void TestReadInstructionSub() throws URISyntaxException, IOException, ASMException {
        ins = new Object[3];
        ins[0] = new ParsedSection(Section.TEXT);
        ins[1] = new SUBInstruction(Condition.AL, false, null, null, "r2", "r0", "r1", null);
        ins[2] = new SUBInstruction(Condition.AL, false, null, null, "r0", "r1", null, null);

        test("/subLine.s");
    }

    @Test
    public void TestReadInstructionComplexer() throws URISyntaxException, IOException, ASMException {
        ins = new Object[12];
        ins[0] = new ParsedSection(Section.TEXT);
        ins[1] = new ADDInstruction(Condition.CC, true, null, null, "r0", "r9", "#2", null);
        ins[2] = new MLAInstruction(Condition.EQ, false, null, null, "r0", "r0", "r1", "r2");
        ins[3] = new SMLALInstruction(Condition.AL, true, null, null, "r4", "r5", "r6", "r7");
        ins[4] = new BICInstruction(Condition.LO, false, null, null, "r5", "r6", "#5", null);
        ins[5] = new LDRInstruction(Condition.AL, false, DataMode.BYTE, null, "r0", "=x", null, null);
        ins[6] = new STMInstruction(Condition.AL, false, null, UpdateMode.FD, "sp!", "{r0,r1,r2}", null, null);
        ins[7] = new BInstruction(Condition.AL, false, null, null, "etiquette", null, null, null);
        ins[8] = new ParsedLabel(Section.TEXT, "CECIESTUNEETIQUETTE");
        ins[9] = new LDRInstruction(Condition.AL, false, null, null, "R1", "[R0,R1,LSL#2]", null, null);
        ins[10] = new LDRInstruction(Condition.AL, false, null, null, "R1", "[R0]", "R1" , "LSL#2");
        ins[11] = new STRInstruction(Condition.AL, false, null, null, "fp", "[sp,#-4]!", null , null);

        test("/multipleLines.s");
    }

    @Test
    public void TestReadDirectives() throws URISyntaxException, IOException, ASMException {
        ins = new Object[16];
        ins[0] = new ParsedSection(Section.BSS);
        ins[1] = new ParsedSection(Section.DATA);
        ins[2] = new GlobalDirective(Section.DATA, "ExEC");
        ins[3] = new ParsedLabel(Section.DATA, "A");
        ins[4] = new ParsedLabel(Section.DATA, "b");
        ins[5] = new WordDirective(Section.DATA, "3");
        ins[6] = new ByteDirective(Section.DATA, "'x'");
        ins[7] = new GlobalDirective(Section.DATA, "Test");
        ins[8] = new ASCIIDirective(Section.DATA, "");
        ins[9] = new ASCIZDirective(Section.DATA, "\"\"");
        ins[10] = new EquivalentDirective(Section.DATA, "laBEL, 'c'");
        ins[11] = new ParsedSection(Section.DATA);
        ins[12] = new ParsedSection(Section.COMMENT);
        ins[13] = new ParsedSection(Section.TEXT);
        ins[14] = new LDRInstruction(Condition.AL, false, null, null, "R1", "=b", null, null);
        ins[15] = new ParsedSection(Section.END);

        test("/directiveMultipleLines.s");
    }
}
