package fr.dwightstudio.jarmemu.asm.exception;

import fr.dwightstudio.jarmemu.asm.ParsedFile;
import fr.dwightstudio.jarmemu.asm.ParsedObject;

public class ASMException extends Exception {
    int line;
    ParsedObject parsedObject;
    private ParsedFile file;

    public ASMException(String s) {
        super(s);
        line = -1;
    }

    public ASMException() {
        super();
        line = -1;
    }

    public boolean isLineSpecified() {
        return line != -1;
    }

    public boolean isFileSpecified() {
        return file != null;
    }

    public int getLine() {
        return line;
    }

    public ParsedFile getFile() {
        return file;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setFile(ParsedFile file) {
        this.file = file;
    }

    public ParsedObject getObject() {
        return parsedObject;
    }

    public void setObject(ParsedObject obj) {
        this.parsedObject = obj;
    }

    public ASMException with(ParsedObject parsedObject) {
        this.parsedObject = parsedObject;
        return this;
    }

    public ASMException with(int line) {
        this.line = line;
        return this;
    }

    public ASMException with(ParsedFile file) {
        this.file = file;
        return this;
    }
}
