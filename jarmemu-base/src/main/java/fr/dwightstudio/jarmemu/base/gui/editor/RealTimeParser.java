package fr.dwightstudio.jarmemu.base.gui.editor;

import fr.dwightstudio.jarmemu.base.util.CaseIndependentEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class RealTimeParser extends Thread {

    public RealTimeParser(@NotNull String name) {
        super(name);
    }

    /**
     * Marks lines as requiring update
     *
     * @param startLine starting line
     * @param stopLine stop line (excluded)
     */
    public abstract void markDirty(int startLine, int stopLine);

    /**
     * Marks line as requiring update
     *
     * @param line the line to update
     */
    public abstract void markDirty(int line);

    /**
     * @return the labels accessible from this file
     */
    public abstract Set<String> getAccessibleLabels();

    /**
     * @return the symbols accessible from this file
     */
    public abstract Set<String> getSymbols();

    /**
     * @return the symbols and labels translation table (from identifier to name with case)
     */
    public abstract Set<CaseIndependentEntry> getCaseTranslationTable();

    /**
     * @return true if the line defines a label
     */
    public abstract boolean lineDefinesLabel(int currentParagraph);

    /**
     * Cancels line analysis if in progress
     * @param cancelLine the line to cancel
     */
    public abstract void cancelLine(int cancelLine);
}
