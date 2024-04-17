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

import fr.dwightstudio.jarmemu.asm.ParsedFile;
import fr.dwightstudio.jarmemu.asm.directive.Section;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.parser.SourceParser;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import org.jetbrains.annotations.NotNull;

public class RegexSourceParser implements SourceParser {

    protected Section currentSection;

    public RegexSourceParser() {
        currentSection = Section.NONE;
    }

    /**
     * Méthode principale
     * Lecture du fichier et renvoie des objets parsés non vérifiés
     */
    @Override
    public ParsedFile parse(SourceScanner sourceScanner) throws ASMException {
        ParsedFile file = new ParsedFile(sourceScanner);
        sourceScanner.goTo(-1);
        currentSection = Section.NONE;

        while (sourceScanner.hasNextLine()){
            String line = sourceScanner.nextLine();

            line = prepare(line);
            if (line.isEmpty()) continue;

            try {
                boolean found = DirectiveParser.parseOneLine(this, line, sourceScanner, file);
                if (!found && currentSection == Section.TEXT) ASMParser.parseOneLine(this, line, sourceScanner, file);
            } catch (ASMException exception) {
                throw exception.with(file).with(sourceScanner.getLineNumber());
            }
        }

        return file;
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
