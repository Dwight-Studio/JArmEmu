package fr.dwightstudio.jarmemu.sim.parse.regex;

import fr.dwightstudio.jarmemu.asm.Directive;
import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.sim.parse.ParsedDirectivePack;
import fr.dwightstudio.jarmemu.sim.parse.ParsedDirective;
import fr.dwightstudio.jarmemu.sim.parse.ParsedLabel;
import fr.dwightstudio.jarmemu.sim.parse.ParsedObject;
import fr.dwightstudio.jarmemu.asm.Section;
import fr.dwightstudio.jarmemu.util.EnumUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectiveParser {

    private static final String[] DIRECTIVES = EnumUtils.getFromEnum(Directive.values(), false);
    private static final String DIRECTIVE_REGEX = String.join("|", DIRECTIVES);
    private static final String LABEL_REGEX = "[A-Za-z_0-9]+";
    private static final String ARGS_REGEX = "[^\n\\.]*";
    private static final Pattern DIRECTIVE_PATTERN = Pattern.compile(
            "(?i)"
                    + "("
                    + "(^[ \t]*(?<LABEL>" + LABEL_REGEX + ")[ \t]*:)|"
                    + "([ \t]*\\.(?<DIRECTIVE>" + DIRECTIVE_REGEX + "))"
                    + "(([ \t]+(?<ARGS>" + ARGS_REGEX + "))|)"
                    + ")"
                    + "(?-i)"
    );

    private int memoryPos;

    public DirectiveParser() {
        this.memoryPos = 0;
    }

    /**
     * Lecture d'une ligne avec Directive
     *
     * @param sourceScanner le SourceScanner associé
     * @param line la ligne à parser
     * @return un ParsedObject à verifier.
     */
    public ParsedObject parseOneLine(SourceScanner sourceScanner, String line, Section section) {

        Matcher matcher = DIRECTIVE_PATTERN.matcher(line);

        ParsedDirectivePack directives = new ParsedDirectivePack();

        boolean flag = false;
        while (matcher.find()) {
            flag = true;
            String labelString = matcher.group("LABEL");
            String directiveString = matcher.group("DIRECTIVE");
            String argsString = matcher.group("ARGS");

            if (labelString != null && !labelString.isEmpty()) {
                if (section.shouldParseDirective()) directives.add(new ParsedLabel(labelString.strip().toUpperCase(), memoryPos, true));

            } else if (directiveString != null && !directiveString.isEmpty()) {
                try {
                    Directive directive = Directive.valueOf(directiveString.toUpperCase());
                    ParsedDirective parsedDirective = new ParsedDirective(directive, argsString == null ? "" : argsString.strip().toUpperCase(), memoryPos);
                    directives.add(parsedDirective);
                    memoryPos += parsedDirective.computeDataLength();
                } catch (IllegalArgumentException exception) {
                    if (section.shouldParseDirective()) throw new SyntaxASMException("Unknown directive '" + directiveString + "' at line " + sourceScanner.getCurrentInstructionValue());
                }
            }
        }

        if (!flag) {
            if (section.shouldParseDirective()) throw new SyntaxASMException("Unexpected statement '" + line + "', at line " + sourceScanner.getCurrentInstructionValue());
        }

        return directives.close();
    }

}
