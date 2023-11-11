package fr.dwightstudio.jarmemu.sim.parse.legacy;

import fr.dwightstudio.jarmemu.asm.Directive;
import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.parse.ParsedDirective;
import fr.dwightstudio.jarmemu.sim.parse.ParsedDirectiveLabel;
import fr.dwightstudio.jarmemu.sim.parse.ParsedDirectivePack;
import fr.dwightstudio.jarmemu.sim.parse.ParsedObject;

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

        if (labelString != null && !labelString.isEmpty() && section.shouldParseDirective()) directives.add(new ParsedDirectiveLabel(labelString.strip().toUpperCase()));
        if (!directiveString.isEmpty()) {
            try {
                Directive directive = Directive.valueOf(directiveString.toUpperCase());
                ParsedDirective parsedDirective = new ParsedDirective(directive, argsString == null ? "" : argsString.strip());
                directives.add(parsedDirective);
            } catch (IllegalArgumentException exception) {
                if (section.shouldParseDirective()) throw new SyntaxASMException("Unknown directive '" + directiveString + "'", sourceScanner.getCurrentInstructionValue());
            }
        }

        return directives.close();
    }

}
