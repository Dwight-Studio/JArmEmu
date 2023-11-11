package fr.dwightstudio.jarmemu.sim.exceptions;

import fr.dwightstudio.jarmemu.sim.parse.ParsedObject;

public class SyntaxASMException extends IllegalStateException {

    int line;
    ParsedObject parsedObject;

    public SyntaxASMException(String s) {
        super(s);
        line = -1;
    }

    public SyntaxASMException(String s, int line) {
        super(s);
        this.line = line;
    }

    public SyntaxASMException(String s, int line, ParsedObject parsedObject) {
        super(s);
        this.line = line;
        this.parsedObject = parsedObject;
    }

    public boolean isLineSpecified() {
        return line != -1;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public ParsedObject getObject() {
        return parsedObject;
    }

    public void setObject(ParsedObject obj) {
        this.parsedObject = obj;
    }

    public SyntaxASMException with(ParsedObject parsedObject) {
        this.parsedObject = parsedObject;
        return this;
    }

    public SyntaxASMException with(int line) {
        this.line = line;
        return this;
    }

    public String getTitle() {
        return "Syntax error:";
    }
}
