package fr.dwightstudio.jarmemu.base.gui.editor;

import fr.dwightstudio.jarmemu.base.asm.Section;
import fr.dwightstudio.jarmemu.base.gui.controllers.FileEditor;

public record SmartContext(FileEditor editor, int line, Section section, Context context, SubContext subContext, int cursorPos, int contextLength, String command, String argType, boolean bracket, boolean brace, boolean rrx) {
    public SmartContext() {
        this(null, 0, null, null, null, 0, 0, null, null, false, false, false);
    }
}
