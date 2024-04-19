package fr.dwightstudio.jarmemu.gui.controllers;

import atlantafx.base.controls.Popover;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.sun.javafx.collections.ObservableListWrapper;
import fr.dwightstudio.jarmemu.asm.instruction.Instruction;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.editor.Context;
import fr.dwightstudio.jarmemu.gui.editor.SubContext;
import fr.dwightstudio.jarmemu.util.RegisterUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import org.fxmisc.richtext.CodeArea;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AutocompletionController implements Initializable {

    private Timeline openingTimeline;
    private ObservableListWrapper<String> list;
    private ListView<String> listView;
    private Popover popover;

    // Data
    private FileEditor editor;
    private Context context;
    private SubContext subContext;
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
                        FileEditor editor = JArmEmuApplication.getEditorController().currentFileEditor();
                        editor.getCodeArea().selectWord();
                        if (editor.getCodeArea().getSelectedText().isEmpty() && !editor.getCurrentWord().isEmpty()) {
                            editor.getCodeArea().moveTo(editor.getCodeArea().getCaretPosition() - 1);
                            editor.getCodeArea().selectWord();
                        }
                        editor.getCodeArea().replaceSelection(selected);
                    }
                }
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

        openingTimeline = new Timeline(new KeyFrame(Duration.millis(250), event -> update()));
    }

    public void update(FileEditor editor, Context context, SubContext subContext, String command, String argType, boolean bracket, boolean brace) {
        this.editor = editor;
        this.context = context;
        this.subContext = subContext;
        this.command = command;
        this.argType = argType;
        this.bracket = bracket;
        this.brace = brace;
        openingTimeline.stop();
        openingTimeline.play();
    }

    private void update() {
        list.clear();

        switch (context) {
            case NONE -> {
                for (Instruction instruction : Instruction.values()) {
                    list.add(instruction.name());
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
                    }

                    case "RotatedOrRegisterArgument", "RotatedImmediateOrRegisterArgument" -> {
                        if (subContext != SubContext.REGISTER && subContext != SubContext.IMMEDIATE) {
                            for (RegisterUtils value : RegisterUtils.values()) {
                                list.add(value.name());
                            }

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

                    case "LabelArgument" -> list.addAll(editor.getRealTimeParser().getAccessibleLabels());

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

        String currentWord = editor.getCurrentWord();
        list.removeIf(s -> !s.toUpperCase().startsWith(currentWord.toUpperCase()));
        list.removeIf(s -> s.equalsIgnoreCase(currentWord));

        System.out.println(context + ":" + subContext + "[" + command + ":" + argType + "]");

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

    public void close() {
        Platform.runLater(() -> {
            if (popover.isShowing()) popover.hide();
        });
    }
}
