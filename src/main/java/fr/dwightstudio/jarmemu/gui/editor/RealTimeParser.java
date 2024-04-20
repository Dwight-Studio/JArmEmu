package fr.dwightstudio.jarmemu.gui.editor;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class RealTimeParser extends Thread {

    public RealTimeParser(@NotNull String name) {
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

    /**
     * @return les labels accessibles dans ce fichier
     */
    public abstract Set<String> getAccessibleLabels();

    public abstract Set<String> getSymbols();

    public abstract boolean lineDefinesLabel(int currentParagraph);
}
