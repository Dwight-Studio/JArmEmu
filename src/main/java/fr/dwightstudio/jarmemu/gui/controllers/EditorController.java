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

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import fr.dwightstudio.jarmemu.gui.AbstractJArmEmuModule;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.enums.LineStatus;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.sim.obj.FileLine;
import fr.dwightstudio.jarmemu.util.FileUtils;
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

public class EditorController extends AbstractJArmEmuModule {
    public static final String SAMPLE_CODE = String.join("\n", new String[]{".global _start", ".text", "_start:", "\t@ Beginning of the program"});

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final ArrayList<FileEditor> fileEditors;

    public EditorController(JArmEmuApplication application) {
        super(application);
        fileEditors = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * Ajoute une notification sur l'éditeur (5 maximums).
     *
     * @param titleString le titre (en gras)
     * @param contentString le corps du message
     * @param classString la classe à utiliser (Classes de BootstrapFX)
     */
    public void addNotif(String titleString, String contentString, String classString) {

        if (getController().notifications.getChildren().size() > 5) return;

        Notification notification;

        switch (classString) {
            case Styles.ACCENT -> notification = new Notification(titleString + "\n" + contentString, new FontIcon(Material2OutlinedAL.INFO));
            case Styles.SUCCESS -> notification = new Notification(titleString + "\n" + contentString, new FontIcon(Material2OutlinedAL.CHECK_CIRCLE_OUTLINE));
            case Styles.WARNING -> notification = new Notification(titleString + "\n" + contentString, new FontIcon(Material2OutlinedMZ.OUTLINED_FLAG));
            case Styles.DANGER -> notification = new Notification(titleString + "\n" + contentString, new FontIcon(Material2OutlinedAL.ERROR_OUTLINE));

            default -> notification = new Notification(titleString + ": " + contentString);
        }

        notification.getStyleClass().add(classString);
        notification.setOnClose((event) -> getController().notifications.getChildren().remove(notification));
        notification.setMouseTransparent(false);

        getController().notifications.getChildren().add(notification);
    }

    /**
     * Affiche une notification relative à une AssemblyError.
     *
     * @param exception l'erreur en question
     */
    protected void addError(SyntaxASMException exception) {
        if (exception.getObject() != null) {
            logger.info("Error parsing " + exception.getObject().toString() + " at line " + exception.getLine());
        } else {
            logger.info("Error parsing code at line " + exception.getLine());
        }
        logger.log(Level.INFO, ExceptionUtils.getStackTrace(exception));
        if (exception.isLineSpecified()) {
            addNotif(exception.getTitle(), exception.getMessage() + " at line " + exception.getLine(), Styles.DANGER);
        } else {
            addNotif(exception.getTitle(), exception.getMessage(), Styles.DANGER);
        }
    }

    /**
     * Supprime les notifications.
     */
    protected void clearNotifs() {
        getController().notifications.getChildren().clear();
    }

    /**
     * @return l'éditeur actuellement ouvert.
     */
    public FileEditor currentFileEditor() {
        return fileEditors.get(getController().filesTabPane.getSelectionModel().getSelectedIndex());
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
     * Ouvre un nouvel éditeur vide.
     */
    public void newFile() {
        open("New File", SAMPLE_CODE);
    }

    /**
     * Ouvre un éditeur sans fichier.
     *
     * @param fileName le nom du fichier
     * @param content  le contenu du fichier
     */
    public void open(String fileName, String content) {
        fileEditors.add(new FileEditor(application, fileName, content));
    }

    /**
     * Ouvre un éditeur à partir d'un fichier.
     *
     * @param path le chemin du fichier
     */
    public void open(File path) {
        FileEditor editor = new FileEditor(application, path);
        fileEditors.add(editor);
    }

    /**
     * Nettoie tous les marquages.
     */
    public void clearAllLineMarkings() {
        for (FileEditor editor : fileEditors) {
            editor.clearLineMarking();
        }
    }

    /**
     * Marque une ligne en utilisant le numéro de ligne des fichiers concaténés.
     *
     * @param pos la ligne à marquer
     * @param lineStatus le status de marquage
     */
    public void markLine(FileLine pos, LineStatus lineStatus) {
        fileEditors.get(pos.getFileIndex()).markLine(pos.getPos(), lineStatus);
    }

    /**
     * @param pos la ligne à marquer
     * @return vrai si la ligne contient un breakpoint, faux sinon
     */
    public boolean hasBreakPoint(FileLine pos) {
        return fileEditors.get(pos.getFileIndex()).hasBreakPoint(pos.getPos());
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
    public void onContinue() {
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
}
