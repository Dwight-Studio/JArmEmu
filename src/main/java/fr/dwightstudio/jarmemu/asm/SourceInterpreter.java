package fr.dwightstudio.jarmemu.asm;

import fr.dwightstudio.jarmemu.asm.args.AddressParser;
import fr.dwightstudio.jarmemu.asm.args.ArgumentParser;
import fr.dwightstudio.jarmemu.asm.args.RegisterWithUpdateParser;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fxmisc.richtext.CodeArea;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SourceInterpreter {

    private final Logger logger = Logger.getLogger(getClass().getName());
    protected Instruction instruction;
    protected boolean updateFlags;
    protected CodeScanner codeScanner;

    protected DataMode dataMode;
    protected UpdateMode updateMode;
    protected Condition conditionExec;
    protected String currentLine;
    protected String instructionString;
    protected ArrayList<String> arguments;
    protected StateContainer stateContainer;

    /**
     * Création du lecteur de code du fichier *.s
     * @param file Le fichier
     * @throws FileNotFoundException Exception si le fichier n'est pas trouvé
     */
    public SourceInterpreter(File file) throws FileNotFoundException {

        updateFromFile(file);

        this.instruction = null;
        this.updateFlags = false;
        this.updateMode = null;
        this.conditionExec = null;
        this.currentLine = "";
        this.instructionString = "";
        this.arguments = new ArrayList<>();
        this.stateContainer = new StateContainer();
    }

    /**
     * Création du lecteur de code du l'éditeur
     * @param codeArea L'éditeyr depuis lequel récupérer le code
     */
    public SourceInterpreter(CodeArea codeArea) {

        updateFromEditor(codeArea);

        this.instruction = null;
        this.updateFlags = false;
        this.updateMode = null;
        this.conditionExec = null;
        this.currentLine = "";
        this.instructionString = "";
        this.arguments = new ArrayList<>();
    }

    /**
     * Crée le CodeScanner qui lira le code contenu dans l'éditeur
     */
    public void updateFromEditor(CodeArea codeArea) {
        this.codeScanner = new CodeScanner(codeArea.getText());
    }


    public void exportToEditor(CodeArea codeArea) {
        codeArea.clear();
        codeArea.insertText(0, codeScanner.exportCode());
    }

    /**
     * Crée le CodeScanner qui lira le code contenu dans le fichier
     */
    public void updateFromFile(File file) throws FileNotFoundException {
        this.codeScanner = new CodeScanner(file);
    }

    /**
     * Exporter le code dans un fichier
     */
    public void exportToFile(File savePath) throws FileNotFoundException {
        codeScanner.exportCodeToFile(savePath);
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
        } else if (instructionString.endsWith("B")) {
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
     * Lecture du fichier et envoie des instructions
     */
    public void read(){
        while (this.codeScanner.hasNextLine()){
            readOneLine();
            executeCurrentLine();
        }
    }

    /**
     * Lecture d'une ligne
     */
    public void readOneLine() throws IllegalStateException {
        this.instruction = null;
        this.updateFlags = false;
        this.updateMode = null;
        this.dataMode = null;
        this.conditionExec = null;
        this.arguments.clear();

        currentLine = this.codeScanner.nextLine();
        currentLine = this.removeComments(currentLine);
        currentLine = this.removeBlanks(currentLine);
        currentLine = currentLine.toUpperCase();

        // Remise à zéro des drapeaux de ligne des parsers
        AddressParser.reset(this.stateContainer);
        RegisterWithUpdateParser.reset(this.stateContainer);

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

        if (currentLine.contains("{")){
            StringBuilder argument = new StringBuilder(currentLine.substring(instructionLength).split(",", 2)[1].strip());
            argument.deleteCharAt(0);
            argument.deleteCharAt(argument.length() - 1);
            ArrayList<String> argumentArray = new ArrayList<>(Arrays.asList(argument.toString().split(",")));
            argumentArray.replaceAll(String::strip);
            argument = new StringBuilder(currentLine.substring(instructionLength).split(",")[0].strip() + "," + "{");
            for (String arg:argumentArray) {
                argument.append(arg).append(",");
            }
            argument.deleteCharAt(argument.length() - 1);
            argument.append("}");
            this.arguments.addAll(Arrays.asList(argument.toString().split(",", 2)));
            this.arguments.replaceAll(String::strip);
        } else if (!currentLine.endsWith(":")){
            this.arguments.addAll(Arrays.asList(currentLine.substring(instructionLength).split(",")));
            this.arguments.replaceAll(String::strip);
        }

    }

    /**
     * Envoie des instructions se trouvant sur la ligne courante
     */
    public void executeCurrentLine() {
        ArgumentParser[] argParsers = instruction.getArgParsers();
        Object[] parsedArgs = new Object[4];

        try {
            for (int i = 0; i < 4; i++) {
                try {
                    parsedArgs[i] = argParsers[i].parse(stateContainer, arguments.get(i));
                } catch (IndexOutOfBoundsException exception) {
                    parsedArgs[i] = argParsers[i].none();
                }
            }
        } catch (AssemblySyntaxException exception) {
            logger.log(Level.SEVERE, ExceptionUtils.getStackTrace(exception));
            // Erreur de syntaxe
        } catch (Exception exception) {
            logger.log(Level.SEVERE, ExceptionUtils.getStackTrace(exception));
            // Erreur fatale
        }

    }
}
