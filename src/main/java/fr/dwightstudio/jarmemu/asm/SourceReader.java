package fr.dwightstudio.jarmemu.asm;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Scanner;

public class SourceReader {

    private final Scanner scanner;

    /**
     * Création du lecteur du fichier *.s
     * @param fileName L'adresse où se trouve le fichier
     * @throws FileNotFoundException Exception si le fichier n'est pas trouvé
     */
    public SourceReader(URI fileName) throws FileNotFoundException {
        File file = new File(fileName);
        this.scanner = new Scanner(file);
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
     * Méthode principale
     * Lecture du fichier et envoie des instructions
     */
    public void read(){
        String line;
        while (this.scanner.hasNextLine()){
            line = this.scanner.nextLine();
            line = this.removeComments(line);
            line = this.removeBlanks(line);
        }
    }

    /**
     * Copie de la méthode principale pour une seule ligne
     * @return Renvoie la ligne modifiée
     */
    public String readOneLine(){
        String line;
        line = this.scanner.nextLine();
        line = this.removeComments(line);
        line = this.removeBlanks(line);
        return line;
    }

}
