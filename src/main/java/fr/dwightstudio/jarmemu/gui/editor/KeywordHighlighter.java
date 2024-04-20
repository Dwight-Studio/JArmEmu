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

package fr.dwightstudio.jarmemu.gui.editor;

import fr.dwightstudio.jarmemu.asm.directive.Directive;
import fr.dwightstudio.jarmemu.asm.directive.Section;
import fr.dwightstudio.jarmemu.asm.instruction.Condition;
import fr.dwightstudio.jarmemu.asm.instruction.DataMode;
import fr.dwightstudio.jarmemu.asm.instruction.Instruction;
import fr.dwightstudio.jarmemu.asm.instruction.UpdateMode;
import fr.dwightstudio.jarmemu.gui.controllers.FileEditor;
import fr.dwightstudio.jarmemu.util.RegisterUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.richtext.model.TwoDimensional;
import org.reactfx.Subscription;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.dwightstudio.jarmemu.util.EnumUtils.getFromEnum;

public class KeywordHighlighter extends RealTimeParser {

    private static final String[] INSTRUCTIONS = getFromEnum(Instruction.values(), false);
    private static final String[] DIRECTIVES = ArrayUtils.addAll(getFromEnum(Directive.values(), false), getFromEnum(Section.values(), false));
    private static final String[] REGISTERS = getFromEnum(RegisterUtils.values(), false);
    private static final String[] CONDITIONS = getFromEnum(Condition.values(), true);
    private static final String[] DATA_MODES = getFromEnum(DataMode.values(), true);
    private static final String[] UPDATE_MODES = getFromEnum(UpdateMode.values(), true);
    private static final String[] SHIFTS = new String[]{"LSL", "LSR", "ASR", "ROR", "RRX"};
    private static final String[] UPDATE_FLAG = new String[]{"S", ""};

    private static final String INSTRUCTION_PATTERN = "\\b(?i)(" + String.join("|", INSTRUCTIONS) + ")(" + String.join("|", CONDITIONS) + ")((" + String.join("|", DATA_MODES) + ")|(" + String.join("|", UPDATE_FLAG) + ")|(" + String.join("|", UPDATE_MODES) + "))\\b";
    private static final String DIRECTIVES_PATTERN = "\\.\\b(?i)(" + String.join("|", DIRECTIVES) + ")(?-i)\\b";
    private static final String REGISTER_PATTERN = "\\b(?i)(" + String.join("|", REGISTERS) + ")(?-i)\\b(!|)";
    private static final String SHIFT_PATTERN = "\\b(?i)(" + String.join("|", SHIFTS) + ")(?-i)\\b";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String LABEL_PATTERN = "[A-Za-z_0-9]+[ \t]*:";
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

    private final FileEditor editor;
    private final BlockingQueue<Integer> queue;
    private final Subscription subscription;

    public KeywordHighlighter(FileEditor editor) {
        super("RealTimeParser" + editor.getRealIndex());
        this.editor = editor;
        this.queue = new LinkedBlockingQueue<>();

        subscription = editor.getCodeArea().plainTextChanges().subscribe(change -> {
            int start = editor.getCodeArea().offsetToPosition(change.getPosition(), TwoDimensional.Bias.Forward).getMajor();
            int stop = editor.getCodeArea().offsetToPosition(change.getInsertionEnd(), TwoDimensional.Bias.Forward).getMajor();

            for (int i = start; i <= stop; i++) {
                queue.add(i);
            }
        });

        this.start();
    }

    // TODO: Ajouter le support des du Find&Replace

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                int line = queue.take();

                int lastKwEnd = 0;
                StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

                String text = editor.getCodeArea().getParagraph(line).getText();
                Matcher matcher = PATTERN.matcher(text);

                while (matcher.find()) {

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

                if (lastKwEnd != 0) editor.getCodeArea().setStyleSpans(line, 0, spansBuilder.create());
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

    /**
     * Marque la ligne comme nécessitant une actualisation
     *
     * @param line la ligne à actualiser
     */
    public void markDirty(int line) {
        this.queue.add(line);
    }

    @Override
    public Set<String> getAccessibleLabels() {
        return Set.of();
    }

    @Override
    public Set<String> getSymbols() {
        return Set.of();
    }

    /**
     * Marque les lignes comme nécessitant une actualisation
     *
     * @param startLine la ligne de début
     * @param stopLine la ligne de fin (exclue)
     */
    public void markDirty(int startLine, int stopLine) {
        for (int i = startLine; i <= stopLine; i++) {
            queue.add(i);
        }
    }
}
