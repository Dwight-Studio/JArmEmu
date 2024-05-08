package fr.dwightstudio.jarmemu.base.gui.controllers;

import atlantafx.base.controls.Popover;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.sun.javafx.collections.ObservableListWrapper;
import fr.dwightstudio.jarmemu.base.Status;
import fr.dwightstudio.jarmemu.base.asm.Directive;
import fr.dwightstudio.jarmemu.base.asm.Section;
import fr.dwightstudio.jarmemu.base.asm.parser.regex.ASMParser;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.editor.Context;
import fr.dwightstudio.jarmemu.base.gui.editor.SubContext;
import fr.dwightstudio.jarmemu.base.util.RegisterUtils;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.PopupControl;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Pane;
import javafx.stage.PopupWindow;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fxmisc.richtext.model.TwoDimensional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutocompletionController implements Initializable {

    private static final Pattern LAST_WORD_PATTERN = Pattern.compile("\\b[^ #=+\\-/()\\[\\]{}]+$");

    private final Object LOCK = new Object();
    private final Logger logger = Logger.getLogger(AutocompletionController.class.getSimpleName());

    private ArrayList<String> list;
    private ObservableListWrapper<String> wrappedList;
    private ListView<String> listView;
    private PopupControl popup;
    private boolean reopened;
    private String currentWord;
    private boolean considerWord;

    // Data
    private FileEditor editor;
    private int line;
    private Section section;
    private Context context;
    private SubContext subContext;
    private int contextLength;
    private String command;
    private String argType;
    private boolean bracket;
    private boolean brace;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        list = new ArrayList<>();
        wrappedList = new ObservableListWrapper<>(new ArrayList<>());

        listView = new ListView<>(wrappedList);
        listView.setEditable(false);
        listView.getStyleClass().addAll(Styles.DENSE, Tweaks.EDGE_TO_EDGE);
        listView.setMinHeight(0);
        listView.setMaxHeight(200);
        listView.setMinWidth(0);
        listView.setMaxWidth(400);
        listView.setFixedCellSize(24);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        listView.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case ENTER, TAB -> applyAutocomplete();
                case LEFT -> editor.getCodeArea().moveTo(editor.getCodeArea().getCaretPosition() - 1);
                case RIGHT -> editor.getCodeArea().moveTo(editor.getCodeArea().getCaretPosition() + 1);
                case ESCAPE -> close();
            }
        });

        listView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() >= 2) {
                applyAutocomplete();
            }
        });

        Pane listContainer = new Pane(listView);
        listContainer.getStyleClass().add("autocomplete");

        Pane back = new Pane(listContainer);
        back.getStyleClass().add("popup");

        popup = new PopupControl();
        popup.getScene().setRoot(back);
        popup.setHideOnEscape(true);
        popup.setAutoFix(false);
        popup.setAutoHide(true);
    }

    public void update(FileEditor editor, int line, Section section, Context context, SubContext subContext, int lastTagLength, String command, String argType, boolean bracket, boolean brace) {
        this.editor = editor;
        this.line = line;
        this.section = section;
        this.context = context;
        this.subContext = subContext;
        this.contextLength = lastTagLength;
        this.command = command;
        this.argType = argType;
        this.bracket = bracket;
        this.brace = brace;

        if (JArmEmuApplication.getStatus() == Status.EDITING) {
            update();
        }
    }

    private void update() {
        synchronized (LOCK) {
            list.clear();

            if (editor.getCodeArea().getParagraph(editor.getCodeArea().getCurrentParagraph()).getText().isBlank()) {
                close();
                return;
            }

            if (section == Section.TEXT) {
                switch (context) {
                    case NONE, INSTRUCTION, LABEL -> list.addAll(Arrays.asList(ASMParser.INSTRUCTIONS));

                    case INSTRUCTION_ARGUMENT_1, INSTRUCTION_ARGUMENT_2, INSTRUCTION_ARGUMENT_3,
                         INSTRUCTION_ARGUMENT_4 -> {
                        switch (argType) {
                            case "RegisterArgument" -> {
                                if (subContext != SubContext.REGISTER) {
                                    for (RegisterUtils value : RegisterUtils.values()) {
                                        if (!value.isSpecial()) list.add(value.name());
                                    }
                                }
                            }

                            case "RegisterWithUpdateArgument" -> {
                                if (subContext != SubContext.REGISTER) {
                                    for (RegisterUtils value : RegisterUtils.values()) {
                                        if (!value.isSpecial()) {
                                            list.add(value.name());
                                            list.add(value.name() + "!");
                                        }
                                    }
                                }
                            }

                            case "ImmediateArgument", "RotatedImmediateArgument" -> {
                                if (subContext != SubContext.IMMEDIATE) list.add("#");
                                else {
                                    list.addAll(editor.getRealTimeParser().getSymbols());
                                    considerWord = true;
                                }
                            }

                            case "ImmediateOrRegisterArgument", "RotatedImmediateOrRegisterArgument" -> {
                                if (subContext != SubContext.REGISTER && subContext != SubContext.IMMEDIATE) {
                                    for (RegisterUtils value : RegisterUtils.values()) {
                                        if (!value.isSpecial()) list.add(value.name());
                                    }

                                    list.add("#");
                                } else if (subContext == SubContext.IMMEDIATE) {
                                    list.addAll(editor.getRealTimeParser().getSymbols());
                                    considerWord = true;
                                }
                            }

                            case "ShiftArgument" -> {
                                if (subContext == SubContext.SHIFT) list.add("#");
                                else if (subContext != SubContext.IMMEDIATE)
                                    list.add("LSL");
                                    list.add("LSR");
                                    list.add("ASR");
                                    list.add("ROR");
                                    list.add("RRX");
                            }

                            case "RegisterAddressArgument" -> {
                                if (subContext != SubContext.ADDRESS) {
                                    for (RegisterUtils value : RegisterUtils.values()) {
                                        if (!value.isSpecial()) list.add("[" + value.name() + "]");
                                    }
                                }
                            }

                            case "RegisterArrayArgument" -> {
                                if (brace) {
                                    switch (subContext) {
                                        case NONE -> {
                                            for (RegisterUtils value : RegisterUtils.values()) {
                                                if (!value.isSpecial()) {
                                                    list.add(value.name());
                                                    list.add(value.name() + "-");
                                                }
                                            }
                                        }

                                        case PRIMARY -> {
                                            for (RegisterUtils value : RegisterUtils.values()) {
                                                if (!value.isSpecial()) list.add(value.name());
                                            }
                                        }
                                    }
                                } else {
                                    list.add("{");
                                }
                            }

                            case "LabelArgument" -> {
                                if (subContext != SubContext.LABEL_REF)
                                    list.addAll(editor.getRealTimeParser().getAccessibleLabels());
                            }

                            case "LabelOrRegisterArgument" -> {
                                list.addAll(editor.getRealTimeParser().getAccessibleLabels());
                                for (RegisterUtils value : RegisterUtils.values()) {
                                    if (!value.isSpecial()) list.add("[" + value.name() + "]");
                                }
                            }

                            case "AddressArgument" -> {
                                if (subContext != SubContext.ADDRESS && subContext != SubContext.PSEUDO) {
                                    if (bracket) {
                                        switch (subContext) {
                                            case NONE -> {
                                                for (RegisterUtils value : RegisterUtils.values()) {
                                                    if (!value.isSpecial()) list.add(value.name());
                                                }
                                            }

                                            case PRIMARY -> {
                                                for (RegisterUtils value : RegisterUtils.values()) {
                                                    if (!value.isSpecial()) list.add(value.name());
                                                }

                                                list.add("#");
                                            }

                                            case TERTIARY -> {
                                                list.add("LSL");
                                                list.add("LSR");
                                                list.add("ASR");
                                                list.add("ROR");
                                                list.add("RRX");
                                            }

                                            case SHIFT -> list.add("#");

                                            case IMMEDIATE -> {
                                                list.addAll(editor.getRealTimeParser().getSymbols());
                                                considerWord = true;
                                            }
                                        }
                                    } else {
                                        list.add("[");
                                        list.add("=");
                                    }
                                } else if (subContext == SubContext.PSEUDO) {
                                    list.addAll(editor.getRealTimeParser().getSymbols());
                                    considerWord = true;
                                }
                            }
                        }
                    }
                }
            }

            if (section.isDataRelatedSection() || section == Section.NONE || section == Section.TEXT) {
                switch (context) {
                    case NONE, LABEL -> {
                        for (Directive directive : Directive.values()) {
                            list.add("." + directive.name());
                        }

                        for (Section sec : Section.values()) {
                            if (sec != Section.NONE) list.add("." + sec.name());
                        }
                    }

                    case DIRECTIVE_ARGUMENTS -> {
                        switch (command.toUpperCase()) {
                            case "SET", "EQU", "EQUIV", "EQV" -> {
                                if (Objects.requireNonNull(subContext) == SubContext.SECONDARY) {
                                    list.addAll(editor.getRealTimeParser().getSymbols());
                                    considerWord = true;
                                }
                            }
                        }
                    }
                }
            }

            currentWord = getCurrentContext();

            if (considerWord) {
                Matcher matcher = LAST_WORD_PATTERN.matcher(currentWord);
                if (matcher.find()) {
                    currentWord = matcher.group();
                }
            }

            currentWord = currentWord.strip();

            //System.out.println(section + " " + context + ":" + subContext + ";" + command + ";" + argType + "{" + currentWord + "}");
            //System.out.println(list);

            switch (currentWord) {
                case "" -> {}
                case "{", "[" -> currentWord = "";
                default -> {
                    final String finalCurrentWord = currentWord;
                    list.removeIf(s -> !s.toLowerCase().startsWith(finalCurrentWord.toLowerCase()) || s.isBlank());
                    list.removeIf(s -> s.equalsIgnoreCase(finalCurrentWord));
                }
            }

            list.replaceAll(String::toLowerCase);
            list.sort(Comparator.comparingInt(String::length));

            editor.getRealTimeParser().getCaseTranslationTable().forEach(s -> list.replaceAll(p -> s.equals(p) ? s.string() : p));

            if (!list.isEmpty()) Platform.runLater(this::show);
            else close();
        }
    }

    private void show() {
        if (!list.isEmpty()) {
            int start = getCurrentContextStart();
            int end = getCurrentContextEnd();

            (start != end ? editor.getCodeArea().getCharacterBoundsOnScreen(start, end) : editor.getCodeArea().getCaretBounds()).ifPresent(bounds -> {
                wrappedList.clear();
                wrappedList.addAll(list);

                double height = wrappedList.size() * listView.getFixedCellSize() + 20;
                listView.setPrefHeight(height);

                if (bounds.getMaxY() + Math.min(height, 200) > editor.getCodeArea().localToScene(editor.getCodeArea().getBoundsInLocal()).getMaxY()) {
                    popup.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_BOTTOM_LEFT);
                    if (!popup.isShowing()) popup.show(JArmEmuApplication.getStage());
                    popup.setAnchorY(bounds.getMinY());
                } else {
                    popup.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_LEFT);
                    if (!popup.isShowing()) popup.show(JArmEmuApplication.getStage());
                    popup.setAnchorY(bounds.getMaxY());
                }

                popup.setAnchorX(bounds.getMinX() - 16);

                listView.requestFocus();
                listView.getSelectionModel().selectFirst();
                listView.scrollTo(0);
                reopened = true;
            });
        } else if (popup.isShowing()) popup.hide();
    }

    /**
     * @return the current context string
     */
    private String getCurrentContext() {
        int start = editor.getCodeArea().getCaretColumn() - contextLength;
        int stop = editor.getCodeArea().getCaretColumn();
        if (start < 0 || stop - start <= 0) return "";
        return editor.getCodeArea().getParagraph(editor.getCodeArea().getCurrentParagraph()).substring(start, stop);
    }

    /**
     * @return current context starting pos
     */
    private int getCurrentContextStart() {
        int start = editor.getCodeArea().getCaretColumn() - currentWord.length();
        if (start < 0) {
            return editor.getCodeArea().getCaretPosition();
        } else {
            return editor.getCodeArea().getAbsolutePosition(line, start);
        }
    }

    /**
     * @return current context ending pos
     */
    private int getCurrentContextEnd() {
        return editor.getCodeArea().getAbsolutePosition(line, editor.getCodeArea().getCaretColumn());
    }

    /**
     * Selects current context/word
     */
    private void selectCurrentWord() {
        editor.getCodeArea().selectRange(getCurrentContextStart(), getCurrentContextEnd());
    }

    /**
     * Updates autocompletion popover when scrolling
     */
    public void scroll() {
        close();
    }

    /**
     * Closes autocompletion popover
     */
    public void close() {
        Platform.runLater(() -> {
            if (popup.isShowing()) popup.hide();
        });
    }

    /**
     * Updates autocompletion popover when moving caret
     */
    public void caretMoved() {
        synchronized (LOCK) {
            if (editor == null) return;
            if (editor.getCodeArea().offsetToPosition(editor.getCodeArea().getCaretPosition(), TwoDimensional.Bias.Forward).getMajor() != line)
                close();

            if (!reopened) close();
            else reopened = false;
        }
    }

    /**
     * Autocompletes braces/brackets/parenthesis...
     *
     * @param character the character to autocomplete
     * @param pos       the caret position
     */
    public void autocompleteChar(String character, int pos) {
        final String newChar = switch (character) {
            case "[" -> "]";

            case "{" -> "}";

            case "\"" -> "\"";

            case "(" -> ")";

            default -> "";
        };

        if (!newChar.isEmpty()) {
            synchronized (LOCK) {
                editor.getRealTimeParser().cancelLine(line);
                editor.getCodeArea().insertText(pos, newChar);
                editor.getCodeArea().moveTo(pos);
                reopened = true;
            }
        }
    }

    /**
     * Applies autocompletion according to selected item
     */
    private void applyAutocomplete() {
        String selected = listView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            int pos = editor.getCodeArea().getCaretPosition();

            if (currentWord.isBlank()) {
                editor.getCodeArea().selectRange(pos, pos);
            } else {
                selectCurrentWord();
            }
            editor.getCodeArea().replaceSelection(selected);

            if (selected.length() == 1) {
                autocompleteChar(selected, pos + 1);
            }
        }
    }
}
