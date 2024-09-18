/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2024 Dwight Studio
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.dwightstudio.jarmemu.base.gui.editor;

import fr.dwightstudio.jarmemu.base.util.CaseIndependentEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class RealTimeParser extends Thread {

    public static final int DEFAULT_REAL_TIME_PARSER = 0;

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

    /**
     * Prevents autocomplete when processing line
     * @param line the line to prevents
     */
    public abstract void preventAutocomplete(int line);
}
