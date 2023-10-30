package fr.dwightstudio.jarmemu.sim.parse.regex;

import fr.dwightstudio.jarmemu.asm.Condition;
import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.Instruction;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.sim.parse.ParsedInstruction;
import fr.dwightstudio.jarmemu.sim.parse.ParsedLabel;
import fr.dwightstudio.jarmemu.sim.parse.ParsedObject;
import fr.dwightstudio.jarmemu.util.EnumUtils;
import fr.dwightstudio.jarmemu.util.RegisterUtils;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexASMParser {

    private static final String[] INSTRUCTIONS = EnumUtils.getFromEnum(Instruction.values(), false);
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

    private static final Pattern INSTRUCTION_PATTERN = Pattern.compile(
            "(?i)^[ \t]*"
                    + "(?<INSTRUCTION>" + INSTRUCTION_REGEX + ")"
                    + "(?<CONDITION>" + CONDITION_REGEX + ")"
                    + "("
                    + "(?<FLAG>" + FLAG_REGEX + ")"
                    + "|(?<DATA>" + DATA_REGEX + ")"
                    + "|(?<UPDATE>" + UPDATE_REGEX + ")"
                    + "|)"
                    + "[ \t]+(?<ARG1>" + ARG_REGEX + ")[ \t]*"
                    + "((,[ \t]*(?<ARG2>" + ARG_REGEX + ")[ \t]*)|)"
                    + "((,[ \t]*(?<ARG3>" + ARG_REGEX + ")[ \t]*)|)"
                    + "((,[ \t]*(?<ARG4>" + ARG_REGEX + ")[ \t]*)|)"
                    + "[ \t]*$(?-i)"
    );

    private static final String LABEL_REGEX = "[A-Za-z_0-9]+";

    private static final Pattern LABEL_PATTERN = Pattern.compile(
            "(?<LABEL>" + LABEL_REGEX + ")[ \t]*:"
    );

    /**
     * Lecture d'une ligne avec assembler
     *
     * @param sourceScanner le SourceScanner associé
     * @param line la ligne à parser
     * @return un ParsedObject à verifier.
     */
    public static ParsedObject parseOneLine(SourceScanner sourceScanner, String line) {
        Instruction instruction;
        boolean updateFlags = false;
        DataMode dataMode = null;
        UpdateMode updateMode = null;
        Condition condition = Condition.AL;

        String arg1;
        String arg2;
        String arg3;
        String arg4;

        Matcher matcher = LABEL_PATTERN.matcher(line);

        if (matcher.find()) {
            return new ParsedLabel(matcher.group("LABEL").strip().toUpperCase(), RegisterUtils.lineToPC(sourceScanner.getCurrentInstructionValue()));
        }

        matcher = INSTRUCTION_PATTERN.matcher(line);

        if (matcher.find()) {
            String instructionString = matcher.group("INSTRUCTION");
            String conditionString = matcher.group("CONDITION");
            String flagString = matcher.group("FLAG");
            String dataString = matcher.group("DATA");
            String updateString = matcher.group("UPDATE");

            arg1 = matcher.group("ARG1");
            arg2 = matcher.group("ARG2");
            arg3 = matcher.group("ARG3");
            arg4 = matcher.group("ARG4");

            try {
                instruction = Instruction.valueOf(instructionString.toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new SyntaxASMException("Unknown instruction '" + instructionString + "' at line " + sourceScanner.getCurrentInstructionValue());
            }

            try {
                if (Objects.equals(conditionString, "")) conditionString = null;
                if (conditionString != null) condition = Condition.valueOf(conditionString.toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new SyntaxASMException("Unknown condition '" + conditionString + "' at line " + sourceScanner.getCurrentInstructionValue());
            }

            if (flagString != null) updateFlags = flagString.equalsIgnoreCase("S");

            try {
                if (Objects.equals(dataString, "")) dataString = null;
                if (dataString != null) dataMode = DataMode.customValueOf(dataString.toUpperCase().strip());
            } catch (IllegalArgumentException exception) {
                throw new SyntaxASMException("Unknown data mode '" + dataString + "' at line " + sourceScanner.getCurrentInstructionValue());
            }

            try {
                if (Objects.equals(updateString, "")) updateString = null;
                if (updateString != null) updateMode = UpdateMode.valueOf(updateString.toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new SyntaxASMException("Unknown update mode '" + updateString + "' at line " + sourceScanner.getCurrentInstructionValue());
            }

        } else {
            throw new SyntaxASMException("Unexpected statement '" + line + "', at line " + sourceScanner.getCurrentInstructionValue());
        }

        if (arg1 != null) {
            arg1 = arg1.strip().toUpperCase();
            if (arg1.isEmpty()) arg1 = null;
        }

        if (arg2 != null) {
            arg2 = arg2.strip().toUpperCase();
            if (arg2.isEmpty()) arg2 = null;
        }

        if (arg3 != null) {
            arg3 = arg3.strip().toUpperCase();
            if (arg3.isEmpty()) arg3 = null;
        }

        if (arg4 != null) {
            arg4 = arg4.strip().toUpperCase();
            if (arg4.isEmpty()) arg4 = null;
        }

        return new ParsedInstruction(instruction, condition, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }
}
