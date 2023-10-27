package fr.dwightstudio.jarmemu.sim;

import fr.dwightstudio.jarmemu.sim.obj.ParsedInstruction;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

public interface SourceParser {

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
     * Lecture du fichier et renvoie des instructions parsés à verifier
     *
     * @param stateContainer conteneur d'état sur lequel parser
     */
    public HashMap<Integer, ParsedInstruction> parse(StateContainer stateContainer);

    /**
     * Lecture d'une ligne et teste de tous ses arguments
     * @return une ParsedInstruction à verifier.
     */
    public ParsedInstruction parseOneLine(StateContainer stateContainer);
}
