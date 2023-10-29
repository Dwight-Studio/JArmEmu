package fr.dwightstudio.jarmemu.sim.obj;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;

public class AssemblyError {

    private final int line;
    private final SyntaxASMException exception;

    public AssemblyError(int line, SyntaxASMException exception) {
        this.line = line;
        this.exception = exception;
    }


    public int getLine() {
        return line;
    }

    public SyntaxASMException getException() {
        return exception;
    }
}
