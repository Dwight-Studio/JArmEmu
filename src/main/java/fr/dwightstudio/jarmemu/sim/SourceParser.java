package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.asm.*;
import org.fxmisc.richtext.CodeArea;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceParser {

    private final Logger logger = Logger.getLogger(getClass().getName());
    protected Instruction instruction;
    protected boolean updateFlags;
    protected SourceScanner sourceScanner;

    protected DataMode dataMode;
    protected UpdateMode updateMode;
    protected Condition conditionExec;
    protected String currentLine;
    protected String instructionString;
    protected ArrayList<String> arguments;

    /**
     * Création du lecteur de code du fichier *.s
     * @param file Le fichier
     * @throws FileNotFoundException Exception si le fichier n'est pas trouvé
     */
    public SourceParser(File file) throws FileNotFoundException {

        updateFromFile(file);

        this.instruction = null;
        this.updateFlags = false;
        this.updateMode = null;
        this.conditionExec = Condition.AL;
        this.currentLine = "";
        this.instructionString = "";
        this.arguments = new ArrayList<>();
    }

    /**
     * Création du lecteur de code de l'éditeur
     * @param codeArea L'éditeyr depuis lequel récupérer le code
     */
    public SourceParser(CodeArea codeArea) {

        updateFromEditor(codeArea);

        this.instruction = null;
        this.updateFlags = false;
        this.updateMode = null;
        this.conditionExec = Condition.AL;
        this.currentLine = "";
        this.instructionString = "";
        this.arguments = new ArrayList<>();
    }

    /**
     * Crée le SourceScanner qui lira le code contenu dans l'éditeur
     */
    public void updateFromEditor(CodeArea codeArea) {
        this.sourceScanner = new SourceScanner(codeArea.getText());
    }


    public void exportToEditor(CodeArea codeArea) {
        codeArea.clear();
        codeArea.insertText(0, sourceScanner.exportCode());
    }

    /**
     * Crée le SourceScanner qui lira le code contenu dans le fichier
     */
    public void updateFromFile(File file) throws FileNotFoundException {
        this.sourceScanner = new SourceScanner(file);
    }

    /**
     * @return la ligne actuellement interprétée
     */
    public int getCurrentLine() {
        return sourceScanner.getCurrentInstructionValue();
    }

    /**
     * Exporter le code dans un fichier
     */
    public void exportToFile(File savePath) throws FileNotFoundException {
        sourceScanner.exportCodeToFile(savePath);
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

    /**
     * Retire les drapeaux S, H, B et les "update modes"
     * @param instructionString La ligne à modifier
     * @return La ligne modifiée ou non
     */
    public String removeFlags(@NotNull String instructionString){
        if (instructionString.endsWith("S") && (instructionString.length()%2==0)){
            updateFlags = true;
            instructionString = instructionString.substring(0, instructionString.length()-1);
        } else if (instructionString.endsWith("H")) {
            dataMode = DataMode.HALF_WORD;
            instructionString = instructionString.substring(0, instructionString.length()-1);
        } else if (instructionString.endsWith("B") && ((!instructionString.equals("SUB") && !instructionString.equals("RSB")))) {
            dataMode = DataMode.BYTE;
            instructionString = instructionString.substring(0, instructionString.length()-1);
        } else if (instructionString.length()==7 || instructionString.length()==5) {
            UpdateMode[] updateModes = UpdateMode.values();
            for (UpdateMode updatemode:updateModes) {
                if (instructionString.endsWith(updatemode.toString().toUpperCase())) {
                    updateMode = updatemode;
                    instructionString = instructionString.substring(0, instructionString.length()-2);
                }
            }
        }
        return instructionString;
    }

    /**
     * Retire les conditions d'exécution
     * @param instructionString La ligne à modifier
     * @return La ligne modifiée ou non
     */
    public String removeCondition(String instructionString){
        Condition[] conditions = Condition.values();
        for (Condition condition:conditions) {
            if (instructionString.endsWith(condition.toString().toUpperCase())){
                if (instructionString.endsWith("MLAL")) continue;
                if (instructionString.endsWith("TEQ")) continue;

                this.conditionExec = condition;
                instructionString = instructionString.substring(0, instructionString.length()-2);
            }
        }
        return instructionString;
    }

    /**
     * Méthode principale
     * Lecture du fichier et renvoie des instructions parsées à verifier
     *
     * @param stateContainer conteneur d'état sur lequel parser
     */
    public HashMap<Integer, ParsedInstruction> parse(StateContainer stateContainer){
        HashMap<Integer, ParsedInstruction> rtn = new HashMap<>();

        sourceScanner.goTo(-1);
        while (this.sourceScanner.hasNextLine()){
            ParsedInstruction inst = parseOneLine(stateContainer);
            if (inst != null) rtn.put(sourceScanner.getCurrentInstructionValue(), inst);
        }

        return rtn;
    }

    /**
     * Lecture d'une ligne
     */
    public void readOneLineASM() {
        this.instruction = null;
        this.updateFlags = false;
        this.updateMode = null;
        this.dataMode = null;
        this.conditionExec = Condition.AL;
        this.arguments.clear();

        currentLine = this.sourceScanner.nextLine();
        currentLine = this.removeComments(currentLine);
        currentLine = this.removeBlanks(currentLine);
        currentLine = currentLine.toUpperCase();

        instructionString = currentLine.split(" ")[0];
        int instructionLength = instructionString.length();
        instructionString = this.removeFlags(instructionString);
        instructionString = this.removeCondition(instructionString);

        Instruction[] instructions = Instruction.values();
        for (Instruction instruction:instructions) {
            if(instruction.toString().toUpperCase().equals(instructionString)) this.instruction = instruction;
        }

        if(currentLine.endsWith(":")){
            this.arguments.add(currentLine);
        } else {
            if (this.instruction == null) throw new AssemblySyntaxException("Unknown instruction '" + instructionString + "'");
        }

        if (currentLine.contains("{")) { // On pouvait utiliser des regex
            StringBuilder argument = new StringBuilder(currentLine.substring(instructionLength).split(",", 2)[1].strip());
            argument.deleteCharAt(0);
            argument.deleteCharAt(argument.length() - 1);
            ArrayList<String> argumentArray = new ArrayList<>(Arrays.asList(argument.toString().split(",")));
            argumentArray.replaceAll(String::strip);
            argument = new StringBuilder(currentLine.substring(instructionLength).split(",")[0].strip() + "," + "{");
            for (String arg : argumentArray) {
                argument.append(arg).append(",");
            }
            argument.deleteCharAt(argument.length() - 1);
            argument.append("}");
            this.arguments.addAll(Arrays.asList(argument.toString().split(",", 2)));
            this.arguments.replaceAll(String::strip);
        } else if (currentLine.contains("[")) {
            Pattern addressPattern = Pattern.compile("([^\\[]*)(\\[[^\\]]*\\])([^\\n]*)");
            logger.info(addressPattern.pattern());
            logger.info("'" + currentLine + "'");
            Matcher matcher = addressPattern.matcher(currentLine);
            String before = matcher.group(0);
            String address = matcher.group(1);
            this.arguments.addAll(Arrays.asList(before.split(",")));
            this.arguments.add(address);
            if (matcher.groupCount() == 2) {
                this.arguments.addAll(Arrays.asList(matcher.group(2).split(",")));
            }
        } else if (!(currentLine.endsWith(":"))) {
            this.arguments.addAll(Arrays.asList(currentLine.substring(instructionLength).split(",")));
            this.arguments.replaceAll(String::strip);
        }

        if (arguments.size() > 4) throw new AssemblySyntaxException("Invalid instruction '" + currentLine + "' (too many arguments");
    }

    /**
     * Lecture d'une ligne et teste de tous ses arguments
     * @return une ParsedInstruction à verifier.
     */
    public ParsedInstruction parseOneLine(StateContainer stateContainer) {
        try {
            readOneLineASM();
        } catch (AssemblySyntaxException exception) {
            //TODO: Gérer les exceptions après avoir géré les Pseudo-OP
        }

        if (this.instruction == null) {
            if (!arguments.isEmpty() && arguments.get(0).endsWith(":")) {
                String str = arguments.get(0);
                return ParsedInstruction.ofLabel(str.substring(0, str.length()-1), getCurrentLine());
            }
        }

        String arg1 = null;
        String arg2 = null;
        String arg3 = null;
        String arg4 = null;

        try {
            arg1 = arguments.get(0);
            arg2 = arguments.get(1);
            arg3 = arguments.get(2);
            arg4 = arguments.get(3);
        } catch (IndexOutOfBoundsException ignored) {};

        return new ParsedInstruction(instruction, conditionExec, updateFlags, dataMode, updateMode, arg1, arg2, arg3, arg4);
    }
}
