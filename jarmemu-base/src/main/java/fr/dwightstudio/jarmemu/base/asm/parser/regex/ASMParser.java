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

import fr.dwightstudio.jarmemu.base.asm.Instruction;
import fr.dwightstudio.jarmemu.base.asm.ParsedFile;
import fr.dwightstudio.jarmemu.base.asm.ParsedLabel;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.base.asm.modifier.*;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.SourceScanner;
import fr.dwightstudio.jarmemu.base.util.EnumUtils;
import fr.dwightstudio.jarmemu.base.util.ModifierUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ASMParser {

    public static final Map<Class<? extends Enum<? extends ModifierParameter>>, Map<String, ? extends ModifierParameter>> MODIFIER_PARAMETERS = new HashMap<>();

    static {
        MODIFIER_PARAMETERS.put(Condition.class, EnumUtils.valuesToMap(Condition.values(), true));
        MODIFIER_PARAMETERS.put(UpdateFlags.class, EnumUtils.valuesToMap(UpdateFlags.values(), true));
        MODIFIER_PARAMETERS.put(DataMode.class, EnumUtils.valuesToMap(DataMode.values(), true));
        MODIFIER_PARAMETERS.put(UpdateMode.class, EnumUtils.valuesToMap(UpdateMode.values(), false));
    }

    public static final int INSTRUCTION_NUMBER = Instruction.values().length;
    public static final String[] INSTRUCTIONS;
    public static final String INSTRUCTION_REGEX;

    static {
        StringBuilder buffer = new StringBuilder();
        ArrayList<String> instructions = new ArrayList<>();

        Instruction[] values = Instruction.values();
        Arrays.sort(values, Comparator.comparingInt(i -> -i.toString().length()));

        for (int i = 0; i < INSTRUCTION_NUMBER; i++) {
            Instruction instruction = values[i];
            if (instruction.isValid()) {
                ModifierUtils.PossibleModifierIterator iterator = new ModifierUtils.PossibleModifierIterator(instruction.getModifierParameterClasses());
                buffer.append("(?<INS").append(i).append(">").append(instruction).append(")(?<MOD").append(i).append(">");
                while (iterator.hasNext()) {
                    String modifier = iterator.next().toString();
                    instructions.add(instruction + modifier);
                    buffer.append(modifier.isBlank() ? "" : modifier);
                    if (iterator.hasNext()) buffer.append("|");
                }
                buffer.append(")");
            } else {
                instructions.add(instruction.toString());
                buffer.append("(?<INS").append(i).append(">").append(instruction).append(")").append("(?<MOD").append(i).append(">[a-zA-Z0-9]*)");
            }
            buffer.append("|");
        }

        buffer.deleteCharAt(buffer.length() - 1);

        INSTRUCTIONS = instructions.toArray(new String[0]);
        INSTRUCTION_REGEX = buffer.toString();
    }

    public static final String CONTENT_REGEX = "[^,\n\\[\\]\\{\\}]+";
    public static final String BRACKET_REGEX = "[^\n\\[\\]\\{\\}]+";
    public static final String ARG_REGEX = CONTENT_REGEX + "|\\[" + BRACKET_REGEX + "\\]!|\\[" + BRACKET_REGEX + "\\]|\\{" + BRACKET_REGEX + "\\}";

    public static final String COMPLETE_INSTRUCTION_REGEX = "(?<INSTRUCTION>" + INSTRUCTION_REGEX + ")"
            + "(([ \t]+(?<ARG1>" + ARG_REGEX + ")[ \t]*)|)"
            + "((,[ \t]*(?<ARG2>" + ARG_REGEX + ")[ \t]*)|)"
            + "((,[ \t]*(?<ARG3>" + ARG_REGEX + ")[ \t]*)|)"
            + "((,[ \t]*(?<ARG4>" + ARG_REGEX + ")[ \t]*)|)";

    public static final String LABEL_REGEX = "[A-Za-z_]+[A-Za-z_0-9]*";

    public static final Pattern INSTRUCTION_PATTERN = Pattern.compile(
            "(?i)[ \t]*"
                    + "(((?<LABEL>" + LABEL_REGEX + ")[ \t]*:)|)[ \t]*"
                    + "((" + COMPLETE_INSTRUCTION_REGEX + ")|)"
                    + "[ \t]*$(?-i)"
    );

    /**
     * Lecture d'une ligne avec assembler
     *
     * @param parsedFile le fichier à analyser
     */
    protected static void parseOneLine(RegexSourceParser parser, String line, SourceScanner sourceScanner, ParsedFile parsedFile) throws ASMException {
        Instruction instruction;
        Modifier modifier = new Modifier(Condition.AL);

        String arg1;
        String arg2;
        String arg3;
        String arg4;

        Matcher matcherInst = INSTRUCTION_PATTERN.matcher(line);

        if (matcherInst.find()) {
            String instructionString = matcherInst.group("INSTRUCTION");
            String labelString = matcherInst.group("LABEL");

            if ((labelString == null || labelString.isEmpty()) && (instructionString == null || instructionString.isEmpty()))
                throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.parser.unexpectedStatement", line)).with(sourceScanner.getLineNumber()).with(parsedFile);

            if (labelString != null && !labelString.isEmpty()) {
                parsedFile.add(new ParsedLabel(parser.currentSection, labelString).withLineNumber(sourceScanner.getLineNumber()));
            }

            instructionString = null;
            String modifierString = null;

            for (int i = 0; i < INSTRUCTION_NUMBER; i++) {
                if (matcherInst.group("INS" + i) != null) {
                    instructionString = matcherInst.group("INS" + i);
                    modifierString = matcherInst.group("MOD" + i);
                    break;
                }
            }

            if (instructionString != null && !instructionString.isEmpty()) {
                arg1 = matcherInst.group("ARG1");
                arg2 = matcherInst.group("ARG2");
                arg3 = matcherInst.group("ARG3");
                arg4 = matcherInst.group("ARG4");

                try {
                    instruction = Instruction.valueOf(instructionString.toUpperCase());
                } catch (IllegalArgumentException exception) {
                    throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.parser.unknownInstruction", instructionString)).with(sourceScanner.getLineNumber()).with(parsedFile);
                }


                // Evaluate modifier
                if (modifierString != null && !modifierString.isEmpty()) {
                    String tmp = modifierString.toUpperCase();

                    for (Class<? extends Enum<? extends ModifierParameter>> clazz : instruction.getModifierParameterClasses()) {
                        if (tmp.isEmpty()) break;

                        Map<String, ? extends ModifierParameter> modifierParameters = MODIFIER_PARAMETERS.get(clazz);

                        boolean found = false;
                        for (int i = 1; i <= tmp.length(); i++) {
                            ModifierParameter modifierParameter = modifierParameters.get(tmp.substring(0, i));

                            if (modifierParameter != null) {
                                modifier = modifier.with(modifierParameter);
                                tmp = tmp.substring(i);
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            found = modifierParameters.containsKey("");
                        }

                        if (!found)
                            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.parser.unknownModifier", tmp, clazz.getSimpleName()));
                    }

                    if (!tmp.isEmpty())
                        throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.parser.unexpectedModifier", tmp));
                }

                arg1 = cleanArgument(arg1);
                arg2 = cleanArgument(arg2);
                arg3 = cleanArgument(arg3);
                arg4 = cleanArgument(arg4);

                parsedFile.add(instruction.create(modifier, arg1, arg2, arg3, arg4).withLineNumber(sourceScanner.getLineNumber()));
            }
        } else {
            throw new SyntaxASMException(JArmEmuApplication.formatMessage("%exception.parser.unexpectedStatement", line)).with(sourceScanner.getLineNumber()).with(parsedFile);
        }
    }

    private static @Nullable String cleanArgument(String arg1) {
        if (arg1 != null) {
            arg1 = arg1.replaceAll("\\s+", "");
            arg1 = arg1.replaceAll("''", "' '");
            if (arg1.isEmpty()) arg1 = null;
        }
        return arg1;
    }
}
