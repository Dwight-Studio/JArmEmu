package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.enums.UnsavedDialogChoice;
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
        if (getApplication().updateSaveState()) {
            newFile();
        } else {
            getDialogs().unsavedAlert().thenAccept(rtn -> {
                switch (rtn) {
                    case SAVE_AND_CONTINUE -> {
                        onSave();
                        newFile();
                    }

                    case DISCARD_AND_CONTINUE -> newFile();

                    default -> {}
                }
            });
        }
    }

    /**
     * Initie un nouveau fichier
     */
    public void newFile() {
        logger.info("Opening a new file");
        getSimulationMenuController().onStop();
        getEditorController().newFile();
        getSourceParser().setSourceScanner(new SourceScanner(getController().codeArea.getText()));
        getSettingsController().setLastSavePath("");
        application.setNew();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onOpen() {
        logger.info("Locating new file to open...");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Source File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Assembly Source File", "*.s"));
        if (isValid(savePath)) {
            fileChooser.setInitialDirectory(savePath.isDirectory() ? savePath : savePath.getParentFile());
        }
        File file = fileChooser.showOpenDialog(application.stage);
        if (isValidFile(file)) {
            logger.info("File located: " + file.getAbsolutePath());
            savePath = file;
            onReload();
        }
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onSave() {
        if (!isValid(savePath)) {
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
        if (isValid(savePath)) {
            fileChooser.setInitialDirectory(savePath.isDirectory() ? savePath : savePath.getParentFile());
        }
        File file = fileChooser.showSaveDialog(application.stage);
        if (file != null && file.isFile()) {
            if (!file.getAbsolutePath().endsWith(".s")) file = new File(file.getAbsolutePath() + ".s");
            logger.info("File located: " + file.getAbsolutePath());
            savePath = file;
            onSave();
        }
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onReload() {
        if (getApplication().updateSaveState()) {
            reload();
        } else {
            getDialogs().unsavedAlert().thenAccept(rtn -> {
                switch (rtn) {
                    case SAVE_AND_CONTINUE -> {
                        onSave();
                        reload();
                    }

                    case DISCARD_AND_CONTINUE -> reload();

                    default -> {}
                }
            });
        }
    }

    public void reload() {
        logger.info("Reloading file from disk");
        getSimulationMenuController().onStop();
        if (isValidFile(savePath)) {
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
        if (getApplication().updateSaveState()) {
            Platform.exit();
        } else {
            getDialogs().unsavedAlert().thenAccept(rtn -> {
                switch (rtn) {
                    case SAVE_AND_CONTINUE -> {
                        onSave();
                        Platform.exit();
                    }

                    case DISCARD_AND_CONTINUE -> {
                        Platform.exit();
                    }

                    default -> {
                    }
                }
            });
        }
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
        String path;
        application.setNew();

        if (application.getArgSave() == null) {
            path = getSettingsController().getLastSavePath();
        } else {
            path = application.getArgSave();
        }

        logger.info("Trying to open last save...");
        if (!path.isEmpty()) {
            try {
                savePath = new File(path);

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

    private boolean isValid(File file) {
        if (file != null) {
            return file.exists();
        } else {
            return false;
        }
    }

    private boolean isValidFile(File file) {
        if (isValid(file)) {
            return file.isFile();
        } else {
            return false;
        }
    }
}
