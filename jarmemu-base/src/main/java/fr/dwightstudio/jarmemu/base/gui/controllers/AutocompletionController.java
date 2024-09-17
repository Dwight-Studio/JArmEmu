package fr.dwightstudio.jarmemu.base.gui.controllers;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.sun.javafx.collections.ObservableListWrapper;
import fr.dwightstudio.jarmemu.base.Status;
import fr.dwightstudio.jarmemu.base.asm.Directive;
import fr.dwightstudio.jarmemu.base.asm.Section;
import fr.dwightstudio.jarmemu.base.asm.parser.regex.ASMParser;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.editor.SmartContext;
import fr.dwightstudio.jarmemu.base.gui.editor.SubContext;
import fr.dwightstudio.jarmemu.base.util.RegisterUtils;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.PopupControl;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Pane;
import javafx.stage.PopupWindow;
import org.fxmisc.richtext.model.TwoDimensional;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutocompletionController implements Initializable {

    private static final Pattern LAST_WORD_PATTERN = Pattern.compile("\\b[^ #=+\\-/()\\[\\]{}]+$");

    private final Object LOCK = new Object();

    private SmartContext sc;
    
    private ArrayList<String> list;
    private ObservableListWrapper<String> wrappedList;
    private ListView<String> listView;
    private PopupControl popup;
    private boolean reopened;
    private String currentWord;
    private boolean considerWord;

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
                case LEFT -> {
                    sc.editor().getCodeArea().moveTo(sc.editor().getCodeArea().getCaretPosition() - 1);
                    close();
                }
                case RIGHT -> {
                    sc.editor().getCodeArea().moveTo(sc.editor().getCodeArea().getCaretPosition() + 1);
                    close();
                }
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

        sc = new SmartContext();
    }

    public void update(SmartContext smartContext) {
        this.considerWord = false;

        this.sc = smartContext;

        if (JArmEmuApplication.getStatus() == Status.EDITING && JArmEmuApplication.getSettingsController().getAutoCompletion()) {
            update();
        }
    }

    private void update() {
        synchronized (LOCK) {
            list.clear();

            if (sc.editor().getCodeArea().getParagraph(sc.editor().getCodeArea().getCurrentParagraph()).getText().isBlank()) {
                close();
                return;
            }

            if (sc.section() == Section.TEXT) {
                switch (sc.context()) {
                    case NONE, INSTRUCTION, LABEL -> {
                        list.addAll(Arrays.asList(ASMParser.INSTRUCTIONS));
                        considerWord = true;
                    }

                    case INSTRUCTION_ARGUMENT_1, INSTRUCTION_ARGUMENT_2, INSTRUCTION_ARGUMENT_3,
                         INSTRUCTION_ARGUMENT_4 -> {
                        switch (sc.argType()) {
                            case "RegisterArgument" -> {
                                if (sc.subContext() != SubContext.REGISTER) {
                                    for (RegisterUtils value : RegisterUtils.values()) {
                                        if (!value.isSpecial()) list.add(value.name());
                                    }
                                }
                            }

                            case "RegisterWithUpdateArgument" -> {
                                if (sc.subContext() != SubContext.REGISTER) {
                                    for (RegisterUtils value : RegisterUtils.values()) {
                                        if (!value.isSpecial()) {
                                            list.add(value.name());
                                            list.add(value.name() + "!");
                                        }
                                    }
                                }
                            }

                            case "ImmediateArgument", "SmallImmediateArgument", "LongImmediateArgument", "RotatedImmediateArgument" -> {
                                if (sc.subContext() != SubContext.IMMEDIATE) list.add("#");
                                else {
                                    list.addAll(sc.editor().getRealTimeParser().getSymbols());
                                    considerWord = true;
                                }
                            }

                            case "ImmediateOrRegisterArgument", "LongImmediateOrRegisterArgument", "RotatedImmediateOrRegisterArgument", "PostOffsetArgument" -> {
                                if (sc.subContext() != SubContext.REGISTER && sc.subContext() != SubContext.IMMEDIATE) {
                                    for (RegisterUtils value : RegisterUtils.values()) {
                                        if (!value.isSpecial()) list.add(value.name());
                                    }

                                    list.add("#");
                                } else if (sc.subContext() == SubContext.IMMEDIATE) {
                                    list.addAll(sc.editor().getRealTimeParser().getSymbols());
                                    considerWord = true;
                                }
                            }

                            case "ShiftArgument" -> {
                                if (sc.rrx()) return;
                                if (sc.subContext() == SubContext.SHIFT) {
                                    for (RegisterUtils value : RegisterUtils.values()) {
                                        if (!value.isSpecial()) list.add(value.name());
                                    }

                                    list.add("#");
                                } else if (sc.subContext() != SubContext.IMMEDIATE && sc.subContext() != SubContext.REGISTER) {
                                    list.add("LSL");
                                    list.add("LSR");
                                    list.add("ASR");
                                    list.add("ROR");
                                    list.add("RRX");
                                }
                            }

                            case "RegisterAddressArgument" -> {
                                if (sc.subContext() != SubContext.ADDRESS) {
                                    for (RegisterUtils value : RegisterUtils.values()) {
                                        if (!value.isSpecial()) list.add("[" + value.name() + "]");
                                    }
                                }
                            }

                            case "RegisterArrayArgument" -> {
                                if (sc.brace()) {
                                    switch (sc.subContext()) {
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
                                if (sc.subContext() != SubContext.LABEL_REF)
                                    list.addAll(sc.editor().getRealTimeParser().getAccessibleLabels());
                            }

                            case "LabelOrRegisterArgument" -> {
                                list.addAll(sc.editor().getRealTimeParser().getAccessibleLabels());
                                for (RegisterUtils value : RegisterUtils.values()) {
                                    if (!value.isSpecial()) list.add(value.name());
                                }
                            }

                            case "AddressArgument" -> {
                                if (sc.subContext() != SubContext.ADDRESS && sc.subContext() != SubContext.PSEUDO) {
                                    if (sc.bracket()) {
                                        switch (sc.subContext()) {
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

                                            case SHIFT -> {
                                                if (!sc.rrx()) list.add("#");
                                            }

                                            case IMMEDIATE -> {
                                                list.addAll(sc.editor().getRealTimeParser().getSymbols());
                                                considerWord = true;
                                            }
                                        }
                                    } else {
                                        list.add("[");
                                        list.add("=");
                                    }
                                } else if (sc.subContext() == SubContext.PSEUDO) {
                                    list.addAll(sc.editor().getRealTimeParser().getSymbols());
                                    considerWord = true;
                                }
                            }
                        }
                    }
                }
            }

            if (sc.section().isDataRelatedSection() || sc.section() == Section.TEXT || sc.section() == Section.NONE) {
                switch (sc.context()) {
                    case NONE, LABEL -> {
                        for (Directive directive : Directive.values()) {
                            list.add("." + directive.name());
                        }

                        for (Section sec : Section.values()) {
                            if (sec != Section.NONE) list.add("." + sec.name());
                        }
                    }

                    case DIRECTIVE_ARGUMENTS -> {
                        switch (sc.command().toUpperCase()) {
                            case "SET", "EQU", "EQUIV", "EQV" -> {
                                if (Objects.requireNonNull(sc.subContext()) == SubContext.SECONDARY) {
                                    list.addAll(sc.editor().getRealTimeParser().getSymbols());
                                    considerWord = true;
                                }
                            }
                        }
                    }
                }
            }

            try {
                currentWord = getCurrentContext();
            } catch (IndexOutOfBoundsException exception) {
                currentWord = "";
            }

            if (considerWord) {
                Matcher matcher = LAST_WORD_PATTERN.matcher(currentWord);
                if (matcher.find()) {
                    currentWord = matcher.group();
                } else {
                    currentWord = "";
                }

                considerWord = false;
            }

            currentWord = currentWord.strip();

            //System.out.println("\"" + currentWord + "\"");
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

            sc.editor().getRealTimeParser().getCaseTranslationTable().forEach(s -> list.replaceAll(p -> s.equals(p) ? s.string() : p));

            if (!list.isEmpty()) Platform.runLater(this::show);
            else close();
        }
    }

    private void show() {
        if (!list.isEmpty()) {
            int start = getCurrentContextStart();
            int end = getCurrentContextEnd();

            (start != end ? sc.editor().getCodeArea().getCharacterBoundsOnScreen(start, end) : sc.editor().getCodeArea().getCaretBounds()).ifPresent(bounds -> {
                wrappedList.clear();
                wrappedList.addAll(list);

                double height = wrappedList.size() * listView.getFixedCellSize() + 20;
                listView.setPrefHeight(height);

                if (bounds.getMaxY() + Math.min(height, 200) > sc.editor().getCodeArea().localToScene(sc.editor().getCodeArea().getBoundsInLocal()).getMaxY()) {
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
        int stop = sc.editor().getCodeArea().getCaretColumn() - sc.cursorPos();
        int start = stop - sc.contextLength();
        return sc.editor().getCodeArea().getParagraph(sc.editor().getCodeArea().getCurrentParagraph()).substring(start, stop);
    }

    /**
     * @return current context starting pos
     */
    private int getCurrentContextStart() {
        int start = sc.editor().getCodeArea().getCaretColumn() - currentWord.length();
        if (start < 0) {
            return sc.editor().getCodeArea().getCaretPosition();
        } else {
            return sc.editor().getCodeArea().getAbsolutePosition(sc.line(), start);
        }
    }

    /**
     * @return current context ending pos
     */
    private int getCurrentContextEnd() {
        return sc.editor().getCodeArea().getAbsolutePosition(sc.line(), sc.editor().getCodeArea().getCaretColumn());
    }

    /**
     * Selects current context/word
     */
    private void selectCurrentWord() {
        sc.editor().getCodeArea().selectRange(getCurrentContextStart(), getCurrentContextEnd());
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
            if (sc.editor() == null) return;
            if (sc.editor().getCodeArea().offsetToPosition(sc.editor().getCodeArea().getCaretPosition(), TwoDimensional.Bias.Forward).getMajor() != sc.line())
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
                sc.editor().getRealTimeParser().cancelLine(sc.line());
                sc.editor().getCodeArea().insertText(pos, newChar);
                sc.editor().getCodeArea().moveTo(pos);
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
            int pos = sc.editor().getCodeArea().getCaretPosition();

            if (currentWord.isBlank()) {
                sc.editor().getCodeArea().selectRange(pos, pos);
            } else {
                selectCurrentWord();
            }
            sc.editor().getCodeArea().replaceSelection(selected);

            if (selected.length() == 1) {
                autocompleteChar(selected, pos + 1);
            }
        }
    }
}
