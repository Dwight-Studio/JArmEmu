package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.asm.Condition;
import fr.dwightstudio.jarmemu.asm.Instruction;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.ParsedInstruction;
import fr.dwightstudio.jarmemu.sim.parse.ParsedObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeInterpreterTest {

    CodeInterpreter codeInterpreter;
    StateContainer stateContainer;
    HashMap<Integer, ParsedObject> instr;

    @BeforeEach
    public void setup() {
        codeInterpreter = new CodeInterpreter();
        stateContainer = new StateContainer();
        codeInterpreter.stateContainer = stateContainer;
        instr = new HashMap<>();
    }

    @Test
    public void convertMovToShiftTest() {
        ParsedInstruction add = new ParsedInstruction(Instruction.ADD, Condition.AL, false, null, null, "r0", "r0", null, null);
        instr.put(0, add);
        codeInterpreter.parsedObjects = new HashMap<>();
        codeInterpreter.parsedObjects.put(0, add);
        codeInterpreter.replaceMovShifts();
        assertEquals(instr, codeInterpreter.parsedObjects);
        ParsedInstruction mov = new ParsedInstruction(Instruction.MOV, Condition.EQ, true, null, null, "r1", "r0", null, null);
        instr.put(1, mov);
        codeInterpreter.parsedObjects.put(1, mov);
        codeInterpreter.replaceMovShifts();
        assertEquals(instr, codeInterpreter.parsedObjects);
        ParsedInstruction movShift = new ParsedInstruction(Instruction.MOV, Condition.AL, true, null, null, "r1", "r0", "LSL#5", null);
        ParsedInstruction Shift = new ParsedInstruction(Instruction.LSL, Condition.AL, true, null, null, "r1", "r0", "#5", null);
        instr.put(2, Shift);
        codeInterpreter.parsedObjects.put(2, movShift);
        codeInterpreter.replaceMovShifts();
        assertEquals(instr, codeInterpreter.parsedObjects);
    }

}
