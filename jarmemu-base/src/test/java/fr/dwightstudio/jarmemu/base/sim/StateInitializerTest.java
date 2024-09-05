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

package fr.dwightstudio.jarmemu.base.sim;

import fr.dwightstudio.jarmemu.base.JArmEmuTest;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.instruction.ADDInstruction;
import fr.dwightstudio.jarmemu.base.asm.instruction.LSLInstruction;
import fr.dwightstudio.jarmemu.base.asm.instruction.MOVInstruction;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.Modifier;
import fr.dwightstudio.jarmemu.base.asm.parser.regex.RegexSourceParser;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class StateInitializerTest extends JArmEmuTest {

    StateInitializer stateInitializer;
    StateContainer stateContainer;

    @BeforeEach
    public void setup() {
        stateInitializer = new StateInitializer();
        stateContainer = new StateContainer();
        stateContainer.clearAndInitFiles(1);
    }

    @Test
    public void convertMovToShiftTest() throws ASMException {
        SourceScanner sourceScanner = new SourceScanner(".TEXT \n ADD R0, R0 \n MOVSEQ R1, R0 \n MOVS R1, R0, LSL#5", "Test.s", 0);

        ADDInstruction add = new ADDInstruction(new Modifier(Condition.AL, false, null, null), "r0", "r0", null, null);
        MOVInstruction mov = new MOVInstruction(new Modifier(Condition.EQ, true, null, null), "r1", "r0", null, null);
        // MOVInstruction movShift = new MOVInstruction(Condition.AL, true, null, null, "r1", "r0", "LSL#5", null);
        LSLInstruction shift = new LSLInstruction(new Modifier(Condition.AL, true, null, null), "r1", "r0", "#5", null);

        Assertions.assertEquals(0, stateInitializer.load(new RegexSourceParser(), List.of(sourceScanner)).length);
        Assertions.assertEquals(0, stateInitializer.initiate(new StateContainer()).length);
        Assertions.assertArrayEquals(new Object[]{add, mov, shift}, stateInitializer.getInstructionMemory().toArray());
    }

}
