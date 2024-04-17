package fr.dwightstudio.jarmemu.gui.editor;

public enum Context {
    NONE,
    COMMENT,

    INSTRUCTION,
    CONDITION,
    FLAGS,
    INSTRUCTION_ARGUMENT_1(0),
    INSTRUCTION_ARGUMENT_2(1),
    INSTRUCTION_ARGUMENT_3(2),
    INSTRUCTION_ARGUMENT_4(3),

    LABEL,

    DIRECTIVE,
    DIRECTIVE_ARGUMENTS;

    private final int index;

    Context() {
        this.index = -1;
    }

    Context(final int index) {
        this.index = index;
    }

    public Context getNext() {
        if (index == -1) {
            return NONE;
        } else {
            for (Context context : Context.values()) {
                if (context.index == index + 1) {
                    return context;
                }
            }
        }
        return NONE;
    }

    public int getIndex() {
        return index;
    }
}
