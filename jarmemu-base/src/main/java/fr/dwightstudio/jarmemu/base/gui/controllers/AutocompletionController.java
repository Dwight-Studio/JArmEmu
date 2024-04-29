package fr.dwightstudio.jarmemu.base.gui.controllers;

import atlantafx.base.controls.Popover;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.sun.javafx.collections.ObservableListWrapper;
import fr.dwightstudio.jarmemu.base.Status;
import fr.dwightstudio.jarmemu.base.asm.directive.Directive;
import fr.dwightstudio.jarmemu.base.asm.directive.Section;
import fr.dwightstudio.jarmemu.base.asm.instruction.Condition;
import fr.dwightstudio.jarmemu.base.asm.instruction.DataMode;
import fr.dwightstudio.jarmemu.base.asm.instruction.Instruction;
import fr.dwightstudio.jarmemu.base.asm.instruction.UpdateMode;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AutocompletionController implements Initializable {

    private Timeline openingTimeline;
    private Timeline idlingTimeline;
    private ObservableListWrapper<String> list;
    private ListView<String> listView;
    private Popover popover;

    // Data
    private FileEditor editor;
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
                case ENTER, TAB -> {
                    String selected = listView.getSelectionModel().getSelectedItem();

                    if (selected != null) {
                        if (getCurrentContext().isBlank() || context == Context.INSTRUCTION) {
                            int pos = editor.getCodeArea().getCaretPosition();
                            editor.getCodeArea().selectRange(pos, pos);
                        } else {
                            selectCurrentContext();
                        }
                        editor.getCodeArea().replaceSelection(selected);
                    }
                }

                case ESCAPE -> close();
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

        openingTimeline = new Timeline(new KeyFrame(Duration.millis(250), event -> update(false)));
        idlingTimeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> update(true)));
    }

    public void update(FileEditor editor, Section section, Context context, SubContext subContext, int lastTagLength, String command, String argType, boolean bracket, boolean brace) {
        this.editor = editor;
        this.section = section;
        this.context = context;
        this.subContext = subContext;
        this.contextLength = lastTagLength;
        this.command = command;
        this.argType = argType;
        this.bracket = bracket;
        this.brace = brace;

        if (JArmEmuApplication.getInstance().status.get() == Status.EDITING) {
            openingTimeline.stop();
            idlingTimeline.stop();

            openingTimeline.play();
            idlingTimeline.play();
        }
    }

    private void update(boolean idling) {
        list.clear();

        if (editor.getCodeArea().getParagraph(editor.getCodeArea().getCurrentParagraph()).getText().isBlank() && !idling) {
            return;
        } else {
            idlingTimeline.stop();
        }

        if (section == Section.TEXT) {
            switch (context) {
                case NONE -> {
                    for (Instruction instruction : Instruction.values()) {
                        list.add(instruction.name());
                    }

                    for (Directive directive : Directive.values()) {
                        list.add("." + directive.name());
                    }

                    for (Section sec : Section.values()) {
                        if (sec != Section.NONE) list.add("." + sec.name());
                    }
                }

                case INSTRUCTION -> {
                    switch (subContext) {
                        case NONE -> {
                            for (Condition condition : Condition.values()) {
                                list.add(condition.name());
                            }

                            for (DataMode dataMode : DataMode.values()) {
                                list.add(dataMode.toString());
                            }

                            for (UpdateMode updateMode : UpdateMode.values()) {
                                list.add(updateMode.name());
                            }

                            list.add("S");
                        }

                        case CONDITION -> {
                            for (DataMode dataMode : DataMode.values()) {
                                list.add(dataMode.toString());
                            }

                            for (UpdateMode updateMode : UpdateMode.values()) {
                                list.add(updateMode.name());
                            }

                            list.add("S");
                        }
                    }
                }

                case INSTRUCTION_ARGUMENT_1, INSTRUCTION_ARGUMENT_2, INSTRUCTION_ARGUMENT_3, INSTRUCTION_ARGUMENT_4 -> {
                    switch (argType) {
                        case "RegisterArgument", "RegisterWithUpdateArgument" -> {
                            if (subContext != SubContext.REGISTER) {
                                for (RegisterUtils value : RegisterUtils.values()) {
                                    list.add(value.name());
                                }
                            }
                        }

                        case "ImmediateArgument", "RotatedImmediateArgument" -> {
                            if (subContext != SubContext.IMMEDIATE) list.add("#");
                            else list.addAll(editor.getRealTimeParser().getSymbols());
                        }

                        case "RotatedOrRegisterArgument", "RotatedImmediateOrRegisterArgument" -> {
                            if (subContext != SubContext.REGISTER && subContext != SubContext.IMMEDIATE) {
                                for (RegisterUtils value : RegisterUtils.values()) {
                                    list.add(value.name());
                                }

                                list.add("#");
                            }
                        }

                        case "ShiftArgument" -> {
                            if (subContext == SubContext.SHIFT) list.add("#");
                            else if (subContext != SubContext.IMMEDIATE) list.addAll("LSL", "LSR", "ASR", "ROR", "RRX");
                        }

                        case "RegisterAddressArgument" -> {
                            if (subContext != SubContext.ADDRESS) {
                                for (RegisterUtils value : RegisterUtils.values()) {
                                    list.add("[" + value.name() + "]");
                                }
                            }
                        }

                        case "RegisterArrayArgument" -> {
                            if (brace) {
                                if (subContext != SubContext.REGISTER) {
                                    for (RegisterUtils value : RegisterUtils.values()) {
                                        list.add(value.name());
                                    }
                                }
                            }
                        }

                        case "LabelArgument" -> {
                            if (subContext != SubContext.LABEL_REF)
                                list.addAll(editor.getRealTimeParser().getAccessibleLabels());
                        }

                        case "LabelOrRegisterArgument" -> {
                            list.addAll(editor.getRealTimeParser().getAccessibleLabels());
                            for (RegisterUtils value : RegisterUtils.values()) {
                                list.add("[" + value.name() + "]");
                            }
                        }

                        case "AddressArgument" -> {
                            if (subContext != SubContext.ADDRESS) {
                                if (bracket) {
                                    switch (subContext) {
                                        case NONE -> {
                                            for (RegisterUtils value : RegisterUtils.values()) {
                                                list.add(value.name());
                                            }
                                        }

                                        case PRIMARY -> {
                                            for (RegisterUtils value : RegisterUtils.values()) {
                                                list.add(value.name());
                                            }

                                            list.add("#");
                                        }

                                        case TERTIARY -> list.addAll("LSL", "LSR", "ASR", "ROR", "RRX");

                                        case SHIFT -> list.add("#");
                                    }
                                }
                            }
                        }
                    }
                }
            }

            String currentWord = (context == Context.INSTRUCTION) ? "" : getCurrentContext().strip();
            if (!currentWord.isEmpty()) {
                list.removeIf(s -> !s.toUpperCase().startsWith(currentWord.toUpperCase()));
                list.removeIf(s -> s.equalsIgnoreCase(currentWord));
            }
        } else if (section.isDataRelatedSection() || section == Section.NONE) {
            switch (context) {
                case NONE -> {
                    for (Directive directive : Directive.values()) {
                        list.add("." + directive.name());
                    }

                    for (Section sec : Section.values()) {
                        if (sec != Section.NONE) list.add("." + sec.name());
                    }
                }
            }

            String currentWord = getCurrentContext().strip();
            if (!currentWord.isEmpty()) {
                list.removeIf(s -> !s.toUpperCase().startsWith(currentWord.toUpperCase()));
                list.removeIf(s -> s.equalsIgnoreCase(currentWord));
            }
        }

        list.replaceAll(String::toLowerCase);

        show();
    }

    private void show() {
        if (!list.isEmpty()) {
            editor.getCodeArea().getCaretBounds().ifPresent(bounds -> {
                double height = list.size() * listView.getFixedCellSize();
                listView.setPrefHeight(height);

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
        }
    }

    public String getCurrentContext() {
        int start = editor.getCodeArea().getCaretColumn() - contextLength;
        int stop = editor.getCodeArea().getCaretColumn();
        if (start < 0 || stop - start <= 0) return "";
        return editor.getCodeArea().getParagraph(editor.getCodeArea().getCurrentParagraph()).substring(start, stop);
    }

    public void selectCurrentContext() {
        int start = editor.getCodeArea().getCaretColumn() - contextLength;
        int stop = editor.getCodeArea().getCaretColumn();

        if (start < 0 || stop - start <= 0) {
            int pos = editor.getCodeArea().getCaretPosition();
            editor.getCodeArea().selectRange(pos, pos);
        } else {
            int line = editor.getCodeArea().getCurrentParagraph();
            editor.getCodeArea().selectRange(editor.getCodeArea().getAbsolutePosition(line, start), editor.getCodeArea().getAbsolutePosition(line, stop));
        }
    }

    public void scroll() {
        close();
    }

    public void close() {
        Platform.runLater(() -> {
            if (popover.isShowing()) popover.hide();
        });
    }
}
