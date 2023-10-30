package fr.dwightstudio.jarmemu.asm.exceptions;

public class SyntaxASMException extends IllegalStateException {
    public SyntaxASMException(String s) {
        super(s);
    }

    public String getTitle() {
        return "Syntax error:";
    }
}
