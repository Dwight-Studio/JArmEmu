package fr.dwightstudio.jarmemu.gui.editor;

import org.jetbrains.annotations.NotNull;

public abstract class SmartHighlighter extends Thread {

    public SmartHighlighter(@NotNull String name) {
        super(name);
    }

    /**
     * Marque les lignes comme nécessitant une actualisation
     *
     * @param startLine la ligne de début
     * @param stopLine la ligne de fin (exclue)
     */
    public abstract void markDirty(int startLine, int stopLine);

    /**
     * Marque la ligne comme nécessitant une actualisation
     *
     * @param line la ligne à actualiser
     */
    public abstract void markDirty(int line);
}
