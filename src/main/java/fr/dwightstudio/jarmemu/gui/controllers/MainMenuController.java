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

package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.gui.AbstractJArmEmuModule;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.util.FileUtils;
import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class MainMenuController extends AbstractJArmEmuModule {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private File lastFile;

    public MainMenuController(JArmEmuApplication application) {
        super(application);
    }

    /**
     * Initie un nouveau fichier
     */
    public void onNewFile() {
        getSimulationMenuController().onStop();
        logger.info("Opening a new file");
        getSimulationMenuController().onStop();
        getEditorController().newFile();
        getSettingsController().setLastSavePath("");
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onOpen() {
        getSimulationMenuController().onStop();
        logger.info("Locating new file to open...");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Source File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Assembly Source File", "*.s"));
        if (FileUtils.exists(lastFile)) {
            fileChooser.setInitialDirectory(lastFile.isDirectory() ? lastFile : lastFile.getParentFile());
        }
        List<File> files = fileChooser.showOpenMultipleDialog(application.stage);
        if (files != null && !files.isEmpty()){
            for (File file:files) {
                if (FileUtils.isValidFile(file)) {
                    logger.info("File located: " + file.getAbsolutePath());
                    getEditorController().open(file);
                    lastFile = file;
                }
            }
            setLastSave();
        }
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onSaveAll() {
        getEditorController().saveAll();
        setLastSave();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onSave() {
        getEditorController().currentFileEditor().save();
        setLastSave();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onSaveAs() {
        getEditorController().currentFileEditor().saveAs();
        setLastSave();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onReloadAll() {
        getSimulationMenuController().onStop();
        if (getEditorController().getSaveState()) {
            if (getEditorController().getSaveState()) {
                getEditorController().reloadAll();
            } else {
                getDialogs().unsavedAlert().thenAccept(rtn -> {
                    switch (rtn) {
                        case SAVE_AND_CONTINUE -> {
                            onSaveAll();
                            getEditorController().reloadAll();
                        }

                        case DISCARD_AND_CONTINUE -> {
                            getEditorController().reloadAll();
                        }

                        default -> {
                        }
                    }
                });
            }
        }
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onReload() {
        getSimulationMenuController().onStop();
        getEditorController().currentFileEditor().reload();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onCloseAll() {
        getSimulationMenuController().onStop();
        if (getEditorController().getSaveState()) {
            if (getEditorController().getSaveState()) {
                getEditorController().closeAll();
            } else {
                getDialogs().unsavedAlert().thenAccept(rtn -> {
                    switch (rtn) {
                        case SAVE_AND_CONTINUE -> {
                            onSaveAll();
                            getEditorController().closeAll();
                        }

                        case DISCARD_AND_CONTINUE -> {
                            getEditorController().closeAll();
                        }

                        default -> {}
                    }
                });
            }
        }
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onClose() {
        getSimulationMenuController().onStop();
        if (getEditorController().currentFileEditor().getSaveState()) {
            if (getEditorController().getSaveState()) {
                getEditorController().currentFileEditor().close();
            } else {
                getDialogs().unsavedAlert().thenAccept(rtn -> {
                    switch (rtn) {
                        case SAVE_AND_CONTINUE -> {
                            onSaveAll();
                            getEditorController().currentFileEditor().close();
                            getEditorController().cleanClosedEditors();
                        }

                        case DISCARD_AND_CONTINUE -> {
                            getEditorController().currentFileEditor().close();
                            getEditorController().cleanClosedEditors();
                        }

                        default -> {}
                    }
                });
            }
        }
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onExit() {
        if (getEditorController().getSaveState()) {
            exit();
        } else {
            getDialogs().unsavedAlert().thenAccept(rtn -> {
                switch (rtn) {
                    case SAVE_AND_CONTINUE -> {
                        onSaveAll();
                        exit();
                    }

                    case DISCARD_AND_CONTINUE -> {
                        exit();
                    }

                    default -> {}
                }
            });
        }
    }

    /**
     * Ferme correctement l'application
     */
    public void exit() {
        logger.info("Exiting JArmEmu...");

        getExecutionWorker().stop();
        getEditorController().closeAll();

        Platform.exit();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onResetSettings() {
        getSettingsController().setToDefaults();
        getSettingsController().updateGUI();
    }

    /**
     * Ecrit la préférence de la dernière sauvegarde.
     */
    public void setLastSave() {
        getEditorController().cleanClosedEditors();
        List<File> files = getEditorController().getSavePaths();
        String paths = String.join(";", files.stream().map(File::getAbsolutePath).toList());
        getSettingsController().setLastSavePath(paths);
    }

    /**
     * Tente de lire la dernière sauvegarde, ouvre un nouveau fichier sinon.
     */
    public void openLastSave() {
        String[] paths;

        if (application.getArgSave() == null) {
            paths = getSettingsController().getLastSavePath().split(";");
        } else {
            paths = application.getArgSave().split(";");
        }

        logger.info("Trying to open last saves...");
        boolean flag = false;

        for (String path : paths) {
            if (!path.isEmpty()) {
                logger.info("Trying to open " + path + "...");
                try {
                    File file = new File(path);

                    if (file.exists()) {
                        if (file.isFile()) {
                            getEditorController().open(file);
                            flag = true;
                        } else {
                            logger.info("Wrong last-save file (not a file), aborting.");
                        }
                    } else {
                        logger.info("Non existent last-save file, aborting.");
                    }
                } catch (Exception e) {
                    logger.severe(ExceptionUtils.getStackTrace(e));
                }
            } else {
                logger.info("Empty last-save, aborting.");
            }
        }

        if (flag) return;

        onNewFile();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onAbout() {
        getDialogs().about();
    }

    /**
     * Crée les boutons pour les colonnes du tableau de détails de la mémoire.
     */
    public void registerMemoryDetailsColumns() {
        getController().memoryDetailsMenu.getItems().clear();

        getMemoryDetailsController().memoryTable.getColumns().forEach(column -> {
            MenuItem item = new MenuItem(column.getText());

            column.visibleProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    item.setGraphic(new FontIcon(Material2OutlinedAL.CHECK));
                } else {
                    item.setGraphic(null);
                }
            });

            if (column.isVisible()) {
                item.setGraphic(new FontIcon(Material2OutlinedAL.CHECK));
            } else {
                item.setGraphic(null);
            }

            item.setOnAction(event -> {
                column.setVisible(!column.isVisible());
            });
            getController().memoryDetailsMenu.getItems().add(item);
        });
    }

    /**
     * Crée les boutons pour les colonnes du tableau de résumé de la mémoire.
     */
    public void registerMemoryOverviewColumns() {
        getController().memoryOverviewMenu.getItems().clear();

        getMemoryOverviewController().memoryTable.getColumns().forEach(column -> {
            MenuItem item = new MenuItem(column.getText());

            column.visibleProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    item.setGraphic(new FontIcon(Material2OutlinedAL.CHECK));
                } else {
                    item.setGraphic(null);
                }
            });

            if (column.isVisible()) {
                item.setGraphic(new FontIcon(Material2OutlinedAL.CHECK));
            } else {
                item.setGraphic(null);
            }

            item.setOnAction(event -> {
                column.setVisible(!column.isVisible());
            });
            getController().memoryOverviewMenu.getItems().add(item);
        });
    }
}
