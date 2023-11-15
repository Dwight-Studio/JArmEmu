package fr.dwightstudio.jarmemu.gui;

import atlantafx.base.theme.Styles;
import fr.dwightstudio.jarmemu.gui.controllers.AbstractJArmEmuModule;
import fr.dwightstudio.jarmemu.gui.enums.UnsavedDialogChoice;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.util.concurrent.CompletableFuture;

public class JArmEmuDialogs extends AbstractJArmEmuModule {
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
        confirm.setOnAction(event -> getController().closeDialog());

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
            getController().closeDialog();
        });

        Button conti = new Button("Discard and continue");
        conti.getStyleClass().addAll(Styles.DANGER, Styles.ROUNDED);
        conti.setGraphic(new FontIcon(Material2OutlinedAL.DELETE));
        conti.setContentDisplay(ContentDisplay.RIGHT);
        conti.setOnAction(event -> {
            rtn.complete(UnsavedDialogChoice.DISCARD_AND_CONTINUE);
            getController().closeDialog();
        });

        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add(Styles.ROUNDED);
        cancel.setOnAction(event -> {
            rtn.complete(UnsavedDialogChoice.CANCEL);
            getController().closeDialog();
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
            getController().closeDialog();
        });

        getController().openDialogFront(dialog);

        return rtn;
    }
}
