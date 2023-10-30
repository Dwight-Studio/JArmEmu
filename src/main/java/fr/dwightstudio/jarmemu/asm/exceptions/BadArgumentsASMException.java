package fr.dwightstudio.jarmemu.asm.exceptions;

public class BadArgumentsASMException extends SyntaxASMException {
    public BadArgumentsASMException(String s) {
        super("Bad arguments: " + s);
    }

    @Override
    public String getTitle() {
        return "Bad arguments:";
    }
}
