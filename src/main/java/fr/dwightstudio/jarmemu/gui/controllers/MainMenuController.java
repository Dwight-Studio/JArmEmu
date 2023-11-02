package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.controlsfx.dialog.ExceptionDialog;

import java.io.File;
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

        logger.info("Opening a new file");
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

        logger.info("Locating new file to open...");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Source File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Assembly Source File", "*.s"));
        if (savePath != null) fileChooser.setInitialDirectory(savePath.isDirectory() ? savePath : savePath.getParentFile());
        File file = fileChooser.showOpenDialog(application.stage);
        if (file != null && file.exists() && file.isFile()) {
            logger.info("File located: " + file.getAbsolutePath());
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
                logger.info("Saving file...");
                getSourceParser().setSourceScanner(new SourceScanner(getController().codeArea.getText()));
                getSourceParser().getSourceScanner().exportCodeToFile(savePath);
                application.setSaved();
                getSettingsController().setLastSavePath(savePath.getAbsolutePath());
                logger.info("Saved at: " + savePath.getAbsolutePath());
            } catch (Exception exception) {
                new ExceptionDialog(exception).show();
                logger.severe(ExceptionUtils.getStackTrace(exception));
            }
        }
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onSaveAs() {
        logger.info("Locating a new file to save...");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Source File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Assembly Source File", "*.s"));
        if (savePath != null) fileChooser.setInitialDirectory(savePath.isDirectory() ? savePath : savePath.getParentFile());
        File file = fileChooser.showSaveDialog(application.stage);
        if (file != null) {
            logger.info("File located: " + file.getAbsolutePath());
            savePath = file;
            onSave();
        }
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onReload() {
        if (!application.warnUnsaved()) return;

        logger.info("Reloading file from disk");
        getController().onStop();
        if (savePath != null) {
            try {
                getSourceParser().setSourceScanner(new SourceScanner(savePath));
                getController().codeArea.clear();
                getController().codeArea.insertText(0, application.getSourceParser().getSourceScanner().exportCode());
                application.setSaved();
                getSettingsController().setLastSavePath(savePath.getAbsolutePath());
                logger.info("File reloaded: " + savePath.getAbsolutePath());
            } catch (Exception exception) {
                new ExceptionDialog(exception).show();
                logger.severe(ExceptionUtils.getStackTrace(exception));
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

    /**
     * Tente de lire la dernière sauvegarde, ouvre un nouveau fichier sinon.
     */
    public void openLastSave() {
        logger.info("Trying to open last save...");
        if (!getSettingsController().getLastSavePath().isEmpty()) {
            try {
                savePath = new File(getSettingsController().getLastSavePath());

                if (savePath.exists()) {
                    if (savePath.isFile()) {
                        logger.info("Last-save file located: " + savePath.getAbsolutePath());
                        onReload();
                        return;
                    } else {
                        logger.info("Wrong last-save file (not a file), aborting.");
                    }
                } else  {
                    logger.info("Non existent last-save file, aborting.");
                }
            } catch (Exception e) {
                logger.severe(ExceptionUtils.getStackTrace(e));
            }
        } else {
            logger.info("Empty last-save, aborting.");
        }

        onNewFile();
    }
}
