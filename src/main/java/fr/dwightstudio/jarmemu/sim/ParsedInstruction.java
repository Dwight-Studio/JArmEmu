package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.asm.args.AddressParser;
import fr.dwightstudio.jarmemu.asm.args.ArgumentParser;
import fr.dwightstudio.jarmemu.asm.args.RegisterWithUpdateParser;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ParsedInstruction {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private final Instruction instruction;
    private final String[] args;
    private Condition condition;
    private boolean updateFlags = false;
    private DataMode dataMode = null;
    private UpdateMode updateMode = null;
    private boolean label = false;

    public static ParsedInstruction ofLabel(String name, int line) {
        ParsedInstruction inst = new ParsedInstruction(null, null, false, null, null, name, Integer.toString(line), "", "");
        inst.label = true;
        return inst;
    }

    public ParsedInstruction(Instruction instruction, Condition conditionExec, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) {
        this.instruction = instruction;
        this.condition = conditionExec;
        this.updateFlags = updateFlags;
        this.dataMode = dataMode;
        this.updateMode = updateMode;
        this.args = new String[]{arg1, arg2, arg3, arg4};
    }

    public AssemblyError verify(int line) {
        if (label) return null;
        StateContainer stateContainer = new StateContainer();
        try {
            execute(stateContainer);
            return null;
        } catch (AssemblySyntaxException exception) {
            return new AssemblyError(line, exception);
        } finally {
            AddressParser.reset(stateContainer);
            RegisterWithUpdateParser.reset(stateContainer);
        }
    }

    public void execute(StateContainer stateContainer) {
        if (label) {
            stateContainer.labels.put(args[0], Integer.valueOf(args[1]));
        } else {
            ArgumentParser[] argParsers = instruction.getArgParsers();
            Object[] parsedArgs = new Object[4];

            for (int i = 0; i < 4; i++) {
                if (args[i] != null) {
                    parsedArgs[i] = argParsers[i].parse(stateContainer, args[i]);
                } else {
                    parsedArgs[i] = argParsers[i].none();
                }
            }

            instruction.execute(stateContainer, condition, updateFlags, dataMode, updateMode, parsedArgs[0], parsedArgs[1], parsedArgs[2], parsedArgs[3]);
        }
    }

    public boolean isLabel() {
        return label;
    }
}
