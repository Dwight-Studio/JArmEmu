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

import fr.dwightstudio.jarmemu.base.asm.Directive;
import fr.dwightstudio.jarmemu.base.asm.Instruction;
import fr.dwightstudio.jarmemu.base.asm.Section;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.DataMode;
import fr.dwightstudio.jarmemu.base.asm.modifier.UpdateMode;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.controllers.FileEditor;
import fr.dwightstudio.jarmemu.base.util.CaseIndependentEntry;
import fr.dwightstudio.jarmemu.base.util.EnumUtils;
import fr.dwightstudio.jarmemu.base.util.RegisterUtils;
import javafx.application.Platform;
import org.apache.commons.lang3.ArrayUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeywordHighlighter extends RealTimeParser {

    private static final String[] INSTRUCTIONS = EnumUtils.valuesToString(Instruction.values(), false);
    private static final String[] DIRECTIVES = ArrayUtils.addAll(EnumUtils.valuesToString(Directive.values(), false), EnumUtils.valuesToString(Section.values(), false));
    private static final String[] REGISTERS = EnumUtils.valuesToString(RegisterUtils.values(), false);
    private static final String[] CONDITIONS = EnumUtils.valuesToString(Condition.values(), true);
    private static final String[] DATA_MODES = EnumUtils.valuesToString(DataMode.values(), true);
    private static final String[] UPDATE_MODES = EnumUtils.valuesToString(UpdateMode.values(), true);
    private static final String[] SHIFTS = new String[]{"LSL", "LSR", "ASR", "ROR", "RRX"};
    private static final String[] UPDATE_FLAG = new String[]{"S", ""};

    private static final String INSTRUCTION_PATTERN = "\\b(?i)(" + String.join("|", INSTRUCTIONS) + ")(" + String.join("|", CONDITIONS) + ")((" + String.join("|", DATA_MODES) + ")|(" + String.join("|", UPDATE_FLAG) + ")|(" + String.join("|", UPDATE_MODES) + "))\\b";
    private static final String DIRECTIVES_PATTERN = "\\.\\b(?i)(" + String.join("|", DIRECTIVES) + ")(?-i)\\b";
    private static final String REGISTER_PATTERN = "\\b(?i)(" + String.join("|", REGISTERS) + ")(?-i)\\b(!|)";
    private static final String SHIFT_PATTERN = "\\b(?i)(" + String.join("|", SHIFTS) + ")(?-i)\\b";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String LABEL_PATTERN = "[A-Za-z_]+[A-Za-z_0-9]*[ \t]*:";
    private static final String STRING_PATTERN = "\"([^\"\\\\@]|\\\\.)*\"|\'([^\'\\\\@]|\\\\.)*\'";
    private static final String COMMENT_PATTERN = "@[^\n]*";
    private static final String IMM_PATTERN = "=[^\n@]*|#[^\n\\]@]*";
    private static final Pattern PATTERN = Pattern.compile(
            "(?<NEWLINE>\n)"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<SHIFT>" + SHIFT_PATTERN + ")"
                    + "|(?<LABEL>" + LABEL_PATTERN + ")"
                    + "|(?<DIRECTIVE>" + DIRECTIVES_PATTERN + ")"
                    + "|(?<INSTRUCTION>" + INSTRUCTION_PATTERN + ")"
                    + "|(?<REGISTER>" + REGISTER_PATTERN + ")"
                    + "|(?<IMMEDIATE>" + IMM_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
    );
    private static final Pattern LABEL_COMPILED_PATTERN = Pattern.compile(LABEL_PATTERN);

    private final FileEditor editor;
    private final BlockingQueue<Integer> queue;
    private final Subscription subscription;

    private int cancelLine;

    public KeywordHighlighter(FileEditor editor) {
        super("RealTimeParser" + editor.getRealIndex());
        this.editor = editor;
        this.queue = new LinkedBlockingQueue<>();

        subscription = editor.getCodeArea().plainTextChanges().subscribe(change -> {
            editor.updateSaveState();
            int start = editor.getLineFromPos(change.getPosition());
            int end = Math.max(change.getInsertionEnd(), change.getRemovalEnd());

            int stop;
            if (end >= editor.getCodeArea().getLength() || change.getInserted().contains("\n") || change.getRemoved().contains("\n")) {
                stop = editor.getTotalLineNumber() + 1;
            } else {
                stop = editor.getLineFromPos(end) + 1;
            }

            markDirty(start, stop);
        });

        cancelLine = -1;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                int line = queue.take();

                if (line <= 0 || line > editor.getTotalLineNumber()) continue;

                int lastKwEnd = 0;
                StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

                String text = editor.getCodeArea().getParagraph(line - 1).getText();
                Matcher matcher = PATTERN.matcher(text);

                while (matcher.find() && cancelLine != line && !this.isInterrupted()) {

                    String styleClass = matcher.group("COMMENT") != null ? "comment"
                            : matcher.group("STRING") != null ? "string"
                            : matcher.group("SHIFT") != null ? "shift"
                            : matcher.group("LABEL") != null ? "label"
                            : matcher.group("DIRECTIVE") != null ? "DIRECTIVE"
                            : matcher.group("INSTRUCTION") != null ? "instruction"
                            : matcher.group("REGISTER") != null ? "register"
                            : matcher.group("BRACE") != null ? "brace"
                            : matcher.group("BRACKET") != null ? "bracket"
                            : matcher.group("IMMEDIATE") != null ? "immediate" : null;

                    spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
                    spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                    lastKwEnd = matcher.end();
                }

                cancelLine = -1;

                if (lastKwEnd != 0) Platform.runLater(() -> editor.getCodeArea().setStyleSpans(line - 1, 0, spansBuilder.create()));
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();

        subscription.unsubscribe();
    }

    @Override
    public void markDirty(int line) {
        if (!queue.contains(line)) queue.add(line);
    }

    @Override
    public Set<String> getAccessibleLabels() {
        return Set.of();
    }

    @Override
    public Set<String> getSymbols() {
        return Set.of();
    }

    @Override
    public Set<CaseIndependentEntry> getCaseTranslationTable() {
        return Set.of();
    }

    @Override
    public boolean lineDefinesLabel(int currentParagraph) {
        CodeArea codeArea = JArmEmuApplication.getEditorController().currentFileEditor().getCodeArea();
        return LABEL_COMPILED_PATTERN.matcher(codeArea.getParagraph(codeArea.getCurrentParagraph()).getText()).find();
    }

    @Override
    public void markDirty(int startLine, int stopLine) {
        int max = editor.getTotalLineNumber() + 1;
        for (int i = Math.max(1, startLine); i < stopLine && i < max; i++) {
            markDirty(i);
        }
    }

    @Override
    public void cancelLine(int cancelLine) {
        queue.remove(cancelLine);
        this.cancelLine = cancelLine;
    }

    @Override
    public void preventAutocomplete(int line) {

    }
}
