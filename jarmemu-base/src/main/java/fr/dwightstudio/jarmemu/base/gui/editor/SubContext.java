package fr.dwightstudio.jarmemu.base.gui.editor;

public enum SubContext {
    NONE,
    CONDITION,
    FLAGS,

    PRIMARY,
    SECONDARY,
    TERTIARY,
    QUATERNARY,

    REGISTER,
    IMMEDIATE,
    PSEUDO,
    SHIFT,
    ADDRESS,
    REGISTER_ARRAY,
    LABEL_REF,
    INVALID_LABEL_REF;
}
