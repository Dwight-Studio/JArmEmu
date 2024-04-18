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
import javafx.application.Platform;
import org.apache.commons.lang3.ArrayUtils;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.richtext.model.TwoDimensional;
import org.reactfx.Subscription;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.dwightstudio.jarmemu.util.EnumUtils.getFromEnum;

public class RealTimeParser extends Thread {

    private static final String[] INSTRUCTIONS = getFromEnum(Instruction.values());
    private static final String[] DIRECTIVES = ArrayUtils.addAll(getFromEnum(Directive.values()), getFromEnum(Section.values()));
    private static final String[] REGISTERS = getFromEnum(RegisterUtils.values());
    private static final String[] CONDITIONS = getFromEnum(Condition.values());
    private static final String[] DATA_MODES = getFromEnum(DataMode.values());
    private static final String[] UPDATE_MODES = getFromEnum(UpdateMode.values());
    private static final String[] SHIFTS = new String[]{"LSL", "LSR", "ASR", "ROR", "RRX"};
    private static final String[] UPDATE_FLAG = new String[]{"S"};

    private static final Pattern BLANK_PATTERN = Pattern.compile("^[\t ]+");
    private static final Pattern COMMENT_PATTERN = Pattern.compile("^@[^\n]*");
    private static final Pattern ERROR_PATTERN = Pattern.compile("^[^ \t,.@\\[\\]{}]+");
    private static final Pattern GENERAL_SEPARATOR_PATTERN = Pattern.compile("^[ \t,.@\\[\\]{}]");

    private static final Pattern INSTRUCTION_PATTERN = Pattern.compile("^(?i)(" + String.join("|", INSTRUCTIONS) + ")(?-i)");
    private static final Pattern CONDITION_PATTERN = Pattern.compile("^(?i)(" + String.join("|", CONDITIONS) + ")(?-i)");
    private static final Pattern FLAGS_PATTERN = Pattern.compile("^(?i)(" + String.join("|", DATA_MODES) + "|" + String.join("|", UPDATE_FLAG) + "|" + String.join("|", UPDATE_MODES) + ")\\b(?-i)");
    private static final Pattern DIRECTIVE_PATTERN = Pattern.compile("^\\.(?i)(" + String.join("|", DIRECTIVES) + ")(?-i)\\b");
    private static final Pattern LABEL_PATTERN = Pattern.compile("^[A-Za-z_0-9]+[ \t]*:");

    private static final Pattern ARGUMENT_SEPARATOR = Pattern.compile("^,");
    private static final Pattern BRACE_PATTERN = Pattern.compile("^(\\{|\\})");
    private static final Pattern BRACKET_PATTERN = Pattern.compile("^(\\[|\\])");
    private static final Pattern STRING_PATTERN = Pattern.compile("^\"([^\"\\\\@]|\\\\.)*\"|\'([^\'\\\\@]|\\\\.)*\'");
    private static final Pattern IMMEDIATE_PATTERN = Pattern.compile("^#[^\n\\]@]*");
    private static final Pattern PSEUDO_INSTRUCTION_PATTERN = Pattern.compile("^=[^\n@]*");
    private static final Pattern REGISTER_PATTERN = Pattern.compile("^(?i)\\b(" + String.join("|", REGISTERS) + ")\\b(?-i)(!|)");
    private static final Pattern SHIFT_PATTERN = Pattern.compile("^(?i)\\b(" + String.join("|", SHIFTS) + ")\\b(?-i)");

    private final FileEditor editor;
    private final BlockingQueue<Integer> queue;
    private final Subscription subscription;

    private int line;
    private Context context;
    private SubContext subContext;
    private String text;
    private StyleSpansBuilder<Collection<String>> spansBuilder;
    private String command;
    private String argType;
    private boolean offsetArgument;
    private boolean brace;
    private boolean bracket;
    private boolean singleQuote;
    private boolean doubleQuote;

    public RealTimeParser(FileEditor editor) {
        super("RealTimeParser" + editor.getVisualIndex());
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

    private void setup() {
        text = editor.getCodeArea().getParagraph(line).getText();
        context = Context.NONE;
        subContext = SubContext.NONE;
        spansBuilder = new StyleSpansBuilder<>();
        command = "";
        argType = "";
        offsetArgument = false;
        brace = false;
        bracket = false;
        singleQuote = false;
        doubleQuote = false;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                line = queue.take();

                setup();

                while (!text.isEmpty()) {
                    System.out.println(context + ":" + subContext + ";" + argType + "{" + text);
                    if (matchComment()) continue;

                    switch (context) {
                        case NONE -> {
                            if (matchBlank()) continue;
                            if (matchLabel()) continue;
                            if (matchInstruction()) continue;
                            if (matchDirective()) continue;
                        }

                        case LABEL -> {
                            if (matchBlank()) continue;
                            if (matchInstruction()) continue;
                            if (matchDirective()) continue;
                        }

                        case COMMENT -> {
                        }
                        case INSTRUCTION -> {
                            if (matchBlank()) {
                                context = offsetArgument ? Context.INSTRUCTION_ARGUMENT_2 : Context.INSTRUCTION_ARGUMENT_1;
                                continue;
                            }

                            if (matchCondition()) continue;
                            if (matchFlags()) continue;
                        }

                        case CONDITION -> {
                            if (matchBlank()) {
                                context = offsetArgument ? Context.INSTRUCTION_ARGUMENT_2 : Context.INSTRUCTION_ARGUMENT_1;
                                continue;
                            }

                            if (matchFlags()) continue;
                        }

                        case FLAGS -> {
                            if (matchBlank()) {
                                context = offsetArgument ? Context.INSTRUCTION_ARGUMENT_2 : Context.INSTRUCTION_ARGUMENT_1;
                                continue;
                            }
                        }

                        case INSTRUCTION_ARGUMENT_1, INSTRUCTION_ARGUMENT_2, INSTRUCTION_ARGUMENT_3,
                             INSTRUCTION_ARGUMENT_4 -> {
                            if (matchBlank()) continue;
                            if (!bracket && !brace && matchInstructionArgumentSeparator()) continue;

                            if (matchInstructionArgument()) continue;
                        }

                        case DIRECTIVE -> {

                        }

                        case DIRECTIVE_ARGUMENTS -> {

                        }
                    }

                    tagError();
                }

                try {
                    final int finalLine = line;
                    StyleSpans<Collection<String>> spans = spansBuilder.create();
                    Platform.runLater(() -> editor.getCodeArea().setStyleSpans(finalLine, 0, spans));
                } catch (IllegalStateException ignored) {}
            } catch (InterruptedException e) {
                this.interrupt();
            }
        }
    }

    private boolean matchComment() {
        Matcher matcher = COMMENT_PATTERN.matcher(text);

        if (matcher.find()) {
            context = Context.COMMENT;
            tag("comment", matcher);
            return true;
        }

        return false;
    }

    private boolean matchBlank() {
        Matcher matcher = BLANK_PATTERN.matcher(text);

        if (matcher.find()) {
            tagBlank(matcher);
            return true;
        }

        return false;
    }

    private boolean matchLabel() {
        Matcher matcher = LABEL_PATTERN.matcher(text);

        if (matcher.find()) {
            context = Context.LABEL;
            tag("label", matcher);
            return true;
        }

        return false;
    }

    private boolean matchInstruction() {
        Matcher matcher = INSTRUCTION_PATTERN.matcher(text);

        if (matcher.find()) {
            command = matcher.group();
            context = Context.INSTRUCTION;
            tag("instruction", matcher);
            return true;
        }

        return false;
    }

    private boolean matchCondition() {
        Matcher matcher = CONDITION_PATTERN.matcher(text);

        if (matcher.find()) {
            context = Context.CONDITION;
            tag("condition", matcher);
            return true;
        }

        return false;
    }

    private boolean matchFlags() {
        Matcher matcher = FLAGS_PATTERN.matcher(text);

        if (matcher.find()) {
            context = Context.FLAGS;
            System.out.println(matcher.end());
            tag("flags", matcher);
            return true;
        }

        return false;
    }

    private boolean matchInstructionArgument() {
        Instruction instruction = Instruction.valueOf(command.toUpperCase());
        argType = instruction.getArgumentType(context.getIndex());

        boolean rtn = switch (argType) {
            case "RegisterArgument", "RegisterWithUpdateArgument" -> matchRegister();
            case "ImmediateArgument", "RotatedImmediateArgument" -> matchImmediate();
            case "RotatedOrRegisterArgument", "RotatedImmediateOrRegisterArgument" -> matchImmediateOrRegister();
            case "ShiftArgument" -> matchShift();
            case "AddressArgument" -> matchAddress();
            default -> false;
        };

        if (!rtn && !offsetArgument && instruction.hasWorkingRegister()) {
            setup();
            offsetArgument = true;
            return true;
        }

        return rtn;
    }

    private boolean matchRegister() {
        Matcher matcher = REGISTER_PATTERN.matcher(text);

        if (matcher.find()) {
            subContext = SubContext.REGISTER;
            tag("register", matcher);
            return true;
        }

        return false;
    }

    private boolean matchShift() {
        Matcher matcher = SHIFT_PATTERN.matcher(text);

        if (matcher.find()) {
            subContext = SubContext.SHIFT;
            tag("shift", matcher);
            return true;
        }

        return false;
    }

    private boolean matchImmediate() {
        Matcher matcher = IMMEDIATE_PATTERN.matcher(text);

        if (matcher.find()) {
            subContext = SubContext.IMMEDIATE;
            tag("immediate", matcher);
            return true;
        }

        return false;
    }

    private boolean matchImmediateOrRegister() {
        if (matchImmediate()) return true;
        else return matchRegister();
    }

    private boolean matchPseudoInstruction() {
        Matcher matcher = PSEUDO_INSTRUCTION_PATTERN.matcher(text);

        if (matcher.find()) {
            tag("pseudo-instruction", matcher);
            return true;
        }

        return false;
    }

    private boolean matchBrace() {
        Matcher matcher = BRACE_PATTERN.matcher(text);

        if (matcher.find()) {
            return switch (matcher.group()) {
                case "{" -> {
                    if (brace) tagError();
                    else {
                        tag("brace", matcher);
                        brace = true;
                        yield true;
                    }

                    yield false;
                }

                case "}" -> {
                    if (!brace) tagError();
                    else {
                        tag("brace", matcher);
                        brace = false;
                        yield true;
                    }

                    yield false;
                }

                default -> false;
            };
        }

        return false;
    }

    private boolean matchBracket() {
        Matcher matcher = BRACKET_PATTERN.matcher(text);

        if (matcher.find()) {
            return switch (matcher.group()) {
                case "[" -> {
                    if (bracket) tagError();
                    else {
                        tag("bracket", matcher);
                        bracket = true;
                        subContext = SubContext.NONE;
                        yield true;
                    }

                    yield false;
                }

                case "]" -> {
                    if (!bracket) tagError();
                    else {
                        tag("bracket", matcher);
                        bracket = false;
                        subContext = SubContext.NONE;
                        yield true;
                    }

                    yield false;
                }

                default -> false;
            };
        }

        return false;
    }

    private boolean matchString() {
        Matcher matcher = STRING_PATTERN.matcher(text);

        if (matcher.find()) {
            tag("string", matcher);
            return true;
        }

        return false;
    }

    private boolean matchAddress() {
        if (bracket || matchBracket()) {
            switch (subContext) {
                case NONE -> {
                    if (!matchRegister() && !matchBracket()) tagError();
                }

                case REGISTER -> {
                    if (matchBracket()) {
                        return true;
                    } else if (matchSubSeparator()) {
                        subContext = SubContext.PRIMARY;
                    } else tagError();
                }

                case PRIMARY -> {
                    if (matchImmediateOrRegister()) {
                        subContext = SubContext.SECONDARY;
                    } else tagError();
                }

                case SECONDARY -> {
                    if (matchBracket()) {
                        return true;
                    } else if (matchSubSeparator()) {
                        subContext = SubContext.TERTIARY;
                    } else tagError();
                }

                case TERTIARY -> {
                    if (!matchShift()) tagError();
                }

                case SHIFT -> {
                    if (!matchBracket() && !matchImmediate()) tagError();
                }

                case IMMEDIATE -> {
                    if (!matchBracket()) tagError();
                }
            }

            return true;
        } else if (matchPseudoInstruction()) {

            return true;
        } else return false;
    }

    private boolean matchInstructionArgumentSeparator() {
        Matcher matcher = ARGUMENT_SEPARATOR.matcher(text);

        if (matcher.find()) {
            subContext = SubContext.NONE;
            context = context.getNext();
            tagBlank(matcher);
            return true;
        }

        return false;
    }

    private boolean matchSubSeparator() {
        Matcher matcher = ARGUMENT_SEPARATOR.matcher(text);

        if (matcher.find()) {
            tagBlank(matcher);
            return true;
        }

        return false;
    }

    private boolean matchDirective() {
        Matcher matcher = DIRECTIVE_PATTERN.matcher(text);

        if (matcher.find()) {
            command = matcher.group();
            context = Context.DIRECTIVE;
            tag("directive", matcher);
            return true;
        }

        return false;
    }

    private void tag(String highlight, Matcher matcher) {
        spansBuilder.add(Collections.singleton(highlight), matcher.end());
        text = text.substring(matcher.end());
    }

    private void tagBlank(Matcher matcher) {
        spansBuilder.add(Collections.emptyList(), matcher.end());
        text = text.substring(matcher.end());
    }

    private void tagError() {
        Matcher matcher = ERROR_PATTERN.matcher(text);

        if (matcher.find()) {
            spansBuilder.add(Collections.singleton("error"), matcher.end());
            text = text.substring(matcher.end());
        } else {
            matcher = GENERAL_SEPARATOR_PATTERN.matcher(text);

            if (matcher.find()) {
                tag("error", matcher);
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
