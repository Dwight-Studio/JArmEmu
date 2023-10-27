package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.asm.DataMode;
import fr.dwightstudio.jarmemu.asm.Instruction;
import fr.dwightstudio.jarmemu.asm.UpdateMode;
import fr.dwightstudio.jarmemu.sim.obj.ParsedInstruction;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.util.EnumUtils;
import org.fxmisc.richtext.CodeArea;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.regex.Pattern;

public class RegexSourceParser implements SourceParser{

    private static final String[] INSTRUCTIONS = EnumUtils.getFromEnum(Instruction.values(), false);
    private static final String[] CONDITIONS = EnumUtils.getFromEnum(Instruction.values(), false);
    private static final String[] DATA_MODES = EnumUtils.getFromEnum(DataMode.values(), false);
    private static final String[] UPDATE_MODES = EnumUtils.getFromEnum(UpdateMode.values(), false);

    private static final String INSTRUCTION_PATTERN = String.join("|", INSTRUCTIONS);
    private static final String CONDITION_PATTERN = String.join("|", CONDITIONS);
    private static final String FLAG_PATTERN = "S";
    private static final String DATA_PATTERN = String.join("|", DATA_MODES);
    private static final String UPDATE_PATTERN = String.join("|", UPDATE_MODES);
    private static final String ARG_PATTERN = "[^,\n]*|\\[[^,\n]*\\]|\\{[^,\n]*\\}";
    private static final String REST_PATTERN = "[^\n]*";

    private static final Pattern PATTERN = Pattern.compile(
            "(?i)"
            + "(?<INSTRUCTION>" + INSTRUCTION_PATTERN + ")"
            + "(?<CONDITION>" + CONDITION_PATTERN + ")"
            + "("
            + "(?<FLAG>" + FLAG_PATTERN + ")"
            + "|(?<DATA>" + DATA_PATTERN + ")"
            + "|(?<UPDATE>" + UPDATE_PATTERN + ")"
            + "|)"
            + "[ ]+(?<ARG1>" + ARG_PATTERN + ")[ ]*"
            + ",[ ]*(?<ARG2>" + ARG_PATTERN + ")[ ]*"
            + ",[ ]*(?<ARG3>" + ARG_PATTERN + ")[ ]*"
            + ",[ ]*(?<ARG4>" + ARG_PATTERN + ")[ ]*"
            + ",[ ]*(?<REST>" + REST_PATTERN + ")"
            + "(?-i)"
    );

    private SourceScanner sourceScanner;

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
     * Lecture du fichier et renvoie des instructions parsés à verifier
     *
     * @param stateContainer conteneur d'état sur lequel parser
     */
    @Override
    public HashMap<Integer, ParsedInstruction> parse(StateContainer stateContainer) {
        return null;
    }

    /**
     * Lecture d'une ligne et teste de tous ses arguments
     *
     * @param stateContainer
     * @return une ParsedInstruction à verifier.
     */
    @Override
    public ParsedInstruction parseOneLine(StateContainer stateContainer) {
        return null;
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
