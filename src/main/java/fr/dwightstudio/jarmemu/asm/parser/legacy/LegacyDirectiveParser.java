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

package fr.dwightstudio.jarmemu.asm.parser.legacy;

import fr.dwightstudio.jarmemu.asm.Directive;
import fr.dwightstudio.jarmemu.asm.ParsedFile;
import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.sim.parse.ParsedDirective;
import fr.dwightstudio.jarmemu.sim.parse.ParsedDirectiveLabel;
import fr.dwightstudio.jarmemu.sim.parse.ParsedDirectivePack;

public class LegacyDirectiveParser {

    public ParsedObject parseOneLine(SourceScanner sourceScanner, String line, Section section) {

        ParsedDirectivePack directives = new ParsedDirectivePack();

        if (line.endsWith(":")) return directives.close();

        String labelString = null;
        String directiveString;
        String argsString = null;

        String[] lineParts = line.split("\\.");

        if (lineParts.length == 1){
            directiveString = lineParts[0].split(" ")[0].strip();
            argsString = lineParts[0].split(" ")[1].strip();
        } else {
            if (lineParts[0].isEmpty()) {
                if (lineParts[1].strip().split(" ").length == 1){
                    directiveString = lineParts[1].strip();
                } else {
                    directiveString = lineParts[1].strip().split(" ")[0].strip();
                    argsString = lineParts[1].substring(lineParts[1].split(" ")[0].length());
                }

            } else {
                labelString = lineParts[0].strip().substring(0, lineParts[0].strip().length()-1);
                directiveString = lineParts[1].split(" ")[0].strip();
                argsString = lineParts[1].substring(lineParts[1].split(" ")[0].length());
            }
        }

        if (labelString != null && !labelString.isEmpty() && section.shouldParseDirective()) directives.add(new ParsedDirectiveLabel(labelString.strip().toUpperCase(), section));
        if (!directiveString.isEmpty()) {
            try {
                Directive directive = Directive.valueOf(directiveString.toUpperCase());
                ParsedDirective parsedDirective = new ParsedDirective(directive, argsString == null ? "" : argsString.strip(), section);
                directives.add(parsedDirective);
            } catch (IllegalArgumentException exception) {
                if (section.shouldParseDirective()) throw new SyntaxASMException("Unknown directive '" + directiveString + "'").with(sourceScanner.getCurrentInstructionValue()).with(new ParsedFile(sourceScanner));
            }
        }

        return directives.close();
    }

}
