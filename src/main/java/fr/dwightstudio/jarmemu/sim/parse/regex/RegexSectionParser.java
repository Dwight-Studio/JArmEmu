package fr.dwightstudio.jarmemu.sim.parse.regex;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.sim.parse.Section;
import fr.dwightstudio.jarmemu.util.EnumUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexSectionParser {

    private static final String[] SECTIONS = EnumUtils.getFromEnum(Section.values(), false);

    private static final String SECTION_REGEX = String.join("|", SECTIONS);

    private static final Pattern SECTION_PATTERN = Pattern.compile(
            "(?i)^[ \t]*"
                    +"(\\.SECTION |)[ \t]*"
                    + "\\.(?<SECTION>" + SECTION_REGEX + ")"
                    + "[ \t]*$(?-i)"
    );

    /**
     * Lecture d'une ligne avec section
     *
     * @param codeScanner le SourceScanner associé
     * @param line la ligne à parser
     * @return un ParsedObject à verifier.
     */
    public static Section parseOneLine(SourceScanner codeScanner, String line) {
        Section section;

        Matcher matcher = SECTION_PATTERN.matcher(line);

        if (matcher.find()) {
            String sectionString = matcher.group("SECTION");

            try {
                section = Section.valueOf(sectionString.toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new SyntaxASMException("Unknown section '" + sectionString + "'");
            }

            return section;

        } else {
            throw new SyntaxASMException("Invalid section declaration '" + line + "'");
        }
    }
}
