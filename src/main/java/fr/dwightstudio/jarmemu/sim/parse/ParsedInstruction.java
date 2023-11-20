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

package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.sim.exceptions.ExecutionASMException;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.args.AddressParser;
import fr.dwightstudio.jarmemu.sim.parse.args.ArgumentParser;
import fr.dwightstudio.jarmemu.sim.parse.args.ShiftParser;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.regex.Matcher;
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
            execute(stateContainer, true);
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

    /**
     * Exécute l'instruction sur le conteneur d'état
     *
     * @apiNote Si l'exécution échoue et que l'instruction possède un registre dominant, on essaye d'exécuter en décalant les arguments
     *
     * @param stateContainer le conteneur d'état sur lequel exécuter
     * @param forceExecution ignore les erreurs d'exécution non bloquantes
     */
    public void execute(StateContainer stateContainer, boolean forceExecution) throws ExecutionASMException {
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

            instruction.execute(stateContainer, forceExecution, condition, updateFlags, dataMode, updateMode, parsedArgs[0], parsedArgs[1], parsedArgs[2], parsedArgs[3]);
        } else {
            ArgumentParser[] argParsers = instruction.getArgParsers();
            Object[] parsedArgs = new Object[4];

            for (int i = 0; i < 4; i++) {
                if (processedArgs[i] != null) {
                    if (processedArgs[i].contains("'")) {
                        parsedArgs[i] = argParsers[i].parse(stateContainer, processedArgs[i]);
                    } else {
                        parsedArgs[i] = argParsers[i].parse(stateContainer, processedArgs[i].toUpperCase());
                    }
                } else {
                    parsedArgs[i] = argParsers[i].none(i + 1);
                }
            }

            instruction.execute(stateContainer, forceExecution, condition, updateFlags, dataMode, updateMode, parsedArgs[0], parsedArgs[1], parsedArgs[2], parsedArgs[3]);
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
                if (!(pInst.processedArgs[i].equals(this.processedArgs[i]))) {
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

    /**
     * Applique les pseudo-instructions en générant des processedArgs
     *
     * @param stateContainer le conteur d'état sur lequel appliquer les pseudo-instructions
     * @return un pack de directive contenant les directives générées
     */
    public ParsedDirectivePack convertValueToDirective(StateContainer stateContainer) {
        ParsedDirectivePack pack = new ParsedDirectivePack();
        for (int i = 0; i < originalArgs.length; i++) {
            if (originalArgs[i] != null) {
                processedArgs[i] = PSEUDO_OP_PATTERN.matcher(originalArgs[i]).replaceAll(matchResult -> {
                    int value = stateContainer.evalWithAll(matchResult.group("VALUE"));
                    ParsedDirective dir = new ParsedDirective(Directive.WORD, Integer.toString(value), Section.RODATA);
                    String hash = RandomStringUtils.randomAlphabetic(10).toUpperCase();
                    dir.setGenerated(hash.strip());
                    pack.add(dir);
                    return "*" + hash;
                });
            }
        }

        return pack;
    }

    /**
     * @return vrai si l'instruction est une pseudo-instruction
     */
    public boolean isPseudoInstruction() {
        for (String originalArg : originalArgs) {
            if (originalArg != null) {
                if (PSEUDO_OP_PATTERN.matcher(originalArg).matches()) {
                    return true;
                }
            }
        }

        return false;
    }

    public ParsedInstruction convertMovToShift(StateContainer stateContainer) {
        if (this.instruction == Instruction.MOV) {
            ArgumentParser[] argParsers = instruction.getArgParsers();
            if (originalArgs[2] == null) return this;
            if (argParsers[2].parse(stateContainer, originalArgs[2].toUpperCase()) instanceof ShiftParser.ShiftFunction){
                try {
                    return new ParsedInstruction(Instruction.valueOf(originalArgs[2].substring(0, 3).toUpperCase()), this.condition, this.updateFlags, this.dataMode, this.updateMode, originalArgs[0], originalArgs[1], originalArgs[2].substring(3), null);
                } catch (Exception e) {
                    logger.severe(e.getMessage());
                }
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return instruction.name();
    }
}
