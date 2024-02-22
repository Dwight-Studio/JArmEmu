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

package fr.dwightstudio.jarmemu.asm.parser.regex;

import fr.dwightstudio.jarmemu.asm.Condition;
import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.OInstruction;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.parse.ParsedFile;
import fr.dwightstudio.jarmemu.sim.parse.ParsedInstruction;
import fr.dwightstudio.jarmemu.sim.parse.ParsedLabel;
import fr.dwightstudio.jarmemu.sim.parse.ParsedObject;
import fr.dwightstudio.jarmemu.util.EnumUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ASMParser {

    private static final String[] INSTRUCTIONS = EnumUtils.getFromEnum(OInstruction.values(), false);
    private static final String[] CONDITIONS = EnumUtils.getFromEnum(Condition.values(), true);
    private static final String[] DATA_MODES = EnumUtils.getFromEnum(DataMode.values(), false);
    private static final String[] UPDATE_MODES = EnumUtils.getFromEnum(UpdateMode.values(), false);

    private static final String INSTRUCTION_REGEX = String.join("|", INSTRUCTIONS);
    private static final String CONDITION_REGEX = String.join("|", CONDITIONS);
    private static final String FLAG_REGEX = "S";
    private static final String DATA_REGEX = String.join("|", DATA_MODES);
    private static final String UPDATE_REGEX = String.join("|", UPDATE_MODES);
    private static final String CONTENT_REGEX = "[^,\n\\[\\]\\{\\}]+";
    private static final String BRACKET_REGEX = "[^\n\\[\\]\\{\\}]+";
    private static final String ARG_REGEX = CONTENT_REGEX + "|\\[" + BRACKET_REGEX + "\\]!|\\[" + BRACKET_REGEX + "\\]|\\{" + BRACKET_REGEX + "\\}";

    private static final String COMPLETE_INSTRUCTION_REGEX =
            "(?<INSTRUCTION>" + INSTRUCTION_REGEX + ")"
            + "(?<CONDITION>" + CONDITION_REGEX + ")"
            + "("
            + "(?<FLAG>" + FLAG_REGEX + ")"
            + "|(?<DATA>" + DATA_REGEX + ")"
            + "|(?<UPDATE>" + UPDATE_REGEX + ")"
            + "|)"
            + "[ \t]+(?<ARG1>" + ARG_REGEX + ")[ \t]*"
            + "((,[ \t]*(?<ARG2>" + ARG_REGEX + ")[ \t]*)|)"
            + "((,[ \t]*(?<ARG3>" + ARG_REGEX + ")[ \t]*)|)"
            + "((,[ \t]*(?<ARG4>" + ARG_REGEX + ")[ \t]*)|)";

    private static final Pattern INSTRUCTION_PATTERN = Pattern.compile(
            "(?i)[ \t]*"
                    + COMPLETE_INSTRUCTION_REGEX
                    + "[ \t]*$(?-i)"
    );

    private static final String LABEL_REGEX = "[A-Za-z_0-9]+";

    private static final Pattern LABEL_PATTERN = Pattern.compile(
            "(?i)[ \t]*"
            + "(?<LABEL>" + LABEL_REGEX + ")[ \t]*:[ \t]*"
            + "((" + COMPLETE_INSTRUCTION_REGEX + ")|)"
            + "[ \t]*$(?-i)"
    );

    private int instructionPos;

    public ASMParser() {
        this.instructionPos = 0;
    }

    /**
     * Lecture d'une ligne avec assembler
     *
     * @param sourceScanner le SourceScanner associé
     * @param line la ligne à parser
     * @return un ParsedObject à verifier.
     */
    public ParsedObject parseOneLine(SourceScanner sourceScanner, String line) {
        OInstruction instruction;
        boolean updateFlags = false;
        DataMode dataMode = null;
        UpdateMode updateMode = null;
        Condition condition = Condition.AL;

        String arg1;
        String arg2;
        String arg3;
        String arg4;

        Matcher matcherInst = INSTRUCTION_PATTERN.matcher(line);
        Matcher matcherLabel = LABEL_PATTERN.matcher(line);

        if (matcherInst.find()) {
            String instructionString = matcherInst.group("INSTRUCTION");
            String conditionString = matcherInst.group("CONDITION");
            String flagString = matcherInst.group("FLAG");
            String dataString = matcherInst.group("DATA");
            String updateString = matcherInst.group("UPDATE");

            arg1 = matcherInst.group("ARG1");
            arg2 = matcherInst.group("ARG2");
            arg3 = matcherInst.group("ARG3");
            arg4 = matcherInst.group("ARG4");

            try {
                instruction = OInstruction.valueOf(instructionString.toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new SyntaxASMException("Unknown instruction '" + instructionString + "'").with(sourceScanner.getCurrentInstructionValue()).with(new ParsedFile(sourceScanner));
            }

            try {
                if (Objects.equals(conditionString, "")) conditionString = null;
                if (conditionString != null) condition = Condition.valueOf(conditionString.toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new SyntaxASMException("Unknown condition '" + conditionString + "'").with(sourceScanner.getCurrentInstructionValue()).with(new ParsedFile(sourceScanner));
            }

            if (flagString != null) updateFlags = flagString.equalsIgnoreCase("S");

            try {
                if (Objects.equals(dataString, "")) dataString = null;
                if (dataString != null) dataMode = DataMode.customValueOf(dataString.toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new SyntaxASMException("Unknown data mode '" + dataString + "'").with(sourceScanner.getCurrentInstructionValue()).with(new ParsedFile(sourceScanner));
            }

            try {
                if (Objects.equals(updateString, "")) updateString = null;
                if (updateString != null) updateMode = UpdateMode.valueOf(updateString.toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new SyntaxASMException("Unknown update mode '" + updateString + "'").with(sourceScanner.getCurrentInstructionValue()).with(new ParsedFile(sourceScanner));
            }

            instructionPos++;
        } else if (matcherLabel.find()) {
            return new ParsedLabel(matcherLabel.group("LABEL").strip().toUpperCase());
        } else {
            throw new SyntaxASMException("Unexpected statement '" + line + "'").with(sourceScanner.getCurrentInstructionValue()).with(new ParsedFile(sourceScanner));
        }

        if (arg1 != null) {
            arg1 = arg1.replaceAll("\\s+","");
            arg1 = arg1.replaceAll("''","' '");
            if (arg1.isEmpty()) arg1 = null;
        }

        if (arg2 != null) {
            arg2 = arg2.replaceAll("\\s+","");
            arg2 = arg2.replaceAll("''","' '");
            if (arg2.isEmpty()) arg2 = null;
        }

        if (arg3 != null) {
            arg3 = arg3.replaceAll("\\s+","");
            arg3 = arg3.replaceAll("''","' '");
            if (arg3.isEmpty()) arg3 = null;
        }

        if (arg4 != null) {
            arg4 = arg4.replaceAll("\\s+","");
            arg4 = arg4.replaceAll("''","' '");
            if (arg4.isEmpty()) arg4 = null;
        }

        ParsedInstruction parsedInstruction = new ParsedInstruction(instruction, condition, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4, sourceScanner.getFileIndex());

        if (matcherLabel.find()) {
            return new ParsedLabel(matcherLabel.group("LABEL").strip().toUpperCase()).withInstruction(parsedInstruction);
        } else {
            return parsedInstruction;
        }
    }
}
