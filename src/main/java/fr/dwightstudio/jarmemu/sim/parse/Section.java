package fr.dwightstudio.jarmemu.sim.parse;

public enum Section {
    NONE,
    BSS, //Uninitialized read-write data.
    COMMENT, //Version control information.
    DATA, //Initialized read-write data.
    RODATA, //Read-only data.
    TEXT, //Executable instructions.
    NOTE; //Special information from vendors or system builders.
}
