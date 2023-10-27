package fr.dwightstudio.jarmemu.sim.obj;

import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;

public class AssemblyError {

    private final int line;
    private final AssemblySyntaxException exception;

    public AssemblyError(int line, AssemblySyntaxException exception) {
        this.line = line;
        this.exception = exception;
    }


    public int getLine() {
        return line;
    }

    public AssemblySyntaxException getException() {
        return exception;
    }
}
