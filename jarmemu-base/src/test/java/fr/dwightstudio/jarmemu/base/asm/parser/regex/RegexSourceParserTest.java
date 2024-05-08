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

package fr.dwightstudio.jarmemu.base.asm.parser.regex;

import fr.dwightstudio.jarmemu.base.JArmEmuTest;
import fr.dwightstudio.jarmemu.base.asm.ParsedLabel;
import fr.dwightstudio.jarmemu.base.asm.ParsedSection;
import fr.dwightstudio.jarmemu.base.asm.Section;
import fr.dwightstudio.jarmemu.base.asm.directive.*;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.instruction.*;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.DataMode;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import fr.dwightstudio.jarmemu.base.asm.modifier.UpdateMode;
import fr.dwightstudio.jarmemu.base.sim.SourceScanner;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

public class RegexSourceParserTest extends JArmEmuTest {

    private static Logger logger = Logger.getLogger(RegexSourceParserTest.class.getSimpleName());

    RegexSourceParser parser;
    StateContainer container;
    Object[] ins;

    private void test(String s) throws URISyntaxException, IOException, ASMException {
        File file = new File(Objects.requireNonNull(getClass().getResource(s)).toURI());
        Object[] pIns = parser.parse(new SourceScanner(file, 0)).toArray();
        try {
            Assertions.assertArrayEquals(ins, pIns);
        } catch (AssertionFailedError e) {
            logger.info(Arrays.toString(ins));
            logger.info(Arrays.toString(pIns));
            throw e;
        }
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
        ins[1] = new ADDInstruction(new Modifier(Condition.AL, false, null, null), "R1", "R0", null, null);
        ins[2] = new ADCInstruction(new Modifier(Condition.CC, true, null, null), "R2", "R1", "R3", null);

        test("/formatLines.s");
    }

    @Test
    public void TestReadInstruction() throws URISyntaxException, IOException, ASMException {
        ins = new Object[4];
        ins[0] = new ParsedSection(Section.TEXT);
        ins[1] = new LDRInstruction(new Modifier(Condition.AL, false, null, null), "R1", "[R2]", null, null);
        ins[2] = new LDRInstruction(new Modifier(Condition.CC, false, null, null), "R1", "[R2]", null, null);
        ins[3] = new LDRInstruction(new Modifier(Condition.EQ, false, DataMode.B, null), "R1", "[R2]", null, null);

        test("/normalLines.s");
    }

    @Test
    public void TestReadInstructionSub() throws URISyntaxException, IOException, ASMException {
        ins = new Object[3];
        ins[0] = new ParsedSection(Section.TEXT);
        ins[1] = new SUBInstruction(new Modifier(Condition.AL, false, null, null), "r2", "r0", "r1", null);
        ins[2] = new SUBInstruction(new Modifier(Condition.AL, false, null, null), "r0", "r0", "r1", null);

        test("/subLine.s");
    }

    @Test
    public void TestReadInstructionComplexer() throws URISyntaxException, IOException, ASMException {
        ins = new Object[12];
        ins[0] = new ParsedSection(Section.TEXT);
        ins[1] = new ADDInstruction(new Modifier(Condition.CC, true, null, null), "r0", "r9", "#2", null);
        ins[2] = new MLAInstruction(new Modifier(Condition.EQ, false, null, null), "r0", "r0", "r1", "r2");
        ins[3] = new SMLALInstruction(new Modifier(Condition.AL, true, null, null), "r4", "r5", "r6", "r7");
        ins[4] = new BICInstruction(new Modifier(Condition.LO, false, null, null), "r5", "r6", "#5", null);
        ins[5] = new LDRInstruction(new Modifier(Condition.AL, false, DataMode.B, null), "r0", "=x", null, null);
        ins[6] = new STMInstruction(new Modifier(Condition.AL, false, null, UpdateMode.FD), "sp!", "{r0,r1,r2}", null, null);
        ins[7] = new BInstruction(new Modifier(Condition.AL, false, null, null), "etiquette", null, null, null);
        ins[8] = new ParsedLabel(Section.TEXT, "CECIESTUNEETIQUETTE");
        ins[9] = new LDRInstruction(new Modifier(Condition.AL, false, null, null), "R1", "[R0,R1,LSL#2]", null, null);
        ins[10] = new LDRInstruction(new Modifier(Condition.AL, false, null, null), "R1", "[R0]", "R1" , "LSL#2");
        ins[11] = new STRInstruction(new Modifier(Condition.AL, false, null, null), "fp", "[sp,#-4]!", null , null);

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
        ins[14] = new LDRInstruction(new Modifier(Condition.AL, false, null, null), "R1", "=b", null, null);
        ins[15] = new ParsedSection(Section.END);

        test("/directiveMultipleLines.s");
    }
}
