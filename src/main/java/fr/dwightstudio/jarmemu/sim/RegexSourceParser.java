package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.ParsedInstruction;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.util.EnumUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexSourceParser implements SourceParser{

    private static final String[] INSTRUCTIONS = EnumUtils.getFromEnum(Instruction.values(), false);
    private static final String[] CONDITIONS = EnumUtils.getFromEnum(Instruction.values(), false);
    private static final String[] DATA_MODES = EnumUtils.getFromEnum(DataMode.values(), false);
    private static final String[] UPDATE_MODES = EnumUtils.getFromEnum(UpdateMode.values(), false);

    private static final String INSTRUCTION_REGEX = String.join("|", INSTRUCTIONS);
    private static final String CONDITION_REGEX = String.join("|", CONDITIONS);
    private static final String FLAG_REGEX = "S";
    private static final String DATA_REGEX = String.join("|", DATA_MODES);
    private static final String UPDATE_REGEX = String.join("|", UPDATE_MODES);
    private static final String ARG_REGEX = "[^,\n]*|\\[[^,\n]*\\]|\\{[^,\n]*\\}";
    private static final String REST_REGEX = "[^\n]*";

    private static final Pattern INSTRUCTION_PATTERN = Pattern.compile(
            "(?i)[ \t]*"
            + "(?<INSTRUCTION>" + INSTRUCTION_REGEX + ")"
            + "(?<CONDITION>" + CONDITION_REGEX + ")"
            + "("
            + "(?<FLAG>" + FLAG_REGEX + ")"
            + "|(?<DATA>" + DATA_REGEX + ")"
            + "|(?<UPDATE>" + UPDATE_REGEX + ")"
            + "|)"
            + "[ ]+(?<ARG1>" + ARG_REGEX + ")[ ]*"
            + ",[ ]*(?<ARG2>" + ARG_REGEX + ")[ ]*"
            + ",[ ]*(?<ARG3>" + ARG_REGEX + ")[ ]*"
            + ",[ ]*(?<ARG4>" + ARG_REGEX + ")[ ]*"
            + ",[ ]*(?<REST>" + REST_REGEX + ")"
            + "(?-i)"
    );
    
    private static final String LABEL_REGEX = "([A-Za-z_0-9]+):";

    private static final Pattern LABEL_PATTERN = Pattern.compile(
            "(?<LABEL>" + LABEL_REGEX + ")"
    );

    private SourceScanner sourceScanner;

    public RegexSourceParser(SourceScanner sourceScanner) {
        this.sourceScanner = sourceScanner;
    }

    /**
     * @return le CodeScanner utilisé par le parseur
     */
    @Override
    public SourceScanner getSourceScanner() {
        return sourceScanner;
    }

    /**
     * Définie le CodeScanner à utiliser par le parseur
     *
     * @param sourceScanner le CodeScanner à utiliser
     */
    @Override
    public void setSourceScanner(SourceScanner sourceScanner) {
        this.sourceScanner = sourceScanner;
    }

    /**
     * Méthode principale
     * Lecture du fichier et renvoie des instructions parsées à verifier
     *
     * @param stateContainer conteneur d'état sur lequel parser
     */
    @Override
    public HashMap<Integer, ParsedInstruction> parse(StateContainer stateContainer) {
        HashMap<Integer, ParsedInstruction> rtn = new HashMap<>();

        sourceScanner.goTo(-1);
        while (this.sourceScanner.hasNextLine()){
            ParsedInstruction inst = parseOneLine(stateContainer);
            if (inst != null) rtn.put(sourceScanner.getCurrentInstructionValue(), inst);
        }

        return rtn;
    }

    /**
     * Lecture d'une ligne et teste de tous ses arguments
     *
     * @param stateContainer
     * @return une ParsedInstruction à verifier.
     */
    @Override
    public ParsedInstruction parseOneLine(StateContainer stateContainer) {
        Instruction instruction = null;
        boolean updateFlags = false;
        DataMode dataMode = null;
        UpdateMode updateMode = null;
        Condition condition = Condition.AL;

        String arg1;
        String arg2;
        String arg3;
        String arg4;


        String line = sourceScanner.nextLine();
        line = removeComments(line);
        line = removeBlanks(line);

        Matcher matcher = LABEL_PATTERN.matcher(line);

        if (matcher.find()) {
            return ParsedInstruction.ofLabel("", sourceScanner.getCurrentInstructionValue());
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
                instruction = Instruction.valueOf(instructionString);
            } catch (IllegalArgumentException exception) {
                throw new SyntaxASMException("Unknown instruction '" + instructionString + "'");
            }

            try {
                if (conditionString != null) condition = Condition.valueOf(conditionString);
            } catch (IllegalArgumentException exception) {
                throw new SyntaxASMException("Unknown condition '" + conditionString + "'");
            }

            if (updateString != null) updateFlags = flagString.equalsIgnoreCase("S");

            try {
                if (dataString != null) dataMode = DataMode.valueOf(conditionString);
            } catch (IllegalArgumentException exception) {
                throw new SyntaxASMException("Unknown data mode '" + dataString + "'");
            }

        } else {
            // TODO: Faire les Pseudo-OP
            return null;
        }

        return new ParsedInstruction(instruction, condition, updateFlags, dataMode, updateMode, arg1.strip(), arg2.strip(), arg3.strip(), arg4.strip());
    }

    /**
     * Retire le commentaire de la ligne s'il y en a un
     * @param line La ligne sur laquelle on veut appliquer la fonction
     * @return La ligne modifiée ou non
     */
    public String removeComments(@NotNull String line){
        return line.split("@")[0];
    }

    /**
     * Retire les espaces blancs avant et après l'instruction
     * @param line La ligne sur laquelle on veut appliquer la fonction
     * @return La ligne modifiée ou non
     */
    public String removeBlanks(@NotNull String line){
        return line.strip();
    }
}
