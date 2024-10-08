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

package fr.dwightstudio.jarmemu.base.gui;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import com.sun.javafx.collections.ObservableListWrapper;
import fr.dwightstudio.jarmemu.base.asm.Instruction;
import fr.dwightstudio.jarmemu.base.asm.modifier.Condition;
import fr.dwightstudio.jarmemu.base.asm.modifier.ModifierParameter;
import fr.dwightstudio.jarmemu.base.gui.enums.UnsavedDialogChoice;
import fr.dwightstudio.jarmemu.base.gui.factory.InstructionDetailTableCell;
import fr.dwightstudio.jarmemu.base.gui.factory.InstructionUsageTableCell;
import fr.dwightstudio.jarmemu.base.gui.factory.StylizedStringTableCell;
import fr.dwightstudio.jarmemu.base.gui.view.SyntaxView;
import fr.dwightstudio.jarmemu.base.util.InstructionSyntaxUtils;
import fr.dwightstudio.jarmemu.base.util.TableViewUtils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2RoundAL;
import org.kordamp.ikonli.material2.Material2RoundMZ;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class JArmEmuDialogs {

    public Logger logger = Logger.getLogger(getClass().getSimpleName());

    public void warningAlert(String message) {
        FontIcon icon = new FontIcon(Material2RoundMZ.WARNING);
        icon.getStyleClass().addAll(Styles.WARNING, "big-icon");
        icon.setIconSize(128);

        Text contentText = new Text(message);
        contentText.setWrappingWidth(500);

        Button confirm = new Button(JArmEmuApplication.formatMessage("%dialog.warning.confirm"));
        confirm.getStyleClass().addAll(Styles.ACCENT, Styles.ROUNDED);
        confirm.setGraphic(new FontIcon(Material2RoundAL.CHECK));
        confirm.setContentDisplay(ContentDisplay.RIGHT);
        confirm.setOnAction(event -> {
            JArmEmuApplication.getController().closeDialogFront();
        });

        JArmEmuApplication.getController().lockModals();

        JArmEmuApplication.getController().openDialogFront(new ModalDialog(
                icon,
                JArmEmuApplication.formatMessage("%dialog.warning.title"),
                contentText,
                confirm
        ));
    }

    public CompletableFuture<UnsavedDialogChoice> unsavedAlert() {
        Text contentText = new Text(JArmEmuApplication.formatMessage("%dialog.unsaved.message"));
        contentText.setWrappingWidth(500);

        CompletableFuture<UnsavedDialogChoice> rtn = new CompletableFuture<>();

        FontIcon icon = new FontIcon(Material2RoundMZ.WARNING);
        icon.getStyleClass().addAll(Styles.WARNING, "big-icon");
        icon.setIconSize(128);

        Button save = new Button(JArmEmuApplication.formatMessage("%dialog.unsaved.save"));
        save.getStyleClass().addAll(Styles.SUCCESS, Styles.ROUNDED);
        save.setGraphic(new FontIcon(Material2RoundMZ.SAVE));
        save.setContentDisplay(ContentDisplay.RIGHT);
        save.setOnAction(event -> {
            rtn.complete(UnsavedDialogChoice.SAVE_AND_CONTINUE);
            JArmEmuApplication.getController().closeDialogFront();
        });

        Button conti = new Button(JArmEmuApplication.formatMessage("%dialog.unsaved.discard"));
        conti.getStyleClass().addAll(Styles.DANGER, Styles.ROUNDED);
        conti.setGraphic(new FontIcon(Material2RoundAL.DELETE));
        conti.setContentDisplay(ContentDisplay.RIGHT);
        conti.setOnAction(event -> {
            rtn.complete(UnsavedDialogChoice.DISCARD_AND_CONTINUE);
            JArmEmuApplication.getController().closeDialogFront();
        });

        Button cancel = new Button(JArmEmuApplication.formatMessage("%dialog.unsaved.cancel"));
        cancel.getStyleClass().add(Styles.ROUNDED);
        cancel.setOnAction(event -> {
            rtn.complete(UnsavedDialogChoice.CANCEL);
            JArmEmuApplication.getController().closeDialogFront();
        });

        ModalDialog dialog = new ModalDialog(
                icon,
                JArmEmuApplication.formatMessage("%dialog.unsaved.title"),
                contentText,
                save,
                conti,
                cancel
        );

        dialog.getModalBox().setOnClose(event -> {
            rtn.complete(UnsavedDialogChoice.CANCEL);
            JArmEmuApplication.getController().closeDialogFront();
        });

        JArmEmuApplication.getController().lockModals();

        JArmEmuApplication.getController().openDialogFront(dialog);
        JArmEmuApplication.getStage().requestFocus();

        return rtn;
    }

    public void about() {
        Image image = new Image(JArmEmuApplication.getMediaAsStream("images/logo.png"));
        ImageView picture = new ImageView(image);
        picture.setPreserveRatio(true);
        picture.setFitHeight(128);

        Text title = new Text("JArmEmu");
        title.setStyle("-fx-font-family: 'Inter Black';");
        title.getStyleClass().addAll(Styles.TITLE_1);

        Button version = new Button(JArmEmuApplication.VERSION);
        version.getStyleClass().addAll(Styles.ACCENT, Styles.ROUNDED, Styles.SMALL);
        version.setOnAction(event -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(JArmEmuApplication.VERSION);
            Clipboard.getSystemClipboard().setContent(content);
        });

        Button website = new Button(JArmEmuApplication.formatMessage("%about.website.title"));
        website.setPrefWidth(200);
        website.setGraphic(new FontIcon(Material2RoundAL.LAUNCH));
        website.setContentDisplay(ContentDisplay.RIGHT);
        website.setAlignment(Pos.CENTER);
        website.setOnAction(event -> {
            JArmEmuApplication.getInstance().openURL("https://dwightstudio.fr/jarmemu");
            JArmEmuApplication.getController().closeDialogBack();
        });

        Button credits = new Button(JArmEmuApplication.formatMessage("%about.credits.title"));
        credits.setPrefWidth(200);
        credits.setGraphic(new FontIcon(Material2RoundAL.INFO));
        credits.setContentDisplay(ContentDisplay.RIGHT);
        credits.setAlignment(Pos.CENTER);
        credits.setOnAction(event -> credits());

        Button license = new Button(JArmEmuApplication.formatMessage("%about.licence.title"));
        license.setPrefWidth(200);
        license.setGraphic(new FontIcon(Material2RoundAL.INFO));
        license.setContentDisplay(ContentDisplay.RIGHT);
        license.setAlignment(Pos.CENTER);
        license.setOnAction(event -> license());

        VBox vBox = new VBox(picture, title, version, website, credits, license);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));
        vBox.setPrefWidth(200);
        VBox.setMargin(title, new Insets(0, 0, 5, 0));
        VBox.setMargin(version, new Insets(0, 0, 20, 0));
        VBox.setMargin(website, new Insets(0, 0, 10, 0));

        ModalDialog dialog = new ModalDialog(
                vBox,
                vBox.getPrefWidth(),
                vBox.getPrefHeight()
        );

        JArmEmuApplication.getController().openDialogBack(dialog);
    }

    private void license() {
        Text title = new Text(JArmEmuApplication.formatMessage("%about.licence.title"));
        title.setStyle("-fx-font-family: 'Inter Black';");
        title.getStyleClass().addAll(Styles.TITLE_1);

        Text license = new Text(JArmEmuApplication.LICENCE);
        license.setTextAlignment(TextAlignment.CENTER);

        VBox vBox = new VBox(title, license);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));
        vBox.setPrefWidth(200);
        VBox.setMargin(title, new Insets(0, 0, 10, 0));

        ModalDialog dialog = new ModalDialog(vBox, vBox.getPrefWidth(), vBox.getPrefHeight());

        JArmEmuApplication.getController().openDialogMiddle(dialog);
    }

    private void credits() {
        Image image = new Image(JArmEmuApplication.getMediaAsStream("images/dwstd.png"));
        ImageView picture = new ImageView(image);
        picture.setPreserveRatio(true);
        picture.setSmooth(true);
        picture.setFitHeight(128);

        Text title = new Text(JArmEmuApplication.formatMessage("%about.credits.title"));
        title.setStyle("-fx-font-family: 'Inter Black';");
        title.getStyleClass().addAll(Styles.TITLE_1);

        Text credits = new Text(JArmEmuApplication.formatMessage("%about.credits.message"));

        credits.setWrappingWidth(500);
        credits.setTextAlignment(TextAlignment.CENTER);

        VBox vBox = new VBox(title, picture, credits);
        vBox.setSpacing(15);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));
        vBox.setPrefWidth(500);
        VBox.setMargin(title, new Insets(0, 0, 10, 0));

        ModalDialog dialog = new ModalDialog(vBox, vBox.getPrefWidth(), vBox.getPrefHeight());

        JArmEmuApplication.getController().openDialogMiddle(dialog);
    }

    public void instructionList() {
        Text title = new Text(JArmEmuApplication.formatMessage("%instructionList.title"));
        title.setStyle("-fx-font-family: 'Inter Black';");
        title.getStyleClass().addAll(Styles.TITLE_1);

        TableColumn<Instruction, String> col0 = new TableColumn<>(JArmEmuApplication.formatMessage("%instructionList.table.name"));
        TableViewUtils.setupColumn(col0, Material2RoundAL.LABEL, 80, false, false, true);
        col0.setCellValueFactory(i -> new ReadOnlyStringWrapper(i.getValue().toString()));
        col0.setCellFactory(StylizedStringTableCell.factory("text", "usage", "instruction"));

        TableColumn<Instruction, Instruction> col1 = new TableColumn<>(JArmEmuApplication.formatMessage("%instructionList.table.usage"));
        TableViewUtils.setupColumn(col1, Material2RoundAL.DESCRIPTION, 80, false, true, false);
        col1.setMinWidth(Region.USE_PREF_SIZE);
        col1.setCellValueFactory(i -> new ReadOnlyObjectWrapper<>(i.getValue()));
        col1.setCellFactory(InstructionUsageTableCell.factory());

        TableColumn<Instruction, Instruction> col2 = new TableColumn<>();
        TableViewUtils.setupColumn(col2, Material2RoundAL.INFO, 35, false, false, false);
        col2.setCellValueFactory(i -> new ReadOnlyObjectWrapper<>(i.getValue()));
        col2.setCellFactory(InstructionDetailTableCell.factory());

        ObservableList<Instruction> instructions = new ObservableListWrapper<>(new ArrayList<>());
        TableView<Instruction> instructionTable = new TableView<>();

        instructions.setAll(Instruction.values());
        instructions.removeIf(i -> !i.isValid());
        instructionTable.setItems(instructions);

        instructionTable.getColumns().setAll(col0, col1, col2);
        instructionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        instructionTable.getStyleClass().addAll(Styles.STRIPED, Tweaks.ALIGN_CENTER);
        instructionTable.setEditable(false);
        instructionTable.setMaxWidth(Double.POSITIVE_INFINITY);
        instructionTable.setMaxHeight(Double.POSITIVE_INFINITY);
        instructionTable.setMinWidth(650);
        instructionTable.setMinHeight(Region.USE_PREF_SIZE);
        instructionTable.prefHeightProperty().bind(JArmEmuApplication.getStage().heightProperty().map(n -> Math.min(0.6 * n.doubleValue(), 500.0)));

        instructionTable.getStylesheets().add(JArmEmuApplication.getResource("editor-style.css").toExternalForm());

        FontIcon icon = new FontIcon(Material2RoundAL.AUTORENEW);
        HBox placeHolder = new HBox(5, icon);

        icon.getStyleClass().add("medium-icon");
        placeHolder.setAlignment(Pos.CENTER);
        instructionTable.setPlaceholder(placeHolder);

        CustomTextField textField = new CustomTextField();
        textField.setLeft(new FontIcon(Material2RoundMZ.SEARCH));
        textField.setMaxWidth(200);
        textField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                instructionTable.setItems(instructions.filtered(i -> i.isValid() && i.toString().toLowerCase().contains(textField.getText().toLowerCase())));
            }
        });

        VBox vBox = new VBox(title, instructionTable, textField);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));
        vBox.setPrefWidth(VBox.USE_PREF_SIZE);
        vBox.setPrefHeight(VBox.USE_PREF_SIZE);
        vBox.setFillWidth(true);
        VBox.setMargin(instructionTable, new Insets(10));

        ModalDialog dialog = new ModalDialog(vBox, vBox.getPrefWidth(), vBox.getPrefHeight());

        JArmEmuApplication.getController().openDialogBack(dialog);
    }

    public void instructionDetail(Instruction instruction) {
        String instructionString = instruction.toString().toLowerCase();

        Text title = new Text(JArmEmuApplication.formatMessage("%instructionList.detail.title", instruction.toString().toUpperCase()));
        title.setStyle("-fx-font-family: 'Inter Black';");
        title.getStyleClass().addAll(Styles.TITLE_1);

        final String usageString = InstructionSyntaxUtils.getUsage(instruction);

        VBox usageGroup = new VBox();
        usageGroup.setSpacing(20);
        usageGroup.setFillWidth(false);
        usageGroup.setMinWidth(VBox.USE_PREF_SIZE);
        usageGroup.maxWidthProperty().bind(JArmEmuApplication.getStage().widthProperty().multiply(0.55));

        TextFlow usage = new TextFlow();
        usage.getChildren().addAll(InstructionSyntaxUtils.getFormatted(usageString));
        usage.getStyleClass().add("big-usage");
        usage.getStylesheets().add(JArmEmuApplication.getResource("editor-style.css").toExternalForm());
        usage.maxWidthProperty().bind(usageGroup.widthProperty());

        TextFlow description = new TextFlow();
        description.getStylesheets().add(JArmEmuApplication.getResource("editor-style.css").toExternalForm());
        description.getChildren().add(InstructionSyntaxUtils.getText(JArmEmuApplication.formatMessage("%instructionList.description.all") + "\n\n", "notice"));
        description.getChildren().addAll(InstructionSyntaxUtils.getFormatted(JArmEmuApplication.formatMessage("%instructionList.description." + instructionString)));
        description.getStyleClass().add("instruction-description");
        description.maxWidthProperty().bind(usageGroup.widthProperty());;

        usageGroup.getChildren().addAll(usage, description);

        Button exampleButton = new Button(JArmEmuApplication.formatMessage("%instructionList.detail.example"));

        exampleButton.getStyleClass().add(Styles.ACCENT);
        exampleButton.setOnAction(event -> {
            String rawExample = "\t" + JArmEmuApplication.formatMessage("%instructionList.example." + instructionString).replaceAll("\n","\n\t");

            String formatedExample = JArmEmuApplication.formatMessage("%instructionList.detail.exampleContent", instructionString.toUpperCase(), rawExample);

            String exampleContent = """
                    .global _start
                    .data
                    DATA:
                    \t.word 4
                    
                    .text
                    _start:
                    """ + formatedExample + """
                    
                    LOOP:
                    \tNOP
                    \tB LOOP""";

            JArmEmuApplication.getController().closeDialogFront();
            JArmEmuApplication.getController().closeDialogMiddle();
            JArmEmuApplication.getController().closeDialogBack();

            JArmEmuApplication.getEditorController().open(instruction + ".s", exampleContent);
        });

        FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL, 30, 30);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setMinWidth(Region.USE_PREF_SIZE);
        flowPane.setMinHeight(Region.USE_PREF_SIZE);
        flowPane.prefWrapLengthProperty().bind(JArmEmuApplication.getStage().widthProperty().multiply(0.6));

        VBox vBox = new VBox(title, usageGroup, exampleButton, flowPane);
        vBox.setSpacing(20);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(0, 0, 30, 0));

        VBox.setMargin(exampleButton, new Insets(0, 0, 30, 0));

        List<Class<? extends Enum<? extends ModifierParameter>>> modifiers = instruction.getModifierParameterClasses();

        TableView<SyntaxView> valueTable = InstructionSyntaxUtils.getValueTable(usageString);
        if (valueTable != null) flowPane.getChildren().add(valueTable);

        if (modifiers.contains(Condition.class)) flowPane.getChildren().add(InstructionSyntaxUtils.getConditionTable());
        if (usageString.contains("sht") || usageString.contains("[adr]")) flowPane.getChildren().add(InstructionSyntaxUtils.getShiftTable());
        if (usageString.contains("[adr]")) flowPane.getChildren().add(InstructionSyntaxUtils.getAddressTable(instruction));

        ModalDialog dialog = new ModalDialog(vBox);

        JArmEmuApplication.getController().openDialogMiddle(dialog);
    }

}
