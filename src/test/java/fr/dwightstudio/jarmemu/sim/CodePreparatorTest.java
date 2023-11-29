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

import fr.dwightstudio.jarmemu.asm.Condition;
import fr.dwightstudio.jarmemu.asm.Instruction;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.ParsedFile;
import fr.dwightstudio.jarmemu.sim.parse.ParsedInstruction;
import fr.dwightstudio.jarmemu.sim.parse.ParsedObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodePreparatorTest {

    CodePreparator codePreparator;
    ParsedFile parsedFile;
    StateContainer stateContainer;
    HashMap<Integer, ParsedObject> instr;

    @BeforeEach
    public void setup() {
        codePreparator = new CodePreparator(0, 0);
        parsedFile = new ParsedFile();
        stateContainer = new StateContainer();
        instr = new HashMap<>();
    }

    @Test
    public void convertMovToShiftTest() {
        ParsedInstruction add = new ParsedInstruction(Instruction.ADD, Condition.AL, false, null, null, "r0", "r0", null, null);
        instr.put(0, add);
        parsedFile.getParsedObjects().put(0, add);
        codePreparator.getParsedFiles().add(parsedFile);
        codePreparator.replaceMovShifts(stateContainer);
        assertEquals(instr, codePreparator.getParsedFiles().getFirst().getParsedObjects());
        ParsedInstruction mov = new ParsedInstruction(Instruction.MOV, Condition.EQ, true, null, null, "r1", "r0", null, null);
        instr.put(1, mov);
        parsedFile.getParsedObjects().put(1, mov);
        codePreparator.replaceMovShifts(stateContainer);
        assertEquals(instr, codePreparator.getParsedFiles().getFirst().getParsedObjects());
        ParsedInstruction movShift = new ParsedInstruction(Instruction.MOV, Condition.AL, true, null, null, "r1", "r0", "LSL#5", null);
        ParsedInstruction Shift = new ParsedInstruction(Instruction.LSL, Condition.AL, true, null, null, "r1", "r0", "#5", null);
        instr.put(2, Shift);
        parsedFile.getParsedObjects().put(2, movShift);
        codePreparator.replaceMovShifts(stateContainer);
        assertEquals(instr, codePreparator.getParsedFiles().getFirst().getParsedObjects());
    }

}
