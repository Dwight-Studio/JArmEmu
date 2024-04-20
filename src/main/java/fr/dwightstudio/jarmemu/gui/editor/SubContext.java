package fr.dwightstudio.jarmemu.gui.editor;

public enum SubContext {
    NONE,

    CONDITION,
    FLAGS,

    PRIMARY,
    SECONDARY,
    TERTIARY,

    REGISTER,
    IMMEDIATE,
    SHIFT,
    ADDRESS,
    LABEL_REF;
}
