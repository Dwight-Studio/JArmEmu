package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.sim.parse.ParsedInstruction;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.ParsedObject;

import java.util.HashMap;

public interface SourceParser {

    int DEFAULT_SOURCE_PARSER = 0;

    /**
     * @return le CodeScanner utilisé par le parseur
     */
    public SourceScanner getSourceScanner();

    /**
     * Définie le CodeScanner à utiliser par le parseur
     * @param sourceScanner le CodeScanner à utiliser
     */
    public void setSourceScanner(SourceScanner sourceScanner);

    /**
     * Méthode principale
     * Lecture du fichier et renvoie des objets parsés non vérifiés
     */
    public HashMap<Integer, ParsedObject> parse();

    /**
     * Lecture d'une ligne et teste de tous ses arguments
     *
     * @return un ParsedObject non vérifié
     */
    public ParsedObject parseOneLine();
}
