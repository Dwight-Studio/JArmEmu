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

import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.gui.controllers.FileEditor;
import fr.dwightstudio.jarmemu.util.RegisterUtils;
import javafx.concurrent.Task;
import org.apache.commons.lang3.ArrayUtils;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.dwightstudio.jarmemu.util.EnumUtils.getFromEnum;

public class ComputeHightlightsTask extends Task<StyleSpans<Collection<String>>> {

    private static final String[] INSTRUCTIONS = getFromEnum(OInstruction.values(), false);
    private static final String[] KEYWORDS = ArrayUtils.addAll(getFromEnum(Directive.values(), false), getFromEnum(Section.values(), false));
    private static final String[] REGISTERS = getFromEnum(RegisterUtils.values(), false);
    private static final String[] CONDITIONS = getFromEnum(Condition.values(), true);
    private static final String[] DATA_MODES = getFromEnum(DataMode.values(), true);
    private static final String[] UPDATE_MODES = getFromEnum(UpdateMode.values(), true);
    private static final String[] SHIFTS = new String[]{"LSL", "LSR", "ASR", "ROR", "RRX"};
    private static final String[] UPDATE_FLAG = new String[]{"S", ""};

    private static final String INSTRUCTION_PATTERN = "\\b(?i)(" + String.join("|", INSTRUCTIONS) + ")(" + String.join("|", CONDITIONS) + ")((" + String.join("|", DATA_MODES) + ")|(" + String.join("|", UPDATE_FLAG) + ")|(" + String.join("|", UPDATE_MODES) + "))\\b";
    private static final String KEYWORD_PATTERN = "\\.\\b(?i)(" + String.join("|", KEYWORDS) + ")(?-i)\\b";
    private static final String REGISTER_PATTERN = "\\b(?i)(" + String.join("|", REGISTERS) + ")(?-i)\\b";
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
                    + "|(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<INSTRUCTION>" + INSTRUCTION_PATTERN + ")"
                    + "|(?<REGISTER>" + REGISTER_PATTERN + ")"
                    + "|(?<IMM>" + IMM_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
    );

    private final FileEditor fileEditor;

    public ComputeHightlightsTask(FileEditor fileEditor) {
        this.fileEditor = fileEditor;
    }

    @Override
    protected StyleSpans<Collection<String>> call() {
        Matcher matcher = PATTERN.matcher(fileEditor.getCodeArea().getText());
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            if (matcher.group("NEWLINE") != null) {
                continue;
            }

            String styleClass = matcher.group("COMMENT") != null ? "comment"
                    : matcher.group("STRING") != null ? "string"
                    : matcher.group("SHIFT") != null ? "shift"
                    : matcher.group("LABEL") != null ? "label"
                    : matcher.group("KEYWORD") != null ? "keyword"
                    : matcher.group("INSTRUCTION") != null ? "instruction"
                    : matcher.group("REGISTER") != null ? "register"
                    : matcher.group("BRACE") != null ? "brace"
                    : matcher.group("BRACKET") != null ? "bracket"
                    : matcher.group("IMM") != null ? "imm" : null;

            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }

        fileEditor.updateSaveState();
        return spansBuilder.create();
    }
}
