package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import org.controlsfx.dialog.ExceptionDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MainMenuController extends AbstractJArmEmuModule {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private File savePath = null;

    public MainMenuController(JArmEmuApplication application) {
        super(application);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onNewFile() {
        if (!application.warnUnsaved()) return;

        getController().onStop();
        getEditorController().newFile();
        getSourceParser().setSourceScanner(new SourceScanner(getController().codeArea.getText()));
        application.setNew();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onOpen() {
        if (!application.warnUnsaved()) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Source File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Assembly Source File", "*.s"));
        if (savePath != null) fileChooser.setInitialDirectory(savePath.isDirectory() ? savePath : savePath.getParentFile());
        File file = fileChooser.showOpenDialog(application.stage);
        if (file != null) {
            savePath = file;
            onReload();
        }
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onSave() {
        if (savePath == null) {
            onSaveAs();
        } else {
            try {
                getSourceParser().setSourceScanner(new SourceScanner(getController().codeArea.getText()));
                getSourceParser().getSourceScanner().exportCodeToFile(savePath);
                application.setSaved();
            } catch (IOException exception) {
                new ExceptionDialog(exception).show();
            }
        }
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onSaveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Source File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Assembly Source File", "*.s"));
        if (savePath != null) fileChooser.setInitialDirectory(savePath.isDirectory() ? savePath : savePath.getParentFile());
        File file = fileChooser.showSaveDialog(application.stage);
        if (file != null) {
            savePath = file;
            onSave();
        }
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onReload() {
        if (!application.warnUnsaved()) return;

        getController().onStop();
        if (savePath != null) {
            try {
                getSourceParser().setSourceScanner(new SourceScanner(savePath));
                getController().codeArea.clear();
                getController().codeArea.insertText(0, application.getSourceParser().getSourceScanner().exportCode());
                application.setSaved();
            } catch (FileNotFoundException exception) {
                new ExceptionDialog(exception).show();
            }
        }
    }

    /**
     * Méthode invoquée par JavaFX
     */
    protected void onExit() {
        if (application.warnUnsaved()) Platform.exit();
    }

    /**
     * @return le fichier représentant le chemin d'accès de la sauvegarde courante
     */
    public File getSavePath() {
        return savePath;
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onResetSettings() {
        getSettingsController().setToDefaults();
    }
}
