package fr.dwightstudio.jarmemu.asm;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Scanner;
import java.util.logging.Logger;

public class SourceReader {

    private final Scanner scanner;
    private final Logger logger = Logger.getLogger(getClass().getName());
    protected Instruction instruction;
    protected boolean updateFlags;
    protected boolean isHalfWord;
    protected boolean isByte;
    protected UpdateMode updateMode;
    protected Condition conditionExec;
    protected String currentLine;
    protected String instructionString;

    /**
     * Création du lecteur du fichier *.s
     * @param fileName L'adresse où se trouve le fichier
     * @throws FileNotFoundException Exception si le fichier n'est pas trouvé
     */
    public SourceReader(URI fileName) throws FileNotFoundException {
        File file = new File(fileName);
        this.scanner = new Scanner(file);
        this.instruction = null;
        this.updateFlags = false;
        this.isHalfWord = false;
        this.isByte = false;
        this.updateMode = null;
        this.conditionExec = null;
        this.currentLine = "";
        this.instructionString = "";
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
            isHalfWord = true;
            instructionString = instructionString.substring(0, instructionString.length()-1);
        } else if (instructionString.endsWith("B")) {
            isByte = true;
            instructionString = instructionString.substring(0, instructionString.length()-1);
        } else if (instructionString.length()==7 || instructionString.length()==5) {
            UpdateMode[] updateModes = UpdateMode.values();
            for (UpdateMode updatemode:updateModes) {
                if (instructionString.endsWith(updatemode.toString().toUpperCase())) updateMode = updatemode;
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

                conditionExec = condition;
                instructionString = instructionString.substring(instructionString.length()-2);
            }
        }
        return instructionString;
    }

    /**
     * Méthode principale
     * Lecture du fichier et envoie des instructions
     */
    public void read(){
        while (this.scanner.hasNextLine()){
            readOneLine();
        }
    }

    /**
     * Copie de la méthode principale pour une seule ligne
     * @return Renvoie la ligne modifiée
     */
    public void readOneLine(){
            this.instruction = null;
            this.updateFlags = false;
            this.isHalfWord = false;
            this.isByte = false;
            this.updateMode = null;
            this.conditionExec = null;

            currentLine = this.scanner.nextLine();
            currentLine = this.removeComments(currentLine);
            currentLine = this.removeBlanks(currentLine);
            currentLine = currentLine.toUpperCase();

            instructionString = currentLine.split(" ")[0];
            instructionString = this.removeFlags(instructionString);
            instructionString = this.removeCondition(instructionString);

            Instruction[] instructions = Instruction.values();
            for (Instruction instruction:instructions) {
                if(instruction.toString().toUpperCase().equals(instructionString)) this.instruction = instruction;
            }

            if (this.instruction == null) throw new IllegalStateException("Unknown instruction : " + instructionString);
    }

}
