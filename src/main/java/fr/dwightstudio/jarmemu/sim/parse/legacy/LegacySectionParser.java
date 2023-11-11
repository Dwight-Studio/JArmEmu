package fr.dwightstudio.jarmemu.sim.parse.legacy;

import fr.dwightstudio.jarmemu.asm.Directive;
import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;

import java.util.Arrays;

public class LegacySectionParser {

    public Section parseOneLine(String line) {
        Section section;

        if (!line.startsWith(".")) {
            return null;
        } else {
            String sectionString = Arrays.asList(line.split("\\.")).getLast().split(" ")[0];
            try {
                Directive.valueOf(sectionString.toUpperCase());
                section = null;
            } catch (Exception e) {
                try {
                    section = Section.valueOf(sectionString.toUpperCase());
                } catch (IllegalArgumentException exception) {
                    throw new SyntaxASMException("Unknown section '" + sectionString + "'");
                }
            }
            return section;
        }
    }

}
