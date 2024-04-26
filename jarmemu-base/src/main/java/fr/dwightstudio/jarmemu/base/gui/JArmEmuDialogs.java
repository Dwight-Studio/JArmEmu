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

import atlantafx.base.theme.Styles;
import fr.dwightstudio.jarmemu.base.gui.enums.UnsavedDialogChoice;
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
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class JArmEmuDialogs {

    public Logger logger = Logger.getLogger(getClass().getSimpleName());

    public void warningAlert(String message) {
        FontIcon icon = new FontIcon(Material2OutlinedMZ.WARNING);
        icon.getStyleClass().addAll(Styles.WARNING, "big-icon");
        icon.setIconSize(128);

        Text contentText = new Text(message);
        contentText.setWrappingWidth(500);

        Button confirm = new Button(JArmEmuApplication.formatMessage("%dialog.warning.confirm"));
        confirm.getStyleClass().addAll(Styles.ACCENT, Styles.ROUNDED);
        confirm.setGraphic(new FontIcon(Material2OutlinedAL.CHECK));
        confirm.setContentDisplay(ContentDisplay.RIGHT);
        confirm.setOnAction(event -> JArmEmuApplication.getController().closeDialogFront());

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

        FontIcon icon = new FontIcon(Material2OutlinedMZ.WARNING);
        icon.getStyleClass().addAll(Styles.WARNING, "big-icon");
        icon.setIconSize(128);

        Button save = new Button(JArmEmuApplication.formatMessage("%dialog.unsaved.save"));
        save.getStyleClass().addAll(Styles.SUCCESS, Styles.ROUNDED);
        save.setGraphic(new FontIcon(Material2OutlinedMZ.SAVE));
        save.setContentDisplay(ContentDisplay.RIGHT);
        save.setOnAction(event -> {
            rtn.complete(UnsavedDialogChoice.SAVE_AND_CONTINUE);
            JArmEmuApplication.getController().closeDialogFront();
        });

        Button conti = new Button(JArmEmuApplication.formatMessage("%dialog.unsaved.discard"));
        conti.getStyleClass().addAll(Styles.DANGER, Styles.ROUNDED);
        conti.setGraphic(new FontIcon(Material2OutlinedAL.DELETE));
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

        JArmEmuApplication.getController().openDialogFront(dialog);
        JArmEmuApplication.getInstance().stage.requestFocus();

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
        website.setGraphic(new FontIcon(Material2OutlinedAL.LAUNCH));
        website.setContentDisplay(ContentDisplay.RIGHT);
        website.setAlignment(Pos.CENTER);
        website.setOnAction(event -> {
            JArmEmuApplication.getInstance().openURL("https://dwightstudio.fr/jarmemu");
            JArmEmuApplication.getController().closeDialogBack();
        });

        Button credits = new Button(JArmEmuApplication.formatMessage("%about.credits.title"));
        credits.setPrefWidth(200);
        credits.setGraphic(new FontIcon(Material2OutlinedAL.INFO));
        credits.setContentDisplay(ContentDisplay.RIGHT);
        credits.setAlignment(Pos.CENTER);
        credits.setOnAction(event -> credits());

        Button license = new Button(JArmEmuApplication.formatMessage("%about.licence.title"));
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

        dialog.getModalBox().setOnClose(event -> JArmEmuApplication.getController().closeDialogBack());

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

        dialog.getModalBox().setOnClose(event -> JArmEmuApplication.getController().closeDialogMiddle());

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

        dialog.getModalBox().setOnClose(event -> JArmEmuApplication.getController().closeDialogMiddle());

        JArmEmuApplication.getController().openDialogMiddle(dialog);
    }
}
