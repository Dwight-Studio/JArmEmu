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

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import fr.dwightstudio.jarmemu.base.asm.exception.ASMException;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.sim.SourceScanner;
import fr.dwightstudio.jarmemu.base.sim.entity.FilePos;
import fr.dwightstudio.jarmemu.base.util.FileUtils;
import javafx.animation.Timeline;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.util.Duration;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditorController implements Initializable {
    public static final String SAMPLE_CODE = String.join("\n", new String[]{".global _start", ".text", "_start:", "\t@ Beginning of the program"});

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    private final ArrayList<FileEditor> fileEditors;
    private FileEditor lastScheduledEditor;
    private FileEditor lastExecutedEditor;

    public EditorController() {
        fileEditors = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * Create and show a notification on the editor.
     *
     * @param titleString the title
     * @param contentString the notification body
     * @param classString the style class of the message
     */
    public void addNotification(String titleString, String contentString, String classString, Button... buttons) {

        if (JArmEmuApplication.getController().notifications.getChildren().size() >= JArmEmuApplication.getSettingsController().getMaxNotification()) return;

        Notification notification;

        switch (classString) {
            case Styles.ACCENT -> notification = new Notification(titleString + "\n\n" + contentString, new FontIcon(Material2OutlinedAL.INFO));
            case Styles.SUCCESS -> notification = new Notification(titleString + "\n\n" + contentString, new FontIcon(Material2OutlinedAL.CHECK_CIRCLE_OUTLINE));
            case Styles.WARNING -> notification = new Notification(titleString + "\n\n" + contentString, new FontIcon(Material2OutlinedMZ.OUTLINED_FLAG));
            case Styles.DANGER -> notification = new Notification(titleString + "\n\n" + contentString, new FontIcon(Material2OutlinedAL.ERROR_OUTLINE));

            default -> notification = new Notification(titleString + ": " + contentString);
        }

        notification.setOnClose(event -> {
            Timeline closingTimeline = Animations.slideOutDown(notification, Duration.millis(250));
            closingTimeline.setOnFinished(event2 -> JArmEmuApplication.getController().notifications.getChildren().remove(notification));
            closingTimeline.playFromStart();
        });

        notification.getStyleClass().add(classString);
        notification.setMouseTransparent(false);
        notification.setMaxWidth(Double.MAX_VALUE);
        notification.setMinWidth(400);

        notification.setPrimaryActions(buttons);

        JArmEmuApplication.getController().notifications.getChildren().add(notification);
    }

    /**
     * Create and show a notification explaining an exception.
     *
     * @param exception the thrown exception
     */
    protected void addError(ASMException exception) {
        if (exception.getObject() != null) {
            logger.info("Error parsing " + exception.getObject().toString() + " at line " + exception.getLine() + 1);
        } else {
            logger.info("Error parsing code at line " + exception.getLine() + 1);
        }
        logger.log(Level.INFO, ExceptionUtils.getStackTrace(exception));

        if (exception.isLineSpecified()) {
            if (exception.isFileSpecified()) {
                addNotification(
                        exception.getTitle(),
                        JArmEmuApplication.formatMessage("%notification.exception.fileAndLine", exception.getMessage(), exception.getLine() + 1, exception.getFile().getName()),
                        Styles.DANGER
                );
            } else {
                addNotification(
                        exception.getTitle(),
                        JArmEmuApplication.formatMessage("%notification.exception.fileAndLine", exception.getMessage(), exception.getLine() + 1),
                        Styles.DANGER
                );
            }
        } else {
            addNotification(
                    exception.getTitle(),
                    JArmEmuApplication.formatMessage("%notification.exception.nothing", exception.getMessage()),
                    Styles.DANGER
            );
        }
    }

    /**
     * Empty the notification tray.
     */
    public void clearNotifications() {
        JArmEmuApplication.getController().notifications.getChildren().clear();
    }

    /**
     * @return the currently opened editor (on which the user is working)
     */
    public FileEditor currentFileEditor() {
        return fileEditors.get(JArmEmuApplication.getController().filesTabPane.getSelectionModel().getSelectedIndex());
    }

    public List<SourceScanner> getSources() {
        ArrayList<SourceScanner> rtn = new ArrayList<>();

        for (FileEditor editor : fileEditors) {
            rtn.add(editor.getSourceScanner());
        }

        return rtn;
    }

    public List<FileEditor> getFileEditors() {
        return fileEditors;
    }

    /**
     * Open a new editor containing the default code.
     */
    public void newFile() {
        open(JArmEmuApplication.formatMessage("%menu.file.newFile"), SAMPLE_CODE);
    }

    /**
     * Open a new editor which is not tied to a file.
     *
     * @param fileName the editor title (file name)
     * @param content the content of the editor
     */
    public void open(String fileName, String content) {
        fileEditors.add(new FileEditor(fileName, content));
        JArmEmuApplication.getEditorController().updateSimulationButtons();
        JArmEmuApplication.getController().filesTabPane.getSelectionModel().selectLast();
    }

    /**
     * Open an editor with a file.
     *
     * @param path the path to the file
     */
    public void open(File path) {
        FileEditor editor = new FileEditor(path);
        fileEditors.add(editor);
        JArmEmuApplication.getEditorController().updateSimulationButtons();
        JArmEmuApplication.getController().filesTabPane.getSelectionModel().selectLast();
    }

    public void clearAllLineMarkings() {
        for (FileEditor editor : fileEditors) {
            editor.clearLineMarkings();
        }
    }

    /**
     * Mark the line as scheduled (next to be executed) and the scheduled line as executed.
     *
     * @param pos the line number to mark as scheduled
     */
    public void markForward(FilePos pos) {
        try {
            FileEditor fileEditor = fileEditors.get(pos.getFileIndex());
            fileEditor.markForward(pos.getPos());

            if (lastExecutedEditor != null && !lastExecutedEditor.isClosed() && lastExecutedEditor != fileEditor)
                lastExecutedEditor.clearLastExecuted();

            if (lastScheduledEditor != null && !lastScheduledEditor.isClosed() && lastScheduledEditor != fileEditor) {
                lastScheduledEditor.markExecuted();

                lastExecutedEditor = lastScheduledEditor;
            }
            lastScheduledEditor = fileEditor;

            JArmEmuApplication.getController().filesTabPane.getSelectionModel().select(fileEditor.getVisualIndex());
        } catch (NullPointerException e) {
            logger.warning("Trying to mark non-existent line " + pos);
        }
    }

    /**
     * Move cursor to the line (selecting the corresponding editor).
     *
     * @param pos the file coordinates in editors
     */
    public void goTo(FilePos pos) {
        try {
            FileEditor fileEditor = fileEditors.get(pos.getFileIndex());
            fileEditor.goTo(pos.getPos());

            JArmEmuApplication.getController().filesTabPane.getSelectionModel().select(fileEditor.getVisualIndex());
        } catch (NullPointerException e) {
            logger.warning("Trying to go to non-existent line " + pos);
        }
    }

    /**
     * @param pos the number of the line to test
     * @return true if the line contains a breakpoint, false otherwise
     */
    public boolean hasBreakPoint(FilePos pos) {
        if (pos == null) return false;
        return fileEditors.get(pos.getFileIndex()).hasBreakPoint(pos.getPos());
    }

    /**
     * Prepare each file editor to the simulation (pre-generating lines...).
     */
    public void prepareSimulation() {
        fileEditors.forEach(FileEditor::prepareSimulation);
    }

    /**
     * Sauvegarde tous les fichiers.
     */
    public void saveAll() {
        for (FileEditor editor : fileEditors) {
            editor.save();
        }
    }

    /**
     * Recharge tous les fichiers.
     */
    public void reloadAll() {
        for (FileEditor editor : fileEditors) {
            editor.reload();
        }
    }

    /**
     * Ferme tous les fichiers.
     */
    public void closeAll() {
        for (FileEditor editor : fileEditors) {
            editor.close();
        }
        cleanClosedEditors();
    }

    /**
     * Enlève les fichiers fermés de la liste des fichiers
     */
    public void cleanClosedEditors() {
        fileEditors.removeIf(FileEditor::isClosed);
        JArmEmuApplication.getEditorController().updateSimulationButtons();
    }

    /**
     * @return l'état de sauvegarde globale (tous les fichiers ouverts).
     */
    public boolean getSaveState() {
        for (FileEditor editor : fileEditors) {
            if (!editor.getSaveState()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Calcule tous les chemins d'accès et actualise les préférences.
     *
     * @return les chemin d'accès de la sauvegarde courante
     */
    public ArrayList<File> getSavePaths() {
        ArrayList<File> rtn = new ArrayList<>();

        for (FileEditor editor : fileEditors) {
            if (FileUtils.isValidFile(editor.getPath())) rtn.add(editor.getPath());
        }

        return rtn;
    }

    /**
     * Méthode appelée lors de la reprise de l'exécution.
     */
    public void onLaunch() {
        if (!fileEditors.isEmpty()) {
            for (FileEditor editor : fileEditors) {
                editor.getCodeArea().setEditable(false);
            }
        }
    }

    /**
     * Méthode appelée lors de la reprise de l'exécution.
     */
    public void onContinueOrStepOver() {
        if (!fileEditors.isEmpty()) {
            for (FileEditor editor : fileEditors) {
                editor.getCodeArea().setDisable(true);
                editor.getScrollPane().setDisable(true);
            }
        }
    }

    /**
     * Méthode appelée lors de la pause de l'exécution.
     */
    public void onPause() {
        if (!fileEditors.isEmpty()) {
            for (FileEditor editor : fileEditors) {
                editor.getCodeArea().setDisable(false);
                editor.getScrollPane().setDisable(false);
            }
        }
    }

    /**
     * Méthode appelée lors de l'arrêt de la simulation.
     */
    public void onStop() {
        if (!fileEditors.isEmpty()) {
            for (FileEditor editor : fileEditors) {
                editor.getCodeArea().setDisable(false);
                editor.getCodeArea().setEditable(true);
                editor.getScrollPane().setDisable(false);
            }
        }
    }

    /**
     * @param fileEditor l'éditeur de fichier
     * @return l'indice de l'éditeur
     */
    public int getFileIndex(FileEditor fileEditor) {
        return fileEditors.indexOf(fileEditor);
    }

    /**
     * Checks if there is still opened files to allow simulation
     */
    public void updateSimulationButtons() {
        for (FileEditor fileEditor : fileEditors) {
            if (!fileEditor.isClosed()) {
                JArmEmuApplication.getController().menuSimulate.setDisable(false);
                JArmEmuApplication.getController().toolSimulate.setDisable(false);
                return;
            }
        }

        JArmEmuApplication.getController().menuSimulate.setDisable(true);
        JArmEmuApplication.getController().toolSimulate.setDisable(true);
    }

    /**
     * Reinitialize all real time parsers
     */
    public void reinitializeRealTimeParsers() {
        getFileEditors().forEach(FileEditor::initializeRealTimeParser);
        getFileEditors().forEach(FileEditor::forceRefreshRealTimeParser);
    }
}
