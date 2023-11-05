package fr.dwightstudio.jarmemu.asm;

public enum Section {
    NONE(false, true),
    BSS(false, true), // Uninitialized read-write data.
    COMMENT(false, false), // Version control information.
    DATA(false, true), // Initialized read-write data.
    RODATA(false, true), // Read-only data.
    TEXT(true, false), // Executable instructions.
    NOTE(false, false); // Special information from vendors or system builders.

    private final boolean parseASM;
    private final boolean parseDirective;

    Section(boolean parseASM, boolean parseDirective) {

        this.parseASM = parseASM;
        this.parseDirective = parseDirective;
    }

    public boolean shouldParseASM() {
        return parseASM;
    }

    public boolean shouldParseDirective() {
        return parseDirective;
    }
}
