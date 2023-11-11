package fr.dwightstudio.jarmemu.sim.parse;

import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.sim.parse.regex.ASMParser;
import fr.dwightstudio.jarmemu.sim.parse.regex.DirectiveParser;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class RegexSourceParser implements SourceParser {

    private SourceScanner sourceScanner;
    protected CurrentSection currentSection;
    private ASMParser asmParser;
    private DirectiveParser directiveParser;

    public RegexSourceParser(SourceScanner sourceScanner) {
        this.sourceScanner = sourceScanner;
        currentSection= new CurrentSection();

        asmParser = new ASMParser();
        directiveParser = new DirectiveParser();
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
        currentSection = new CurrentSection();
    }

    /**
     * Méthode principale
     * Lecture du fichier et renvoie des objets parsés non vérifiés
     */
    @Override
    public HashMap<Integer, ParsedObject> parse() {
        HashMap<Integer, ParsedObject> rtn = new HashMap<>();
        asmParser = new ASMParser();
        directiveParser = new DirectiveParser();

        sourceScanner.goTo(-1);
        currentSection = new CurrentSection();
        while (this.sourceScanner.hasNextLine()){
            ParsedObject parsed = parseOneLine();
            if (parsed != null) {
                rtn.put(sourceScanner.getCurrentInstructionValue(), parsed);
            }
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

        ParsedObject directives = directiveParser.parseOneLine(sourceScanner, line + " ", currentSection);
        if (directives != null) return directives;

        if (currentSection.getValue() == Section.TEXT) return asmParser.parseOneLine(sourceScanner, line);
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

    public static class CurrentSection {
        private Section value;

        public CurrentSection() {
            this.value = Section.NONE;
        }

        public Section getValue() {
            return value == null ? Section.NONE : value;
        }

        public void setValue(Section value) {
            this.value = value;
        }
    }
}
