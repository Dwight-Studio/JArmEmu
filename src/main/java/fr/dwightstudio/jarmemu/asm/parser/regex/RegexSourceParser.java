/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2024 Dwight Studio
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.dwightstudio.jarmemu.asm.parser.regex;

import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.asm.parser.SourceParser;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.asm.ParsedFile;
import fr.dwightstudio.jarmemu.sim.parse.ParsedObject;
import org.jetbrains.annotations.NotNull;

import java.util.TreeMap;

public class RegexSourceParser implements SourceParser {

    private SourceScanner sourceScanner;
    protected CurrentSection currentSection;
    private ASMParser asmParser;
    private DirectiveParser directiveParser;

    public RegexSourceParser() {
        currentSection= new CurrentSection();

        asmParser = new ASMParser();
        directiveParser = new DirectiveParser();
    }

    public RegexSourceParser(SourceScanner sourceScanner) {
        this();
        this.sourceScanner = sourceScanner;
    }

    /**
     * Définie la liste des fichiers
     *
     * @param source le SourceScanner utilisé
     */
    @Override
    public void setSource(SourceScanner source) {
        this.sourceScanner = source;
    }

    /**
     * Méthode principale
     * Lecture du fichier et renvoie des objets parsés non vérifiés
     */
    @Override
    public ParsedFile parse() {
        TreeMap<Integer, ParsedObject> rtn = new TreeMap<>();
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

        return new ParsedFile(sourceScanner, rtn);
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
