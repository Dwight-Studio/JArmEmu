package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.sim.args.AddressParser;
import fr.dwightstudio.jarmemu.sim.args.ArgumentParser;
import fr.dwightstudio.jarmemu.sim.args.RegisterWithUpdateParser;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

public class ParsedInstruction extends ParsedObject {
    private final Logger logger = Logger.getLogger(getClass().getName());

    private final Instruction instruction;
    private final String[] args;
    private final Condition condition;
    private final boolean updateFlags;
    private final DataMode dataMode;
    private final UpdateMode updateMode;

    public ParsedInstruction(@NotNull Instruction instruction, @NotNull Condition conditionExec, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) {
        this.instruction = instruction;
        this.condition = conditionExec;
        this.updateFlags = updateFlags;
        this.dataMode = dataMode;
        this.updateMode = updateMode;
        this.args = new String[]{arg1, arg2, arg3, arg4};
    }

    public AssemblyError verify(int line, HashMap<String, Integer> labels) {
        StateContainer stateContainer = new StateContainer();

        stateContainer.labels.putAll(labels);

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
        if (instruction.hasDomReg()) {
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
            } catch (SyntaxASMException exception) {
                try {
                    for (int i = 1; i < 4; i++) {
                        if (args[i-1] != null) {
                            parsedArgs[i] = argParsers[i].parse(stateContainer, args[i-1]);
                        } else {
                            parsedArgs[i] = argParsers[i].none();
                        }
                    }
                    parsedArgs[0] = parsedArgs[1];
                } catch (SyntaxASMException ignored) {
                    throw exception;
                }
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ParsedInstruction pInst)) return false;

        if (!(pInst.updateFlags == this.updateFlags)) {
            if (VERBOSE) logger.info("Difference: Flags");
            return false;
        }

        if (pInst.dataMode == null) {
            if (!(this.dataMode == null)) {
                if (VERBOSE) logger.info("Difference: DataMode (Null)");
                return false;
            }
        } else {
            if (!(pInst.dataMode.equals(this.dataMode))) {
                if (VERBOSE) logger.info("Difference: DataMode");
                return false;
            }
        }

        if (pInst.updateMode == null) {
            if (!(this.updateMode == null)) {
                if (VERBOSE) logger.info("Difference: UpdateMode (Null)");
                return false;
            }
        } else {
            if (!(pInst.updateMode.equals(this.updateMode))) {
                if (VERBOSE) logger.info("Difference: UpdateMode");
                return false;
            }
        }

        if (!(pInst.condition == this.condition)) {
            if (VERBOSE) logger.info("Difference: Condition");
            return false;
        }

        for (int i = 0 ; i < 4 ; i++) {
            if (pInst.args[i] == null) {
                if (this.args[i] != null) {
                    if (VERBOSE) logger.info("Difference: Arg" + (1 + i) + " (Null)");
                    return false;
                }
            } else {
                if (!(pInst.args[i].equalsIgnoreCase(this.args[i]))) {
                    if (VERBOSE) logger.info("Difference: Arg" + (1 + i));
                    return false;
                }
            }
        }
        return true;
    }

    public Instruction getInstruction() {
        return instruction;
    }
}
