package fr.dwightstudio.jarmemu.sim.parse.legacy;

import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;

import java.util.Arrays;

public class LegacySectionParser {

    public Section parseOneLine(String line) {
        Section section;

        if (!line.startsWith(".")) {
            return null;
        } else {
            String sectionString = Arrays.asList(line.split("\\.")).getLast();
            try {
                section = Section.valueOf(sectionString.toUpperCase());
            } catch (IllegalArgumentException exception) {
                throw new SyntaxASMException("Unknown section '" + sectionString + "'");
            }

            return section;
        }
    }

}
