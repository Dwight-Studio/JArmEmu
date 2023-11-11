package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.sim.exceptions.ExecutionASMException;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.AddressParser;
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParser;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ParsedInstruction extends ParsedObject {
    private final Logger logger = Logger.getLogger(getClass().getName());

    private final Instruction instruction;
    private final String[] originalArgs;
    private final String[] processedArgs;
    private final Condition condition;
    private final boolean updateFlags;
    private final DataMode dataMode;
    private final UpdateMode updateMode;

    private final Pattern PSEUDO_OP_PATTERN = Pattern.compile("=(?<VALUE>[^\n\\[\\]\\{\\}]+)");

    public ParsedInstruction(@NotNull Instruction instruction, @NotNull Condition conditionExec, boolean updateFlags, DataMode dataMode, UpdateMode updateMode, String arg1, String arg2, String arg3, String arg4) {
        this.instruction = instruction;
        this.condition = conditionExec;
        this.updateFlags = updateFlags;
        this.dataMode = dataMode;
        this.updateMode = updateMode;
        this.originalArgs = new String[]{arg1, arg2, arg3, arg4};
        this.processedArgs = new String[]{arg1, arg2, arg3, arg4};
    }

    public SyntaxASMException verify(int line, Supplier<StateContainer> stateSupplier) {
        StateContainer stateContainer = stateSupplier.get();

        try {
            execute(stateContainer);
        } catch (SyntaxASMException exception) {
            AddressParser.reset(stateContainer);
            return exception.with(line).with(this);
        } catch (ExecutionASMException ignored) {
            // On ignore
        } finally {
            AddressParser.reset(stateContainer);
        }
        return null;
    }

    public void execute(StateContainer stateContainer) {
        if (instruction.hasDomReg()) {
            ArgumentParser[] argParsers = instruction.getArgParsers();
            Object[] parsedArgs = new Object[4];

            try {
                for (int i = 0; i < 4; i++) {
                    if (processedArgs[i] != null) {
                        parsedArgs[i] = argParsers[i].parse(stateContainer, processedArgs[i].toUpperCase());
                    } else {
                        parsedArgs[i] = argParsers[i].none();
                    }
                }
            } catch (SyntaxASMException exception) {
                try {
                    for (int i = 1; i < 4; i++) {
                        if (processedArgs[i-1] != null) {
                            parsedArgs[i] = argParsers[i].parse(stateContainer, processedArgs[i-1].toUpperCase());
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
                if (processedArgs[i] != null) {
                    parsedArgs[i] = argParsers[i].parse(stateContainer, processedArgs[i].toUpperCase());
                } else {
                    parsedArgs[i] = argParsers[i].none(i + 1);
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
            if (pInst.processedArgs[i] == null) {
                if (this.processedArgs[i] != null) {
                    if (VERBOSE) logger.info("Difference: Arg" + (1 + i) + " (Null)");
                    return false;
                }
            } else {
                if (!(pInst.processedArgs[i].equalsIgnoreCase(this.processedArgs[i]))) {
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

    public ParsedDirectivePack convertValueToDirective(StateContainer stateContainer) {
        ParsedDirectivePack pack = new ParsedDirectivePack();
        for (int i = 0; i < originalArgs.length; i++) {
            if (originalArgs[i] != null) {
                String valueString = originalArgs[i];
                processedArgs[i] = PSEUDO_OP_PATTERN.matcher(valueString).replaceAll(matchResult -> {
                    int value = stateContainer.evalWithAll(matchResult.group("VALUE"));
                    ParsedDirective dir = new ParsedDirective(Directive.WORD, Integer.toString(value));
                    String hash = RandomStringUtils.randomAlphabetic(10).toUpperCase();
                    dir.setGenerated(hash.strip());
                    pack.add(dir);
                    return "*" + hash;
                });
            }
        }

        return pack;
    }

    @Override
    public String toString() {
        return instruction.name();
    }
}
