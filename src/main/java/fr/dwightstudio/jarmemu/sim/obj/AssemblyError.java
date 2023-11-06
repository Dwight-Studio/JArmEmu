package fr.dwightstudio.jarmemu.sim.obj;

import fr.dwightstudio.jarmemu.asm.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.parse.ParsedObject;
import org.jetbrains.annotations.Nullable;

public class AssemblyError {

    private final int line;
    private final ParsedObject object;
    private final SyntaxASMException exception;

    public AssemblyError(int line, SyntaxASMException exception) {
        this.line = line;
        this.exception = exception;
        this.object = null;
    }

    public AssemblyError(int line, SyntaxASMException exception, ParsedObject object) {
        this.line = line;
        this.exception = exception;
        this.object = object;
    }

    public AssemblyError withObject(ParsedObject object) {
        return new AssemblyError(this.line, this.exception, object);
    }

    public int getLine() {
        return line;
    }

    public SyntaxASMException getException() {
        return exception;
    }

    public @Nullable ParsedObject getObject() {
        return object;
    }
}
