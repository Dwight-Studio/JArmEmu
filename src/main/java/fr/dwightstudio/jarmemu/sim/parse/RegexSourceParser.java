package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.sim.parse.regex.ASMParser;
import fr.dwightstudio.jarmemu.sim.parse.regex.PseudoInstructionParser;
import fr.dwightstudio.jarmemu.sim.parse.regex.SectionParser;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class RegexSourceParser implements SourceParser {

    private SourceScanner sourceScanner;
    protected Section currentSection;

    public RegexSourceParser(SourceScanner sourceScanner) {
        this.sourceScanner = sourceScanner;
        currentSection = Section.NONE;
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
        currentSection = Section.NONE;
    }

    /**
     * Méthode principale
     * Lecture du fichier et renvoie des objets parsés non vérifiés
     */
    @Override
    public HashMap<Integer, ParsedObject> parse() {
        HashMap<Integer, ParsedObject> rtn = new HashMap<>();

        sourceScanner.goTo(-1);
        currentSection = Section.NONE;
        while (this.sourceScanner.hasNextLine()){
            ParsedObject inst = parseOneLine();
            if (inst != null) rtn.put(sourceScanner.getCurrentInstructionValue(), inst);
        }

        return rtn;
    }

    /**
     * Lecture d'une ligne et teste de tous ses arguments
     *
     * @return un ParsedObject non vérifié
     */
    @Override
    public ParsedObject parseOneLine() {
        String line = sourceScanner.nextLine();
        line = prepare(line);

        if (line.isEmpty()) return null;

        Section section = SectionParser.parseOneLine(sourceScanner, line);
        if (section != null) {
            this.currentSection = section;
            return null;
        }
        ParsedObject instruction = PseudoInstructionParser.parseOneLine(sourceScanner, line, section);
        if (instruction != null) return instruction;
        if (currentSection == Section.TEXT) return ASMParser.parseOneLine(sourceScanner, line);
        return null;
    }

    /**
     * Prépare la ligne pour le parsage
     *
     * @param line la ligne à préparer
     * @return la ligne sans commentaire ou blancs
     */
    public static String prepare(@NotNull String line) {
        return line.split("@")[0].strip();
    }
}
