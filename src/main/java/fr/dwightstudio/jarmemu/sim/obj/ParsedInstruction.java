package fr.dwightstudio.jarmemu.sim.obj;

import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.asm.args.AddressParser;
import fr.dwightstudio.jarmemu.asm.args.ArgumentParser;
import fr.dwightstudio.jarmemu.asm.args.RegisterWithUpdateParser;
import fr.dwightstudio.jarmemu.asm.exceptions.BadArgumentsASMException;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;

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
        } catch (SyntaxASMException exception) {
            return new AssemblyError(line, exception);
        } finally {
            AddressParser.reset(stateContainer);
            RegisterWithUpdateParser.reset(stateContainer);
        }
    }

    public void execute(StateContainer stateContainer) {
        if (label) {
            stateContainer.labels.put(args[0], Integer.valueOf(args[1]));
        } else if (instruction.hasDomReg()) {
            ArgumentParser[] argParsers = instruction.getArgParsers();
            Object[] parsedArgs = new Object[4];

            try {
                for (int i = 0; i < 4; i++) {
                    if (args[i] != null) {
                        parsedArgs[i] = argParsers[i].parse(stateContainer, args[i]);
                    } else {
                        parsedArgs[i] = argParsers[i].none();
                    }
                }
            } catch (BadArgumentsASMException exception) {
                for (int i = 1; i < 4; i++) {
                    if (args[i-1] != null) {
                        parsedArgs[i] = argParsers[i].parse(stateContainer, args[i-1]);
                    } else {
                        parsedArgs[i] = argParsers[i].none();
                    }
                }
                parsedArgs[0] = parsedArgs[1];
            }

            instruction.execute(stateContainer, condition, updateFlags, dataMode, updateMode, parsedArgs[0], parsedArgs[1], parsedArgs[2], parsedArgs[3]);
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParsedInstruction pInst)) return false;

        if (!(pInst.label == this.label)) return false;

        if (!(pInst.updateFlags == this.updateFlags)) return false;

        if (!(pInst.dataMode == this.dataMode)) return false;

        if (!(pInst.updateMode == this.updateMode)) return false;

        if (!(pInst.condition == this.condition)) return false;

        for (int i = 0 ; i < 4 ; i++) {
            if (pInst.args[i] == null) {
                if (this.args[i] != null) return false;
            } else {
                if (!(pInst.args[i].equalsIgnoreCase(this.args[i]))) return false;
            }
        }

        return true;
    }
}
