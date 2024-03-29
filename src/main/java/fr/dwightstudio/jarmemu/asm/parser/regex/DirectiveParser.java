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

import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.asm.directive.ParsedDirective;
import fr.dwightstudio.jarmemu.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.asm.exception.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.util.EnumUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectiveParser {

    private static final String[] DIRECTIVES = EnumUtils.getFromEnum(Directive.values(), false);
    private static final String[] SECTIONS = EnumUtils.getFromEnum(Section.values(), false);
    private static final String SECTION_REGEX = String.join("|", SECTIONS);
    private static final String DIRECTIVE_REGEX = String.join("|", DIRECTIVES);
    private static final String SSECTION_REGEX = "SECTION";
    private static final String LABEL_REGEX = "[A-Za-z_0-9]+";
    private static final String ARGS_REGEX = "[^\n.]*";
    private static final Pattern DIRECTIVE_PATTERN = Pattern.compile(
            "(?i)"
            + "("
            + "(^[ \t]*(?<LABEL>" + LABEL_REGEX + ")[ \t]*:)|"
            + "([ \t]*\\.(?<SSECTION>" + SSECTION_REGEX + ") *)|"
            + "([ \t]*\\.(?<SECTION>" + SECTION_REGEX + ") *)|"
            + "([ \t]*\\.(?<DIRECTIVE>" + DIRECTIVE_REGEX + "))"
            + "(([ \t]+(?<ARGS>" + ARGS_REGEX + ") *)|)"
            + ")"
            + "(?-i)"
    );

    /**
     * Lecture d'une ligne avec Directive
     *
     * @param parser l'instance de l'analyser
     * @param sourceScanner le scanneur de fichier
     * @param parsedFile le fichier à analyser
     */
    protected static boolean parseOneLine(RegexSourceParser parser, String line, SourceScanner sourceScanner, ParsedFile parsedFile) throws ASMException {
        boolean rtn = false;
        Matcher matcher = DIRECTIVE_PATTERN.matcher(line);

        boolean flag = false;
        while (matcher.find()) {
            flag = true;
            String labelString = matcher.group("LABEL");
            String ssectionString = matcher.group("SSECTION");
            String sectionString = matcher.group("SECTION");
            String directiveString = matcher.group("DIRECTIVE");
            String argsString = matcher.group("ARGS");

            if (ssectionString != null && !ssectionString.isEmpty()) {
                rtn = true;
                // Rien à faire, on ignore cette directive
            } else if (sectionString != null && !sectionString.isEmpty()) {
                rtn = true;
                try {
                    Section section = Section.valueOf(sectionString.toUpperCase());
                    parser.currentSection = section;
                    parsedFile.add(new ParsedSection(section).withLineNumber(sourceScanner.getLineNumber()));
                } catch (IllegalArgumentException exception) {
                    throw new SyntaxASMException("Unknown section '" + sectionString + "'").with(sourceScanner.getLineNumber()).with(new ParsedFile(sourceScanner));
                }
            } else if (labelString != null && !labelString.isEmpty()) {
                if (parser.currentSection.shouldParseDirective()) {
                    rtn = true;
                    parsedFile.add(new ParsedLabel(parser.currentSection, labelString.strip().toUpperCase()).withLineNumber(sourceScanner.getLineNumber()));
                }
            } else if (directiveString != null && !directiveString.isEmpty()) {
                rtn = true;
                try {
                    Directive directive = Directive.valueOf(directiveString.toUpperCase());
                    parsedFile.add(directive.create(parser.currentSection, argsString == null ? "" : argsString.strip()).withLineNumber(sourceScanner.getLineNumber()));
                } catch (IllegalArgumentException exception) {
                    if (parser.currentSection.shouldParseDirective()) throw new SyntaxASMException("Unknown directive '" + directiveString + "'").with(sourceScanner.getLineNumber()).with(parsedFile);
                }
            }
        }

        if (!flag) {
            if (parser.currentSection.shouldParseDirective()) throw new SyntaxASMException("Unexpected statement '" + line + "'").with(sourceScanner.getLineNumber()).with(parsedFile);
        }

        return rtn;
    }
}