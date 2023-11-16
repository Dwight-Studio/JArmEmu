/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2023 Dwight Studio
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

package fr.dwightstudio.jarmemu.gui;

import atlantafx.base.theme.Styles;
import fr.dwightstudio.jarmemu.gui.controllers.AbstractJArmEmuModule;
import fr.dwightstudio.jarmemu.gui.enums.UnsavedDialogChoice;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.jetbrains.annotations.NotNull;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import javax.swing.text.Element;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import static fr.dwightstudio.jarmemu.gui.JArmEmuApplication.getResourceAsStream;

public class JArmEmuDialogs extends AbstractJArmEmuModule {

    public Logger logger = Logger.getLogger(getClass().getName());

    public JArmEmuDialogs(JArmEmuApplication application) {
        super(application);
    }

    public void warningAlert(String message) {
        FontIcon icon = new FontIcon(Material2OutlinedMZ.WARNING);
        icon.getStyleClass().addAll(Styles.WARNING, "big-icon");
        icon.setIconSize(128);

        Text contentText = new Text(message);
        contentText.setWrappingWidth(500);

        Button confirm = new Button("Confirm");
        confirm.getStyleClass().addAll(Styles.ACCENT, Styles.ROUNDED);
        confirm.setGraphic(new FontIcon(Material2OutlinedAL.CHECK));
        confirm.setContentDisplay(ContentDisplay.RIGHT);
        confirm.setOnAction(event -> getController().closeDialogFront());

        getController().openDialogFront(new ModalDialog(
                icon,
                "Warning",
                contentText,
                confirm
        ));
    }

    public CompletableFuture<UnsavedDialogChoice> unsavedAlert() {
        Text contentText = new Text("The open file has unsaved changes. Changes will be permanently lost if you continue without saving.\n\nDo you want to save the file?");
        contentText.setWrappingWidth(500);

        CompletableFuture<UnsavedDialogChoice> rtn = new CompletableFuture<>();

        FontIcon icon = new FontIcon(Material2OutlinedMZ.WARNING);
        icon.getStyleClass().addAll(Styles.WARNING, "big-icon");
        icon.setIconSize(128);

        Button save = new Button("Save and continue");
        save.getStyleClass().addAll(Styles.SUCCESS, Styles.ROUNDED);
        save.setGraphic(new FontIcon(Material2OutlinedMZ.SAVE));
        save.setContentDisplay(ContentDisplay.RIGHT);
        save.setOnAction(event -> {
            rtn.complete(UnsavedDialogChoice.SAVE_AND_CONTINUE);
            getController().closeDialogFront();
        });

        Button conti = new Button("Discard and continue");
        conti.getStyleClass().addAll(Styles.DANGER, Styles.ROUNDED);
        conti.setGraphic(new FontIcon(Material2OutlinedAL.DELETE));
        conti.setContentDisplay(ContentDisplay.RIGHT);
        conti.setOnAction(event -> {
            rtn.complete(UnsavedDialogChoice.DISCARD_AND_CONTINUE);
            getController().closeDialogFront();
        });

        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add(Styles.ROUNDED);
        cancel.setOnAction(event -> {
            rtn.complete(UnsavedDialogChoice.CANCEL);
            getController().closeDialogFront();
        });

        ModalDialog dialog = new ModalDialog(
                icon,
                "Warning",
                contentText,
                save,
                conti,
                cancel
        );

        dialog.getModalBox().setOnClose(event -> {
            rtn.complete(UnsavedDialogChoice.CANCEL);
            getController().closeDialogFront();
        });

        getController().openDialogFront(dialog);

        return rtn;
    }

    public void about() {
        Image image = new Image(getResourceAsStream("medias/favicon@128.png"));
        ImageView picture = new ImageView(image);

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

        Button website = new Button("Website");
        website.setPrefWidth(200);
        website.setGraphic(new FontIcon(Material2OutlinedAL.LAUNCH));
        website.setContentDisplay(ContentDisplay.RIGHT);
        website.setAlignment(Pos.CENTER);
        website.setOnAction(event -> {
            application.openURL("https://github.com/Dwight-Studio/JArmEmu");
            getController().closeDialogBack();
        });

        Button credits = new Button("Credits");
        credits.setPrefWidth(200);
        credits.setGraphic(new FontIcon(Material2OutlinedAL.INFO));
        credits.setContentDisplay(ContentDisplay.RIGHT);
        credits.setAlignment(Pos.CENTER);
        credits.setOnAction(event -> credits());

        Button license = new Button("License");
        license.setPrefWidth(200);
        license.setGraphic(new FontIcon(Material2OutlinedAL.INFO));
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

        dialog.getModalBox().setOnClose(event -> getController().closeDialogBack());

        getController().openDialogBack(dialog);
    }

    private void license() {
        Text title = new Text("License");
        title.setStyle("-fx-font-family: 'Inter Black';");
        title.getStyleClass().addAll(Styles.TITLE_1);

        Text license = new Text("""
                JArmEmu is a project by Dwight Studio.
                Led by Tollemer Kévin and Leconte Alexandre.

                JArmEmu is based on the work of the following projects:
                 - Javafx by OpenJFX
                 - Ikonli by Kordamp
                 - RichTextFX by Tomas Mikula
                 - AtlantaFX by mkpaz""");
        license.setTextAlignment(TextAlignment.JUSTIFY);

        VBox vBox = new VBox(title, license);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));
        vBox.setPrefWidth(200);
        VBox.setMargin(title, new Insets(0, 0, 10, 0));

        ModalDialog dialog = new ModalDialog(vBox, vBox.getPrefWidth(), vBox.getPrefHeight());

        dialog.getModalBox().setOnClose(event -> getController().closeDialogMiddle());

        getController().openDialogMiddle(dialog);
    }

    private void credits() {
        Text title = new Text("Credits");
        title.setStyle("-fx-font-family: 'Inter Black';");
        title.getStyleClass().addAll(Styles.TITLE_1);

        Text credits = new Text("""
                JArmEmu is a project by Dwight Studio.
                Led by Tollemer Kévin and Leconte Alexandre.

                JArmEmu is based on the work of the following projects:
                 - Javafx by OpenJFX
                 - Ikonli by Kordamp
                 - RichTextFX by Tomas Mikula
                 - AtlantaFX by mkpaz""");

        VBox vBox = new VBox(title, credits);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));
        vBox.setPrefWidth(200);
        VBox.setMargin(title, new Insets(0, 0, 10, 0));

        ModalDialog dialog = new ModalDialog(vBox, vBox.getPrefWidth(), vBox.getPrefHeight());

        dialog.getModalBox().setOnClose(event -> getController().closeDialogMiddle());

        getController().openDialogMiddle(dialog);
    }
}
