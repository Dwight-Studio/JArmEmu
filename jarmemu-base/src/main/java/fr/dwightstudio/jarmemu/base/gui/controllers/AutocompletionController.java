package fr.dwightstudio.jarmemu.base.gui.controllers;

import atlantafx.base.controls.Popover;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.sun.javafx.collections.ObservableListWrapper;
import fr.dwightstudio.jarmemu.base.Status;
import fr.dwightstudio.jarmemu.base.asm.Directive;
import fr.dwightstudio.jarmemu.base.asm.Section;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.DataMode;
import fr.dwightstudio.jarmemu.base.asm.Instruction;
import fr.dwightstudio.jarmemu.base.asm.modifier.UpdateMode;
import fr.dwightstudio.jarmemu.base.asm.parser.regex.ASMParser;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.editor.Context;
import fr.dwightstudio.jarmemu.base.gui.editor.SubContext;
import fr.dwightstudio.jarmemu.base.util.RegisterUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.util.Duration;
import org.fxmisc.richtext.model.TwoDimensional;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutocompletionController implements Initializable {

    private static final Pattern LAST_WORD_PATTERN = Pattern.compile("\\b[^ #=+\\-/()\\[\\]{}]+$");

    private Timeline idlingTimeline;
    private ObservableListWrapper<String> list;
    private ListView<String> listView;
    private Popover popover;
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
        list = new ObservableListWrapper<>(new ArrayList<>());

        listView = new ListView<>(list);
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

                case ESCAPE -> close();
            }
        });

        listView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() >= 2) {
                applyAutocomplete();
            }
        });

        popover = new Popover();
        popover.setTitle(JArmEmuApplication.formatMessage("%pop.autocomplete.title"));
        popover.setContentNode(listView);
        popover.setHeaderAlwaysVisible(false);
        popover.setDetachable(false);
        popover.setAnimated(true);
        popover.setArrowSize(0);
        popover.setHideOnEscape(true);
        popover.setArrowLocation(Popover.ArrowLocation.TOP_LEFT);

        idlingTimeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> update(true)));
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
            Platform.runLater(() -> update(false));

            idlingTimeline.stop();
            //idlingTimeline.play();
        }
    }

    private void update(boolean idling) {
        list.clear();

        if (editor.getCodeArea().getParagraph(editor.getCodeArea().getCurrentParagraph()).getText().isBlank() && !idling) {
            close();
            return;
        } else {
            idlingTimeline.stop();
        }

        if (section == Section.TEXT) {
            switch (context) {
                case NONE, INSTRUCTION, LABEL -> list.addAll(Arrays.asList(ASMParser.INSTRUCTIONS));

                case INSTRUCTION_ARGUMENT_1, INSTRUCTION_ARGUMENT_2, INSTRUCTION_ARGUMENT_3, INSTRUCTION_ARGUMENT_4 -> {
                    switch (argType) {
                        case "RegisterArgument" -> {
                            if (subContext != SubContext.REGISTER) {
                                for (RegisterUtils value : RegisterUtils.values()) {
                                    list.add(value.name());
                                }
                            }
                        }

                        case "RegisterWithUpdateArgument" -> {
                            if (subContext != SubContext.REGISTER) {
                                for (RegisterUtils value : RegisterUtils.values()) {
                                    list.add(value.name());
                                    list.add(value.name() + "!");
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
                            else if (subContext != SubContext.IMMEDIATE) list.addAll("LSL", "LSR", "ASR", "ROR", "RRX");
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

                                        case TERTIARY -> list.addAll("LSL", "LSR", "ASR", "ROR", "RRX");

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
            default ->  {
                final String finalCurrentWord = currentWord;
                list.removeIf(s -> !s.toLowerCase().startsWith(finalCurrentWord.toLowerCase()));
                list.removeIf(s -> s.equalsIgnoreCase(finalCurrentWord));
            }
        }

        list.replaceAll(String::toLowerCase);
        list.sort(Comparator.comparingInt(String::length));

        editor.getRealTimeParser().getCaseTranslationTable().forEach(s -> list.replaceAll(p -> s.equals(p) ? s.string() : p));

        show();
    }

    private void show() {
        if (!list.isEmpty()) {
            editor.getCodeArea().getCaretBounds().ifPresent(bounds -> {
                double height = list.size() * listView.getFixedCellSize();
                listView.setPrefHeight(height);

                reopened = true;
                if (bounds.getMaxY() + Math.min(height, 200) > editor.getCodeArea().localToScene(editor.getCodeArea().getBoundsInLocal()).getMaxY()) {
                    popover.setArrowLocation(Popover.ArrowLocation.BOTTOM_LEFT);
                    popover.show(editor.getCodeArea(), bounds.getCenterX(), bounds.getMinY() - bounds.getHeight() / 4);
                } else {
                    popover.setArrowLocation(Popover.ArrowLocation.TOP_LEFT);
                    popover.show(editor.getCodeArea(), bounds.getCenterX(), bounds.getMaxY() + bounds.getHeight() / 4);
                }

                listView.requestFocus();
                listView.getSelectionModel().selectFirst();
                listView.scrollTo(0);
            });
        } else close();
    }

    private String getCurrentContext() {
        int start = editor.getCodeArea().getCaretColumn() - contextLength;
        int stop = editor.getCodeArea().getCaretColumn();
        if (start < 0 || stop - start <= 0) return "";
        return editor.getCodeArea().getParagraph(editor.getCodeArea().getCurrentParagraph()).substring(start, stop);
    }

    private void selectCurrentWord() {
        int start = editor.getCodeArea().getCaretColumn() - currentWord.length();
        int stop = editor.getCodeArea().getCaretColumn();

        if (start < 0 || stop - start <= 0) {
            int pos = editor.getCodeArea().getCaretPosition();
            editor.getCodeArea().selectRange(pos, pos);
        } else {
            int line = editor.getCodeArea().getCurrentParagraph();
            editor.getCodeArea().selectRange(editor.getCodeArea().getAbsolutePosition(line, start), editor.getCodeArea().getAbsolutePosition(line, stop));
        }
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
            if (popover.isShowing()) popover.hide();
        });
    }

    /**
     * Updates autocompletion popover when moving caret
     */
    public void caretMoved() {
        if (editor == null) return;
        if (editor.getCodeArea().offsetToPosition(editor.getCodeArea().getCaretPosition(), TwoDimensional.Bias.Forward).getMajor() != line) close();

        if (!reopened) close();
        else reopened = false;
    }

    /**
     * Autocompletes braces/brackets/parenthesis...
     *
     * @param character the character to autocomplete
     * @param pos the caret position
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
            editor.getRealTimeParser().cancelLine(line);
            reopened = true;
            editor.getCodeArea().insertText(pos, newChar);
            reopened = true;
            editor.getCodeArea().moveTo(pos);
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
