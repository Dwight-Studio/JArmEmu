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
import fr.dwightstudio.jarmemu.gui.controllers.EditorController;
import fr.dwightstudio.jarmemu.gui.controllers.FileEditor;
import fr.dwightstudio.jarmemu.sim.entity.FilePos;
import fr.dwightstudio.jarmemu.util.EnumUtils;
import fr.dwightstudio.jarmemu.util.RegisterUtils;
import javafx.application.Platform;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.richtext.model.TwoDimensional;
import org.reactfx.Subscription;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.dwightstudio.jarmemu.util.EnumUtils.getFromEnum;

public class RealTimeParser extends RealTimeAnalyzer {

    public static final int MAXIMUM_ITER_NUM = 1000;

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
    private static final Pattern DIRECTIVE_PATTERN = Pattern.compile("^\\.(?i)(?<DIRECTIVE>" + String.join("|", DIRECTIVES) + ")(?-i)\\b");
    private static final Pattern LABEL_PATTERN = Pattern.compile("^(?<LABEL>[A-Za-z_0-9]+)[ \t]*:");

    private static final Pattern ARGUMENT_SEPARATOR = Pattern.compile("^,");
    private static final Pattern BRACE_PATTERN = Pattern.compile("^(\\{|\\})");
    private static final Pattern BRACKET_PATTERN = Pattern.compile("^(\\[|\\])");
    private static final Pattern STRING_PATTERN = Pattern.compile("^\"([^\"\\\\@]|\\\\.)*\"|\'([^\'\\\\@]|\\\\.)*\'");
    private static final Pattern IMMEDIATE_PATTERN = Pattern.compile("^#[^\n\\]@]*");
    private static final Pattern DIRECTIVE_VALUE_PATTERN = Pattern.compile("^[^\n\\]@]*");
    private static final Pattern PSEUDO_INSTRUCTION_PATTERN = Pattern.compile("^=[^\n@]*");
    private static final Pattern REGISTER_PATTERN = Pattern.compile("^(?i)\\b(" + String.join("|", REGISTERS) + ")\\b(?-i)(!|)");
    private static final Pattern SHIFT_PATTERN = Pattern.compile("^(?i)\\b(" + String.join("|", SHIFTS) + ")\\b(?-i)");
    private static final Pattern LABEL_ARGUMENT_PATTERN = Pattern.compile("^[A-Za-z_0-9]+");

    private static final Logger logger = Logger.getLogger(RealTimeParser.class.getSimpleName());

    private final FileEditor editor;
    private final EditorController controller;
    private final BlockingQueue<Integer> queue;
    private final Subscription subscription;

    private int line;

    private static HashMap<FilePos, String> globals;
    private HashMap<Integer, String> labels;
    private HashMap<Integer, String> symbols;
    private HashMap<Integer, Set<String>> references;
    private String addGlobals;
    private String addLabels;
    private String addSymbols;
    private HashSet<String> addReferences;

    private Context context;
    private SubContext subContext;
    private String text;
    private StyleSpansBuilder<Collection<String>> spansBuilder;
    private String command;
    private String argType;
    private boolean offsetArgument;
    private boolean brace;
    private boolean bracket;
    private List<Find> find;

    public RealTimeParser(FileEditor editor, EditorController controller) {
        super("RealTimeParser" + editor.getRealIndex());
        this.editor = editor;
        this.controller = controller;
        this.queue = new LinkedBlockingQueue<>();

        subscription = editor.getCodeArea().plainTextChanges().subscribe(change -> {
            int start = editor.getCodeArea().offsetToPosition(change.getPosition(), TwoDimensional.Bias.Forward).getMajor();

            if (change.getInserted().contains("\n") || change.getRemoved().contains("\n")) {
                markDirty(start, editor.getCodeArea().getParagraphs().size());
            } else {
                int end = Math.max(change.getInsertionEnd(), change.getRemovalEnd());
                int stop = editor.getCodeArea().offsetToPosition(end, TwoDimensional.Bias.Forward).getMajor() + 1;

                markDirty(start, stop);
            }
        });

        globals = new HashMap<>();
        labels = new HashMap<>();
        symbols = new HashMap<>();
        references = new HashMap<>();
        addReferences = new HashSet<>();
    }

    private void setup() {
        text = editor.getCodeArea().getParagraph(line).getText();

        addGlobals = "";
        addLabels = "";
        addSymbols = "";
        addReferences.clear();

        context = Context.NONE;
        subContext = SubContext.NONE;
        spansBuilder = new StyleSpansBuilder<>();
        command = "";
        argType = "";
        offsetArgument = false;
        brace = false;
        bracket = false;

        find = editor.getSearch(text);
    }

    @Override
    public void run() {
        try {
            while (editor.getRealIndex() == -1) {
                Thread.sleep(50);
            }
            while (!this.isInterrupted()) {
                try {
                    line = queue.take();

                    if (line < 0 || line >= editor.getCodeArea().getParagraphs().size()) continue;

                    setup();

                    int iter;
                    for (iter = 0; !text.isEmpty() && !this.isInterrupted() && iter < MAXIMUM_ITER_NUM; iter++) {
                        //System.out.println(context + ":" + subContext + ";" + command + ";" + argType + "{" + text);
                        if (matchComment()) continue;

                        switch (context) {
                            case ERROR -> {
                                if (matchBlank()) continue;
                            }

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
                                if (matchBlank()) {
                                    context = Context.DIRECTIVE_ARGUMENTS;
                                    continue;
                                }
                            }

                            case DIRECTIVE_ARGUMENTS -> {
                                if (matchBlank()) continue;
                                if (matchDirectiveArguments()) continue;
                            }
                        }

                        tagError();
                    }

                    if (iter >= MAXIMUM_ITER_NUM - 1) {
                        logger.severe("Hanging line " + line + " parsing after " + MAXIMUM_ITER_NUM + " iterations");
                    }

                    String remGlobal = globals.getOrDefault(new FilePos(editor.getRealIndex(), line), "");
                    String remLabel = labels.getOrDefault(line, "");
                    String remSymbol = symbols.getOrDefault(line, "");

                    if (!remGlobal.equals(addGlobals)) {
                        if (remGlobal.isEmpty()) {
                            globals.put(new FilePos(editor.getRealIndex(), line), addGlobals);
                            updateGlobals(addGlobals);
                        } else {
                            globals.remove(new FilePos(editor.getRealIndex(), line));
                            updateGlobals(remGlobal);
                        }
                    }

                    if (!remLabel.equals(addLabels)) {
                        if (remLabel.isEmpty()) {
                            labels.put(line, addLabels);
                            updateReferences(addLabels);
                        } else {
                            labels.remove(line);
                            updateReferences(remLabel);
                        }
                        updateReferences(remLabel.toUpperCase());
                    }

                    if (!remSymbol.equals(addSymbols)) {
                        if (remLabel.isEmpty()) {
                            symbols.put(line, addSymbols);
                            updateReferences(addSymbols);
                        } else {
                            symbols.remove(line);
                            updateReferences(remSymbol);
                        }
                    }

                    Set<String> ref = references.get(line);
                    if (ref != null) {
                        ref.clear();
                        ref.addAll(addReferences);
                    } else if (!addReferences.isEmpty()) {
                        references.put(line, new HashSet<>(addReferences));
                    }

                    try {
                        final int finalLine = line;
                        StyleSpans<Collection<String>> spans = spansBuilder.create();
                        Platform.runLater(() -> editor.getCodeArea().setStyleSpans(finalLine, 0, spans));
                    } catch (IllegalStateException ignored) {
                    }
                } catch (Exception e) {
                    if (e instanceof InterruptedException ex) throw ex;
                    logger.severe(ExceptionUtils.getStackTrace(e));
                }
            }
        } catch (InterruptedException ignored) {}
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
            addLabels = matcher.group("LABEL").toUpperCase();
            context = Context.LABEL;
            addReferences.add(addLabels);
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
            tag("flags", matcher);
            return true;
        }

        return false;
    }

    private boolean matchInstructionArgument() {
        Instruction instruction = Instruction.valueOf(command.toUpperCase());
        argType = instruction.getArgumentType(context.getIndex());

        if (argType == null) {
            return false;
        }

        boolean rtn = switch (argType) {
            case "RegisterArgument", "RegisterWithUpdateArgument" -> matchRegister();
            case "ImmediateArgument", "RotatedImmediateArgument" -> matchImmediate();
            case "CodeArgument" -> matchCodeArgument();
            case "RotatedOrRegisterArgument", "RotatedImmediateOrRegisterArgument" -> matchImmediateOrRegister();
            case "ShiftArgument" -> matchShift();
            case "AddressArgument" -> matchAddress();
            case "RegisterAddressArgument" -> matchRegisterAddress();
            case "RegisterArrayArgument" -> matchRegisterArray();
            case "LabelArgument" -> matchLabelArgument();
            case "LabelOrRegisterArgument" -> matchLabelOrRegister();
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

    private boolean matchCodeArgument() {
        Matcher matcher = DIRECTIVE_VALUE_PATTERN.matcher(text);

        if (matcher.find()) {
            subContext = SubContext.IMMEDIATE;
            tag("immediate", matcher);
            return true;
        }

        return false;
    }

    private boolean matchBrace() {
        Matcher matcher = BRACE_PATTERN.matcher(text);

        if (matcher.find()) {
            return switch (matcher.group()) {
                case "{" -> {
                    if (brace) yield false;
                    else {
                        tag("brace", matcher);
                        brace = true;
                        subContext = SubContext.NONE;
                        yield true;
                    }
                }

                case "}" -> {
                    if (!brace) yield false;
                    else {
                        tag("brace", matcher);
                        brace = false;
                        subContext = SubContext.NONE;
                        yield true;
                    }
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
                    if (!matchRegister()) return false;
                }

                case REGISTER -> {
                    if (matchBracket()) {
                        return true;
                    } else if (matchSubSeparator()) {
                        subContext = SubContext.PRIMARY;
                    } else return false;
                }

                case PRIMARY -> {
                    if (matchImmediateOrRegister()) {
                        subContext = SubContext.SECONDARY;
                    } else return false;
                }

                case SECONDARY -> {
                    if (matchBracket()) {
                        return true;
                    } else if (matchSubSeparator()) {
                        subContext = SubContext.TERTIARY;
                    } else return false;
                }

                case TERTIARY -> {
                    if (!matchShift()) return false;
                }

                case SHIFT -> {
                    if (!matchBracket() && !matchImmediate()) return false;
                }

                case IMMEDIATE -> {
                    if (!matchBracket()) return false;
                }
            }

            return true;
        } else return matchPseudoInstruction();
    }

    private boolean matchRegisterAddress() {
        if (bracket || matchBracket()) {
            if (subContext == SubContext.NONE) {
                if (!matchRegister()) return false;
            } else if (subContext == SubContext.REGISTER) {
                return matchBracket();
            }
        }

        return false;
    }

    private boolean matchRegisterArray() {
        if (brace || matchBrace()) {
            switch (subContext) {
                case NONE -> {
                    if (!matchRegister()) return false;
                }

                case REGISTER -> {
                    if (matchBrace()) {
                        return true;
                    } else if (matchSubSeparator()) {
                        subContext = SubContext.NONE;
                    } else tagError();
                }
            }

            return true;
        } else return false;
    }

    private boolean matchLabelArgument() {
        Matcher matcher = LABEL_ARGUMENT_PATTERN.matcher(text);

        if (matcher.find()) {
            String label = matcher.group().toUpperCase();

            addReferences.add(label);

            if (labels.containsValue(label) || globals.containsValue(label) || addLabels.equals(label)) {
                tag("label-ref", matcher);
            } else {
                tagError("label-ref", matcher);
            }

            return true;
        }

        return false;
    }

    private boolean matchLabelOrRegister() {
        if (matchRegister()) return true;
        return matchLabelArgument();
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
            command = matcher.group("DIRECTIVE");
            context = Context.DIRECTIVE;
            tag("directive", matcher);
            return true;
        }

        return false;
    }

    private boolean matchDirectiveArguments() {
        switch (command.toUpperCase()) {
            case "SET", "EQU", "EQUIV", "EQV" -> {
                switch (subContext) {
                    case NONE -> {
                        Matcher matcher = LABEL_ARGUMENT_PATTERN.matcher(text);

                        if (matcher.find()) {
                            tag("label", matcher);
                            subContext = SubContext.PRIMARY;
                            return true;
                        }

                        return false;
                    }

                    case PRIMARY -> {
                        if (matchSubSeparator()) {
                            subContext = SubContext.SECONDARY;
                            return true;
                        } else {
                            return false;
                        }
                    }

                    case SECONDARY -> {
                        Matcher matcher = DIRECTIVE_VALUE_PATTERN.matcher(text);

                        if (matcher.find()) {
                            tag("immediate", matcher);
                            return true;
                        }

                        return false;
                    }

                    default -> {
                        return false;
                    }
                }
            }

            case "GLOBAL", "GLOBL", "EXPORT" -> {
                Matcher matcher = LABEL_ARGUMENT_PATTERN.matcher(text);

                if (matcher.find()) {
                    addGlobals = matcher.group().toUpperCase();
                    addReferences.add(addGlobals);
                    context = Context.ERROR;

                    if (labels.containsValue(addGlobals) || addLabels.equals(addGlobals)) {
                        tag("label-ref", matcher);
                    } else {
                        tagError("label-ref", matcher);
                    }

                    return true;
                }

                return false;
            }

            default -> {
                if (matchSubSeparator()) return true;

                Matcher matcher = ERROR_PATTERN.matcher(text);

                if (matcher.find()) {
                    tag("directive-argument", matcher);
                }

                return true;
            }
        }
    }

    private void addSpan(Collection<String> highlights, int end) {
        if (!find.isEmpty()) {
            ArrayList<String> highlightsWithFind = new ArrayList<>(highlights);
            highlightsWithFind.add("find");

            int done = 0;
            for (Find f : find) {
                if (done >= end) return;
                if (f.start() == 0) {
                    if (f.end() < end) {
                        spansBuilder.add(highlightsWithFind, f.end() - done);
                        done = f.end();
                    } else {
                        spansBuilder.add(highlightsWithFind, end - done);
                        done = end;
                    }
                } else if (f.start() < end) {
                    if (f.end() < end) {
                        spansBuilder.add(highlights, f.start() - done);
                        spansBuilder.add(highlightsWithFind, f.end() - f.start());
                        done = f.end();
                    } else {
                        spansBuilder.add(highlights, f.start() - done);
                        spansBuilder.add(highlightsWithFind, end - f.start());
                        done = end;
                    }
                }
            }

            if (done < end) spansBuilder.add(highlights, end - done);
        } else {
            spansBuilder.add(highlights, end);
        }
    }

    private void tag(String highlight, Matcher matcher) {
        int end = matcher.end();
        addSpan(Collections.singleton(highlight), end);
        find.replaceAll(f -> f.offset(end));
        find.removeIf(Objects::isNull);
        text = text.substring(end);
    }

    private void tagBlank(Matcher matcher) {
        int end = matcher.end();
        addSpan(Collections.emptyList(), end);
        find.replaceAll(f -> f.offset(end));
        find.removeIf(Objects::isNull);
        text = text.substring(end);
    }

    private void tagError(String highlight, Matcher matcher) {
        int end = matcher.end();
        addSpan(List.of(highlight, "error-secondary"), end);
        find.replaceAll(f -> f.offset(end));
        find.removeIf(Objects::isNull);
        text = text.substring(end);
    }

    private void tagError() {
        Matcher matcher = ERROR_PATTERN.matcher(text);

        if (matcher.find()) {
            int end = matcher.end();
            addSpan(Collections.singleton("error"), end);
            find.replaceAll(f -> f.offset(end));
            find.removeIf(Objects::isNull);
            text = text.substring(end);
        } else {
            matcher = GENERAL_SEPARATOR_PATTERN.matcher(text);

            if (matcher.find()) {
                tag("error", matcher);
            }
        }
    }

    /**
     * Met à jour les références
     *
     * @param name le nom du global/label/symbol à mettre à jour
     */
    private void updateReferences(String name) {
        for (Map.Entry<Integer, Set<String>> entry : references.entrySet()) {
            if (entry.getValue().contains(name)) {
                queue.add(entry.getKey());
            }
        }
    }

    /**
     * Met à jour les globals
     *
     * @param name le nom du global à mettre à jour
     */
    private void updateGlobals(String name) {
        controller.getFileEditors().forEach(editor -> {
            if (editor != this.editor) ((RealTimeParser) editor.getRealTimeAnalyzer()).updateReferences(name);
        });
    }

    @Override
    public void interrupt() {
        super.interrupt();

        subscription.unsubscribe();
    }

    @Override
    public void markDirty(int line) {
        this.queue.add(line);
    }

    @Override
    public void markDirty(int startLine, int stopLine) {
        for (int i = startLine; i <= stopLine; i++) {
            queue.add(i);
        }
    }
}
