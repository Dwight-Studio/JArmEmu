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

package fr.dwightstudio.jarmemu.base.gui.controllers;

import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.util.FileUtils;
import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class MainMenuController {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    private File lastFile;

    /**
     * Initie un nouveau fichier
     */
    public void onNewFile() {
        JArmEmuApplication.getSimulationMenuController().onStop();
        logger.info("Opening a new file");
        JArmEmuApplication.getSimulationMenuController().onStop();
        JArmEmuApplication.getEditorController().newFile();
        JArmEmuApplication.getSettingsController().setLastSavePath("");
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onOpen() {
        JArmEmuApplication.getSimulationMenuController().onStop();
        logger.info("Locating new file to open...");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(JArmEmuApplication.formatMessage("%menu.file.openSourceFile"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(JArmEmuApplication.formatMessage("%menu.file.formatName"), "*.s"));
        if (FileUtils.exists(lastFile)) {
            fileChooser.setInitialDirectory(lastFile.isDirectory() ? lastFile : lastFile.getParentFile());
        }
        List<File> files = fileChooser.showOpenMultipleDialog(JArmEmuApplication.getInstance().stage);
        if (files != null && !files.isEmpty()){
            for (File file:files) {
                if (FileUtils.isValidFile(file)) {
                    logger.info("File located: " + file.getAbsolutePath());
                    JArmEmuApplication.getEditorController().open(file);
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
        JArmEmuApplication.getEditorController().saveAll();
        setLastSave();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onSave() {
        JArmEmuApplication.getEditorController().currentFileEditor().save();
        setLastSave();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onSaveAs() {
        JArmEmuApplication.getEditorController().currentFileEditor().saveAs();
        setLastSave();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onReloadAll() {
        JArmEmuApplication.getSimulationMenuController().onStop();
        if (JArmEmuApplication.getEditorController().getSaveState()) {
            if (JArmEmuApplication.getEditorController().getSaveState()) {
                JArmEmuApplication.getEditorController().reloadAll();
            } else {
                JArmEmuApplication.getDialogs().unsavedAlert().thenAccept(rtn -> {
                    switch (rtn) {
                        case SAVE_AND_CONTINUE -> {
                            onSaveAll();
                            JArmEmuApplication.getEditorController().reloadAll();
                        }

                        case DISCARD_AND_CONTINUE -> {
                            JArmEmuApplication.getEditorController().reloadAll();
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
        JArmEmuApplication.getSimulationMenuController().onStop();
        JArmEmuApplication.getEditorController().currentFileEditor().reload();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onCloseAll() {
        JArmEmuApplication.getSimulationMenuController().onStop();
        if (JArmEmuApplication.getEditorController().getSaveState()) {
            if (JArmEmuApplication.getEditorController().getSaveState()) {
                JArmEmuApplication.getEditorController().closeAll();
            } else {
                JArmEmuApplication.getDialogs().unsavedAlert().thenAccept(rtn -> {
                    switch (rtn) {
                        case SAVE_AND_CONTINUE -> {
                            onSaveAll();
                            JArmEmuApplication.getEditorController().closeAll();
                        }

                        case DISCARD_AND_CONTINUE -> {
                            JArmEmuApplication.getEditorController().closeAll();
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
        JArmEmuApplication.getSimulationMenuController().onStop();
        if (JArmEmuApplication.getEditorController().currentFileEditor().getSaveState()) {
            if (JArmEmuApplication.getEditorController().getSaveState()) {
                JArmEmuApplication.getEditorController().currentFileEditor().close();
            } else {
                JArmEmuApplication.getDialogs().unsavedAlert().thenAccept(rtn -> {
                    switch (rtn) {
                        case SAVE_AND_CONTINUE -> {
                            onSaveAll();
                            JArmEmuApplication.getEditorController().currentFileEditor().close();
                            JArmEmuApplication.getEditorController().cleanClosedEditors();
                        }

                        case DISCARD_AND_CONTINUE -> {
                            JArmEmuApplication.getEditorController().currentFileEditor().close();
                            JArmEmuApplication.getEditorController().cleanClosedEditors();
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
        if (JArmEmuApplication.getEditorController().getSaveState()) {
            exit();
        } else {
            JArmEmuApplication.getDialogs().unsavedAlert().thenAccept(rtn -> {
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

        JArmEmuApplication.getExecutionWorker().stop();
        JArmEmuApplication.getEditorController().closeAll();

        Platform.exit();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onResetSettings() {
        JArmEmuApplication.getSettingsController().setToDefaults();
        JArmEmuApplication.getSettingsController().updateGUI();
    }

    /**
     * Ecrit la préférence de la dernière sauvegarde.
     */
    public void setLastSave() {
        JArmEmuApplication.getEditorController().cleanClosedEditors();
        List<File> files = JArmEmuApplication.getEditorController().getSavePaths();
        String paths = String.join(";", files.stream().map(File::getAbsolutePath).toList());
        JArmEmuApplication.getSettingsController().setLastSavePath(paths);
        logger.info("Saved opened files (" + paths + ")");
    }

    /**
     * Tente de lire la dernière sauvegarde, ouvre un nouveau fichier sinon.
     */
    public void openLastSave() {
        String[] paths;

        if (JArmEmuApplication.getInstance().getArgSave().length == 0) {
            paths = JArmEmuApplication.getSettingsController().getLastSavePath().split(";");
        } else {
            paths = JArmEmuApplication.getInstance().getArgSave();
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
                            JArmEmuApplication.getEditorController().open(file);
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
        JArmEmuApplication.getDialogs().about();
    }

    /**
     * Crée les boutons pour les colonnes du tableau de détails de la mémoire.
     */
    public void registerMemoryDetailsColumns() {
        JArmEmuApplication.getController().memoryDetailsMenu.getItems().clear();

        JArmEmuApplication.getMemoryDetailsController().memoryTable.getColumns().forEach(column -> {
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
            JArmEmuApplication.getController().memoryDetailsMenu.getItems().add(item);
        });
    }

    /**
     * Crée les boutons pour les colonnes du tableau de résumé de la mémoire.
     */
    public void registerMemoryOverviewColumns() {
        JArmEmuApplication.getController().memoryOverviewMenu.getItems().clear();

        JArmEmuApplication.getMemoryOverviewController().memoryTable.getColumns().forEach(column -> {
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
            JArmEmuApplication.getController().memoryOverviewMenu.getItems().add(item);
        });
    }
}
