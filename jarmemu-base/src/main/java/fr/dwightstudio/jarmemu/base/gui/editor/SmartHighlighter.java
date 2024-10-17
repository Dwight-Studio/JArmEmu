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
import fr.dwightstudio.jarmemu.base.asm.parser.regex.ASMParser;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.controllers.FileEditor;
import fr.dwightstudio.jarmemu.base.sim.entity.FilePos;
import fr.dwightstudio.jarmemu.base.util.CaseIndependentEntry;
import fr.dwightstudio.jarmemu.base.util.EnumUtils;
import fr.dwightstudio.jarmemu.base.util.RegisterUtils;
import javafx.application.Platform;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmartHighlighter extends RealTimeParser {

    public static final int MAXIMUM_ITER_NUM = 1000;

    public static final String[] SECTIONS = EnumUtils.valuesToString(Section.values(), Section.NONE);
    public static final String[] DIRECTIVES = EnumUtils.valuesToString(Directive.values());
    public static final String[] REGISTERS = EnumUtils.valuesToString(RegisterUtils.values());
    public static final String[] SHIFTS = new String[]{"LSL", "LSR", "ASR", "ROR", "RRX"};

    public static final Pattern BLANK_PATTERN = Pattern.compile("^[\t ]+");
    public static final Pattern COMMENT_PATTERN = Pattern.compile("^@[^\n]*");
    public static final Pattern ERROR_PATTERN = Pattern.compile("^[^ \t,.@\\[\\]{}]+");
    public static final Pattern GENERAL_SEPARATOR_PATTERN = Pattern.compile("^[ \t,.@\\[\\]{}]");

    public static final Pattern SECTION_PATTERN = Pattern.compile("^\\.(?i)(?<SECTION>" + String.join("|", SECTIONS) + "|SECTION)(?-i)\\b");
    public static final Pattern DIRECTIVE_PATTERN = Pattern.compile("^\\.(?i)(?<DIRECTIVE>" + String.join("|", DIRECTIVES) + ")(?-i)\\b");
    public static final Pattern LABEL_PATTERN = Pattern.compile("^(?<LABEL>[A-Za-z_]+[A-Za-z_0-9]*)[ \t]*:");
    public static final Pattern INSTRUCTION_PATTERN = Pattern.compile("^(?i)(" + ASMParser.INSTRUCTION_REGEX + ")( |\t|$)(?-i)");

    public static final Pattern ARGUMENT_SEPARATOR = Pattern.compile("^,");
    public static final Pattern RANGE_SEPARATOR = Pattern.compile("^-");
    public static final Pattern BRACE_PATTERN = Pattern.compile("^(\\{|\\})");
    public static final Pattern BRACKET_PATTERN = Pattern.compile("^(\\[|\\]!|\\])");
    public static final Pattern STRING_PATTERN = Pattern.compile("^\"([^\"\\\\@]|\\\\.)*\"|\'([^\'\\\\@]|\\\\.)*\'");
    public static final Pattern IMMEDIATE_PATTERN = Pattern.compile("^#[^\n\\]@,]*");
    public static final Pattern DIRECTIVE_VALUE_PATTERN = Pattern.compile("^[^\n@]*");
    public static final Pattern PSEUDO_INSTRUCTION_PATTERN = Pattern.compile("^=[^\n@]*");
    public static final Pattern REGISTER_PATTERN = Pattern.compile("^(?i)\\b(" + String.join("|", REGISTERS) + ")\\b(?-i)(!|)");
    public static final Pattern REGISTER_SIGN_PATTERN = Pattern.compile("^(\\+|-|)");
    public static final Pattern SHIFT_PATTERN = Pattern.compile("^(?i)\\b(" + String.join("|", SHIFTS) + ")\\b(?-i)");
    public static final Pattern LABEL_ARGUMENT_PATTERN = Pattern.compile("^[A-Za-z_]+[A-Za-z_0-9]*");
    public static final Pattern LABEL_ARGUMENT_BIS_PATTERN = Pattern.compile("^(?<LABEL>[A-Za-z_]+[A-Za-z_0-9]*)[ \t]*(@|$)");
    public static final Pattern IGNORED_ARGUMENT = Pattern.compile("^[^,\n@]+");

    public static final Pattern NOTE_PATTERN = Pattern.compile("^[^\n]+");

    private static final Logger logger = Logger.getLogger(SmartHighlighter.class.getSimpleName());

    private final FileEditor editor;
    private final BlockingQueue<Integer> queue;
    private final Subscription subscription;

    private int line;
    private int cancelLine;
    private final Object LOCK = new Object();
    private HashSet<CaseIndependentEntry> caseTranslationTable;
    private HashMap<FilePos, String> globals;
    private final TreeMap<Integer, Section> sections;
    private final HashMap<Integer, String> labels;
    private final HashMap<Integer, String> symbols;
    private final HashMap<Integer, Set<String>> references;
    private String addGlobals;
    private Section addSection;
    private String addLabels;
    private String addSymbols;
    private final HashSet<String> addReferences;

    private Section currentSection;
    private Context context;
    private SubContext subContext;
    private int contextLength;
    private String text;
    private int cursorPos;
    private StyleSpansBuilder<Collection<String>> spansBuilder;
    private String command;
    private String modifier;
    private Instruction instruction;
    private String argType;

    private boolean offsetArgument;
    private boolean brace;
    private boolean bracket;
    private boolean rrx;
    private boolean error;
    private boolean errorOnLastIter;

    private List<Find> find;

    public SmartHighlighter(FileEditor editor) {
        super("RealTimeParser" + editor.getRealIndex());
        this.editor = editor;
        this.queue = new LinkedBlockingQueue<>();

        subscription = editor.getCodeArea().plainTextChanges().subscribe(change -> {
            try {
                editor.updateSaveState();
                int startLine = editor.getLineFromPos(change.getPosition()) + 1;
                int end = Math.max(change.getInsertionEnd(), change.getRemovalEnd());

                int endLine;
                if (end >= editor.getCodeArea().getLength() || change.getInserted().contains("\n") || change.getRemoved().contains("\n")) {
                    endLine = editor.getTotalLineNumber() + 1;
                } else {
                    endLine = editor.getLineFromPos(end) + 2;
                }

                markDirty(startLine, endLine);
            } catch (Exception e) {
                logger.warning(ExceptionUtils.getStackTrace(e));
            }
        });

        caseTranslationTable = new HashSet<>();
        globals = new HashMap<>();
        sections = new TreeMap<>();
        labels = new HashMap<>();
        symbols = new HashMap<>();
        references = new HashMap<>();
        addReferences = new HashSet<>();

        cancelLine = -1;
    }

    private void setup() {
        text = editor.getCodeArea().getParagraph(line - 1).getText();
        cursorPos = editor.getCurrentLine() == line ? editor.getCodeArea().getCaretColumn() : Integer.MAX_VALUE;

        currentSection = getCurrentSection();
        addGlobals = "";
        addSection = null;
        addLabels = "";
        addSymbols = "";
        addReferences.clear();

        context = Context.NONE;
        subContext = SubContext.NONE;
        contextLength = 0;
        spansBuilder = new StyleSpansBuilder<>();
        command = "";
        argType = "";

        offsetArgument = false;
        brace = false;
        bracket = false;
        rrx = false;
        error = false;
        errorOnLastIter = false;

        find = editor.getSearch(text);
    }

    @Override
    public void run() {
        try {
            sleep(500);
            while (!this.isInterrupted()) {
                try {
                    line = queue.take();

                    if (line <= 0 || line > editor.getTotalLineNumber()) continue;

                    setup();

                    int iter;
                    for (iter = 0; cancelLine != line && !this.isInterrupted() && iter < MAXIMUM_ITER_NUM; iter++) {
                        //System.out.println(currentSection + " " + context + ":" + subContext + ";" + command + ";" + argType + "{" + text);

                        errorOnLastIter = error;
                        error = false;

                        if (cursorPos <= 0) {
                            SmartContext sc = new SmartContext(editor, line, currentSection, context, subContext, cursorPos, contextLength, command, argType, bracket, brace, rrx);
                            JArmEmuApplication.getAutocompletionController().update(sc);
                            cursorPos = Integer.MAX_VALUE;
                        }

                        if (text.isEmpty()) break;

                        if (currentSection == Section.TEXT) {
                            if (matchComment()) continue;

                            switch (context) {
                                case ERROR -> {
                                    if (matchBlank()) continue;
                                }

                                case NONE -> {
                                    if (matchBlank()) continue;
                                    if (matchLabel()) continue;
                                    if (matchInstruction()) continue;
                                    if (matchSection()) continue;
                                    if (matchDirective()) continue;
                                }

                                case LABEL -> {
                                    if (matchBlank()) continue;
                                    if (matchInstruction()) continue;
                                    if (matchDirective()) continue;
                                }

                                case INSTRUCTION -> {
                                    if (matchBlank()) {
                                        context = offsetArgument ? Context.INSTRUCTION_ARGUMENT_2 : Context.INSTRUCTION_ARGUMENT_1;
                                        argType = instruction.getArgumentType(context.getIndex());
                                        if (argType != null) argType = argType.replaceFirst("Optional", "");
                                        continue;
                                    }
                                }

                                case INSTRUCTION_ARGUMENT_1, INSTRUCTION_ARGUMENT_2, INSTRUCTION_ARGUMENT_3,
                                     INSTRUCTION_ARGUMENT_4 -> {
                                    if (subContext != SubContext.INVALID_LABEL_REF) if (matchBlank()) continue;
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
                        } else if (currentSection.isDataRelatedSection() || currentSection == Section.NONE) {
                            if (matchComment()) continue;

                            switch (context) {
                                case ERROR -> {
                                    if (matchBlank()) continue;
                                }

                                case NONE -> {
                                    if (matchBlank()) continue;
                                    if (matchLabel()) continue;
                                    if (matchSection()) continue;
                                    if (matchDirective()) continue;
                                }

                                case LABEL -> {
                                    if (matchBlank()) continue;
                                    if (matchDirective()) continue;
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
                        } else if (currentSection == Section.COMMENT || currentSection == Section.NOTE) {
                            if (matchBlank()) continue;
                            if (matchSection()) continue;
                            matchNote();
                            break;
                        } else if (currentSection == Section.END) {
                            matchNote();
                            break;
                        }

                        tagError();
                    }

                    cancelLine = -1;

                    if (iter >= MAXIMUM_ITER_NUM - 1) {
                        logger.severe("Hanging line " + line + " parsing after " + MAXIMUM_ITER_NUM + " iterations");
                    }

                    synchronized (LOCK) {
                        String remGlobal = globals.getOrDefault(new FilePos(editor.getRealIndex(), line), "");
                        Section remSection = sections.getOrDefault(line, null);
                        String remLabel = labels.getOrDefault(line, "");
                        String remSymbol = symbols.getOrDefault(line, "");

                        if (!remGlobal.equals(addGlobals)) {
                            if (remGlobal.isEmpty()) {
                                globals.put(new FilePos(editor.getRealIndex(), line), addGlobals);
                                updateGlobals(addGlobals);
                            } else {
                                globals.remove(new FilePos(editor.getRealIndex(), line));
                                caseTranslationTable.remove(new CaseIndependentEntry(remGlobal));
                                updateGlobals(remGlobal);
                            }
                        }

                        if (remSection != addSection) {
                            if (remSection == null) {
                                sections.put(line, addSection);
                            } else {
                                sections.remove(line);
                            }

                            if (addSection != Section.END) {
                                markDirty(line + 1, getNextSectionLine());
                            } else {
                                markDirty(line + 1, editor.getTotalLineNumber());
                            }
                        }

                        if (!remLabel.equals(addLabels)) {
                            if (remLabel.isEmpty()) {
                                labels.put(line, addLabels);
                                updateReferences(addLabels);
                            } else {
                                labels.remove(line);
                                caseTranslationTable.remove(new CaseIndependentEntry(remLabel));
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
                                caseTranslationTable.remove(new CaseIndependentEntry(remSymbol));
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
                    }

                    try {
                        final int paragraph = line - 1;
                        StyleSpans<Collection<String>> spans = spansBuilder.create();
                        Platform.runLater(() -> {
                            try {
                                editor.getCodeArea().setStyleSpans(paragraph, 0, spans);
                            } catch (IndexOutOfBoundsException e) {
                                logger.warning("Wrong StyleSpans length for line " + paragraph + 1);
                            }
                        });
                    } catch (IllegalStateException ignored) {
                    }
                } catch (Exception e) {
                    if (e instanceof InterruptedException ex) throw ex;
                    logger.severe(ExceptionUtils.getStackTrace(e));
                }
            }
        } catch (InterruptedException ignored) {
        }
    }

    private boolean matchNote() {
        Matcher matcher = NOTE_PATTERN.matcher(text);

        if (matcher.find()) {
            tagBlank(matcher);
            return true;
        }

        return false;
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
            addLabels = matcher.group("LABEL");

            caseTranslationTable.add(new CaseIndependentEntry(addLabels));
            addLabels = addLabels.toUpperCase();

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
            command = null;
            modifier = null;

            for (int i = 0; i < ASMParser.INSTRUCTION_NUMBER; i++) {
                if (matcher.group("INS" + i) != null) {
                    command = matcher.group("INS" + i).toLowerCase();
                    modifier = matcher.group("MOD" + i).toLowerCase();
                    break;
                }
            }

            if (command != null) {
                instruction = Instruction.valueOf(command.toUpperCase());
                context = Context.INSTRUCTION;
                subContext = SubContext.NONE;

                if (instruction.isValid()) {
                    tag("instruction", command.length());
                    if (!modifier.isEmpty()) tag("modifier", modifier.length());

                    contextLength = command.length() + modifier.length();
                } else {
                    matcher = DIRECTIVE_VALUE_PATTERN.matcher(text);
                    if (matcher.find()) {
                        tag("invalid-instruction", matcher);
                    }
                }

                error = true;
                return true;
            }
        }

        return false;
    }

    private boolean matchInstructionArgument() {
        if (argType == null) {
            return false;
        }

        boolean rtn = switch (argType) {
            case "RegisterArgument", "RegisterWithUpdateArgument" -> matchRegister();
            case "ImmediateArgument", "SmallImmediateArgument", "LongImmediateArgument", "RotatedImmediateArgument" ->
                    matchImmediate();
            case "RotatedOrRegisterArgument", "RotatedImmediateOrRegisterArgument" -> matchImmediateOrRegister();
            case "ShiftArgument" -> matchShift();
            case "AddressArgument" -> matchAddress();
            case "RegisterAddressArgument" -> matchRegisterAddress();
            case "RegisterArrayArgument" -> matchRegisterArray();
            case "LabelArgument" -> matchLabelArgument();
            case "LabelOrRegisterArgument" -> matchLabelOrRegister();
            case "PostOffsetArgument" -> matchOffset();
            case "IgnoredArgument" -> matchIgnored();
            default -> false;
        };

        if (!rtn && !offsetArgument && instruction.hasWorkingRegister()) {
            setup();
            offsetArgument = true;
            return true;
        }

        return rtn;
    }

    private boolean matchIgnored() {
        Matcher matcher = IGNORED_ARGUMENT.matcher(text);

        if (matcher.find()) {
            tag("invalid-instruction", matcher);
            return true;
        }

        return false;
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
        if (rrx) return false;
        if (subContext == SubContext.SHIFT) return matchImmediateOrRegister();

        if (subContext == SubContext.IMMEDIATE) return false;

        Matcher matcher = SHIFT_PATTERN.matcher(text);

        if (matcher.find()) {
            rrx = matcher.group().strip().equalsIgnoreCase("RRX");

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

    private boolean matchOffset() {
        if (matchImmediate()) return true;
        else {
            Matcher matcher = REGISTER_SIGN_PATTERN.matcher(text);

            if (matcher.find()) {
                tag("immediate", matcher);
            }
            return matchRegister();
        }
    }

    private boolean matchPseudoInstruction() {
        Matcher matcher = PSEUDO_INSTRUCTION_PATTERN.matcher(text);

        if (matcher.find()) {
            tag("pseudo-instruction", matcher);
            subContext = SubContext.PSEUDO;
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
                        contextLength = 0;
                        yield true;
                    }
                }

                case "}" -> {
                    if (!brace) yield false;
                    else {
                        tag("brace", matcher);
                        brace = false;
                        subContext = SubContext.NONE;
                        contextLength = 0;
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
                    if (bracket) yield false;
                    else {
                        tag("bracket", matcher);
                        bracket = true;
                        subContext = SubContext.NONE;
                        contextLength = 0;
                        yield true;
                    }
                }

                case "]", "]!" -> {
                    if (!bracket) yield false;
                    else {
                        tag("bracket", matcher);
                        bracket = false;
                        subContext = SubContext.NONE;
                        contextLength = 0;
                        yield true;
                    }
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
                        subContext = SubContext.ADDRESS;
                        return true;
                    } else if (matchSubSeparator()) {
                        subContext = SubContext.PRIMARY;
                    } else return false;
                }

                case PRIMARY -> {
                    if (matchOffset()) {
                        subContext = subContext == SubContext.IMMEDIATE ? subContext : SubContext.SECONDARY;
                        return true;
                    } else return false;
                }

                case SECONDARY -> {
                    if (matchBracket()) {
                        subContext = SubContext.ADDRESS;
                        return true;
                    } else if (matchSubSeparator()) {
                        subContext = SubContext.TERTIARY;
                    } else return false;
                }

                case TERTIARY -> {
                    if (!matchShift()) return false;
                }

                case SHIFT -> {
                    if (rrx && matchBracket()) {
                        subContext = SubContext.ADDRESS;
                        return true;
                    } else {
                        return matchImmediate();
                    }
                }

                case IMMEDIATE -> {
                    if (matchBracket()) {
                        subContext = SubContext.ADDRESS;
                        return true;
                    } else {
                        return false;
                    }
                }
            }

            return true;
        } else return matchPseudoInstruction();
    }

    private boolean matchRegisterAddress() {
        if (bracket || matchBracket()) {
            if (subContext == SubContext.NONE) {
                return matchRegister();
            } else if (subContext == SubContext.REGISTER) {
                if (matchBracket()) {
                    subContext = SubContext.ADDRESS;
                    return true;
                } else return false;
            } else return false;
        }

        return false;
    }

    private boolean matchRegisterArray() {
        if (brace || matchBrace()) {
            switch (subContext) {
                case NONE, PRIMARY -> {
                    if (!matchRegister()) return false;
                }

                case REGISTER -> {
                    if (matchBrace()) {
                        subContext = SubContext.REGISTER_ARRAY;
                        return true;
                    } else if (matchSubSeparator()) {
                        subContext = SubContext.NONE;
                    } else if (matchRegisterRangeSeparator()) {
                        subContext = SubContext.PRIMARY;
                        contextLength = 0;
                    } else return false;
                }
            }

            return true;
        } else return false;
    }

    private boolean matchLabelArgument() {
        if (subContext == SubContext.LABEL_REF) return false;
        Matcher matcher = LABEL_ARGUMENT_BIS_PATTERN.matcher(text);

        if (matcher.find()) {
            matcher = LABEL_ARGUMENT_PATTERN.matcher(matcher.group("LABEL"));
            matcher.find();
            subContext = SubContext.LABEL_REF;
            String label = matcher.group().toUpperCase().strip();

            addReferences.add(label);

            if (labels.containsValue(label) || globals.containsValue(label) || addLabels.equals(label)) {
                tag("label-ref", matcher);
            } else {
                tagError("label-ref", matcher);
                subContext = SubContext.INVALID_LABEL_REF;
            }

            return true;
        } else {
            matcher = DIRECTIVE_VALUE_PATTERN.matcher(text);

            if (matcher.find()) {
                tag("immediate", matcher);
                return true;
            }
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
            subContext = subContext == SubContext.IMMEDIATE ? subContext : SubContext.NONE;
            context = context.getNext();
            argType = instruction.getArgumentType(context.getIndex());
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

    private boolean matchRegisterRangeSeparator() {
        Matcher matcher = RANGE_SEPARATOR.matcher(text);

        if (matcher.find()) {
            tag("register", matcher);
            return true;
        }

        return false;
    }

    private boolean matchSection() {
        Matcher matcher = SECTION_PATTERN.matcher(text);

        if (matcher.find()) {
            String s = matcher.group("SECTION").toUpperCase().strip();
            if (!s.equalsIgnoreCase("Section")) {
                currentSection = addSection = Section.valueOf(s);
                context = Context.SECTION;
            }
            tag("section", matcher);
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
                            addSymbols = matcher.group().strip();

                            caseTranslationTable.add(new CaseIndependentEntry(addSymbols));
                            addSymbols = addSymbols.toUpperCase();
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
                    addGlobals = matcher.group().strip();

                    caseTranslationTable.add(new CaseIndependentEntry(addGlobals));
                    addGlobals = addGlobals.toUpperCase();

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

    private void tag(String highlight, int length) {
        contextLength = length;
        addSpan(Collections.singleton(highlight), contextLength);
        find.replaceAll(f -> f.offset(contextLength));
        find.removeIf(Objects::isNull);
        text = text.substring(contextLength);
        cursorPos -= contextLength;
    }

    private void tag(String highlight, Matcher matcher) {
        tag(highlight, matcher.end());
    }

    private void tagBlank(Matcher matcher) {
        tagBlank(matcher.end());
    }

    private void tagBlank(int length) {
        contextLength = length;
        addSpan(Collections.emptyList(), contextLength);
        find.replaceAll(f -> f.offset(contextLength));
        find.removeIf(Objects::isNull);
        text = text.substring(contextLength);
        cursorPos -= contextLength;
    }

    private void tagError(String highlight, Matcher matcher) {
        tagError(highlight, matcher.end());
    }

    private void tagError(String highlight, int length) {
        contextLength = length;
        addSpan(List.of(highlight, "error-secondary"), contextLength);
        find.replaceAll(f -> f.offset(contextLength));
        find.removeIf(Objects::isNull);
        text = text.substring(contextLength);
        cursorPos -= contextLength;
    }

    private void tagError() {
        Matcher matcher = ERROR_PATTERN.matcher(text);

        if (matcher.find()) {
            int lcl = contextLength;
            contextLength = matcher.end();
            addSpan(Collections.singleton("error"), contextLength);
            find.replaceAll(f -> f.offset(contextLength));
            find.removeIf(Objects::isNull);
            text = text.substring(contextLength);
            cursorPos -= contextLength;

            if (errorOnLastIter) {
                contextLength += lcl;
            }

            error = true;
        } else {
            matcher = GENERAL_SEPARATOR_PATTERN.matcher(text);

            if (matcher.find()) {
                tag("error", matcher);
                error = true;
            }
        }
    }

    /**
     * Update label/symbol references.
     *
     * @param name the name of the symbol/label
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
        JArmEmuApplication.getEditorController().getFileEditors().forEach(editor -> {
            if (editor != this.editor) ((SmartHighlighter) editor.getRealTimeParser()).updateReferences(name);
        });
    }

    /**
     * @return the current section (as the highlighter memorized)
     */
    private Section getCurrentSection() {
        Section current = Section.NONE;

        for (Map.Entry<Integer, Section> entry : sections.entrySet()) {
            if (entry.getKey() > line) {
                break;
            } else {
                current = entry.getValue();
            }
        }

        return current;
    }

    /**
     * @return the starting line of the next section
     */
    private int getNextSectionLine() {
        for (int i : sections.keySet()) {
            if (i > line) {
                return i;
            }
        }

        return editor.getTotalLineNumber();
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
        Set<String> rtn = new HashSet<>(labels.values());
        rtn.addAll(globals.values());
        return rtn;
    }

    @Override
    public Set<String> getSymbols() {
        return new HashSet<>(symbols.values());
    }

    @Override
    public Set<CaseIndependentEntry> getCaseTranslationTable() {
        return caseTranslationTable;
    }

    @Override
    public boolean lineDefinesLabel(int line) {
        return labels.containsKey(line);
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
    public Object getLock() {
        return LOCK;
    }
}
