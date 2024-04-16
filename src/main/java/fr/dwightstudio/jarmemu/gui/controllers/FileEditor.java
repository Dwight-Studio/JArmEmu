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

package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.gui.AbstractJArmEmuModule;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.editor.ComputeHightlightsTask;
import fr.dwightstudio.jarmemu.gui.editor.EditorContextMenu;
import fr.dwightstudio.jarmemu.gui.factory.JArmEmuLineFactory;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.util.FileUtils;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.controlsfx.dialog.ExceptionDialog;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reactfx.Subscription;

import java.awt.*;
import java.io.File;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileEditor extends AbstractJArmEmuModule {

    private Logger logger = Logger.getLogger(getClass().getSimpleName());

    // GUI
    private final CodeArea codeArea;
    private final VirtualizedScrollPane<CodeArea> editorScroll;
    private final StackPane stackPane;
    private final Tab fileTab;
    private final EditorContextMenu contextMenu;
    private final JArmEmuLineFactory lineFactory;
    private final ExecutorService executor;
    private final Subscription hightlightUpdateSubscription;

    // Propriétés du fichier
    private File path;
    private String lastSaveContent;
    private boolean saved;
    private boolean closed;


    public FileEditor(JArmEmuApplication application, String fileName, String content) {
        super(application);
        this.executor = Executors.newSingleThreadExecutor();
        codeArea = new CodeArea();
        editorScroll = new VirtualizedScrollPane<>(codeArea);
        stackPane = new StackPane(editorScroll);
        fileTab = new Tab(fileName, stackPane);

        Separator separator = new Separator(Orientation.VERTICAL);
        separator.setMouseTransparent(true);
        separator.setPadding(new Insets(0, 0, 0, 80));

        stackPane.setAlignment(Pos.CENTER_LEFT);
        stackPane.getChildren().add(separator);

        hightlightUpdateSubscription = codeArea.plainTextChanges().successionEnds(Duration.ofMillis(50))
                .retainLatestUntilLater(executor)
                .supplyTask(this::autoComputeHighlightingAsync)
                .awaitLatest(codeArea.plainTextChanges())
                .filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        logger.log(Level.WARNING, ExceptionUtils.getStackTrace(t.getFailure()));
                        return Optional.empty();
                    }
                }).subscribe((highlighting) -> {
                    codeArea.setStyleSpans(0, highlighting);
                    // TODO: Ajouter l'autocompletion avec analyse dans les parsers
                    //logger.info(codeArea.getText(codeArea.getCurrentParagraph()));
                });

        fileTab.setOnCloseRequest(event -> {
            getController().filesTabPane.getSelectionModel().select(fileTab);
            if (!getSaveState()) {
                event.consume();
                getDialogs().unsavedAlert().thenAccept(rtn -> {
                    switch (rtn) {
                        case SAVE_AND_CONTINUE -> {
                            getSimulationMenuController().onStop();
                            save();
                            close();
                            getEditorController().cleanClosedEditors();
                        }

                        case DISCARD_AND_CONTINUE -> {
                            getSimulationMenuController().onStop();
                            close();
                            getEditorController().cleanClosedEditors();
                        }

                        default -> {}
                    }
                });
            } else {
                getSimulationMenuController().onStop();
                close();
                getEditorController().cleanClosedEditors();
            }
        });

        codeArea.replaceText(content);
        codeArea.getStylesheets().add(JArmEmuApplication.getResource("editor-style.css").toExternalForm());

        contextMenu = new EditorContextMenu(this);
        codeArea.setContextMenu(contextMenu);

        lineFactory = new JArmEmuLineFactory(getApplication(), this);
        codeArea.setParagraphGraphicFactory(lineFactory);

        getController().filesTabPane.getTabs().add(fileTab);

        // Indentation automatique
        final Pattern whiteSpace = Pattern.compile( "^\\s+" );
        codeArea.addEventHandler( KeyEvent.KEY_PRESSED, KE ->
        {
            if ( KE.getCode() == KeyCode.ENTER ) {
                int caretPosition = codeArea.getCaretPosition();
                int currentParagraph = codeArea.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher( codeArea.getParagraph( currentParagraph-1 ).getSegments().get( 0 ) );
                if ( m0.find() ) Platform.runLater( () -> codeArea.insertText( caretPosition, m0.group() ) );
            }
        });

        // Ajout automatique du caractère fermant
        codeArea.addEventHandler( KeyEvent.KEY_TYPED, KE ->
        {
            int caretPosition = codeArea.getCaretPosition();
            Platform.runLater( () -> {
                switch (KE.getCharacter()) {
                    case "[" -> {
                        codeArea.insertText( caretPosition,"]" );
                        codeArea.moveTo(caretPosition);
                    }

                    case "{" -> {
                        codeArea.insertText( caretPosition,"}" );
                        codeArea.moveTo(caretPosition);
                    }

                    case "\"" -> {
                        codeArea.insertText( caretPosition,"\"" );
                        codeArea.moveTo(caretPosition);
                    }
                }
            } );
        });

        setSaved();
        closed = false;
    }

    public FileEditor(JArmEmuApplication application, @NotNull File path) {
        this(application, path.getName(), "");
        this.path = path;
        reload();
    }

    public CodeArea getCodeArea() {
        return codeArea;
    }

    public VirtualizedScrollPane<CodeArea> getScrollPane() {
        return editorScroll;
    }

    public EditorContextMenu getContextMenu() {
        return contextMenu;
    }

    /**
     * @param line le numéro de la ligne
     * @return vrai si la ligne contient un breakpoint, faux sinon
     */
    public boolean hasBreakPoint(int line) {
        return lineFactory.hasBreakpoint(line);
    }

    public void toggleBreakpoint(int line) {
        lineFactory.onToggleBreakpoint(line);
    }

    /**
     * Nettoie le marquage des lignes.
     */
    public void clearLineMarkings() {
        this.lineFactory.clearMarkings();
    }

    /**
     * Marque comme executé la dernière ligne prévue tout en nettoyant l'ancienne ligne exécutée.
     */
    public void markExecuted() {
        this.lineFactory.markExecuted();
    }

    /**
     * Marque comme prévu une ligne tout en marquant executé l'ancienne ligne prévue.
     *
     * @param line le numéro de la ligne
     */
    public void markForward(int line) {
        if (line >= 0) {
            codeArea.moveTo(line, 0);
            codeArea.requestFollowCaret();

            this.lineFactory.markForward(line);
        }
    }

    /**
     * Déplace le curseur jusqu'à cette ligne.
     *
     * @param line le numéro de la ligne dans le fichier
     */
    public void goTo(int line) {
        codeArea.moveTo(line, 0);
        codeArea.requestFollowCaret();

        lineFactory.goTo(line);
    }

    /**
     * @return le numéro du caractère au-dessus duquel se trouve la souris
     */
    public int getMousePosition() {
        int lineNum = codeArea.getLength();
        for (int i = 0; i < lineNum; i ++) {
            Optional<Bounds> optionalBounds = codeArea.getCharacterBoundsOnScreen(i, i+1);
            if (optionalBounds.isPresent()) {
                Bounds bounds = optionalBounds.get();
                Point mousePos = MouseInfo.getPointerInfo().getLocation();
                if (bounds.contains(new Point2D(mousePos.x, mousePos.y))) return i;
            }
        }

        return -1;
    }

    /**
     * Nettoie la dernière ligne marquée comme exécutée.
     *
     * @apiNote Utile lors du changement d'éditeur
     */
    public void clearLastExecuted() {
        this.lineFactory.clearLastExecuted();
    }

    /**
     * Ferme le fichier
     */
    public void close() {
        logger.info("Closing " + getFileName());
        getController().filesTabPane.getTabs().remove(fileTab);
        hightlightUpdateSubscription.unsubscribe();
        executor.close();
        this.closed = true;
    }

    public boolean isClosed() {
        return closed;
    }

    /**
     * Prépare la simulation (pré-géneration des lignes)
     */
    public void prepareSimulation() {
        int lineNum = codeArea.getParagraphs().size();
        logger.info("Pre-generate " + lineNum + " lines in " + getFileName());
        lineFactory.pregen(codeArea.getParagraphs().size());
        Platform.runLater(this::clearLineMarkings);
    }

    /**
     * Sauvegarde le fichier
     */
    public void save() {
        if (!FileUtils.exists(path)) {
            saveAs();
        } else {
            try {
                logger.info("Saving file...");
                getSourceScanner().exportCodeToFile(path);
                setSaved();
                logger.info("Saved at: " + path.getAbsolutePath());
            } catch (Exception exception) {
                new ExceptionDialog(exception).show();
                logger.severe(ExceptionUtils.getStackTrace(exception));
            }
        }
    }

    /**
     * Sauvegarde le fichier sous un chemin spécifique
     */
    public void saveAs() {
        logger.info("Locating a new file to save...");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(JArmEmuApplication.formatMessage("%menu.file.saveSourceFile"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(JArmEmuApplication.formatMessage("%menu.file.formatName"), "*.s"));
        if (FileUtils.exists(path)) {
            fileChooser.setInitialDirectory(path.isDirectory() ? path : path.getParentFile());
        }
        File file = fileChooser.showSaveDialog(application.stage);
        if (file != null && !file.isDirectory()) {
            try {
                if (!file.getAbsolutePath().endsWith(".s")) file = new File(file.getAbsolutePath() + ".s");
                logger.info("File located: " + file.getAbsolutePath());
                path = file;
                logger.info("Saving file...");
                getSourceScanner().exportCodeToFile(path);
                setSaved();
                logger.info("Saved at: " + path.getAbsolutePath());
            } catch (Exception exception) {
                new ExceptionDialog(exception).show();
                logger.severe(ExceptionUtils.getStackTrace(exception));
            }
        }
    }

    /**
     * Recharge le fichier depuis le disque
     */
    public void reload() {
        logger.info("Reloading file from disk");
        if (FileUtils.isValidFile(path)) {
            try {
                SourceScanner scanner = new SourceScanner(path, getEditorController().getFileIndex(this));
                this.codeArea.replaceText("");
                this.codeArea.replaceText(scanner.exportCode());
                setSaved();
                logger.info("File reloaded: " + path.getAbsolutePath());
            } catch (Exception exception) {
                new ExceptionDialog(exception).show();
                logger.severe(ExceptionUtils.getStackTrace(exception));
            }
        }
    }

    /**
     * @return le nom du fichier ou "New File"
     */
    public String getFileName() {
        return FileUtils.isValidFile(path) ? path.getName() : JArmEmuApplication.formatMessage("%menu.file.newFile");
    }

    /**
     * Défini le contenu de la dernière sauvegarde
     *
     * @apiNote Sert à déterminer l'état actuel de la sauvegarde ('*' dans le titre)
     */
    private void setSaved() {
        saved = true;
        lastSaveContent = String.valueOf(codeArea.getText());
        fileTab.setText(getFileName());
    }

    /**
     * Met à jour l'état de sauvegarde
     */
    public void updateSaveState() {
        saved = codeArea.getText().equals(lastSaveContent);

        if (saved) {
            Platform.runLater(() -> fileTab.setText(getFileName()));
        } else {
            Platform.runLater(() -> fileTab.setText(getFileName() + "*"));
        }

    }

    public boolean getSaveState() {
        updateSaveState();
        return saved;
    }

    /**
     * @return un nouveau SourceScanner du fichier modifié
     */
    public SourceScanner getSourceScanner() {
        return new SourceScanner(codeArea.getText(), path == null ? "New File" : path.getName(), getEditorController().getFileIndex(this));
    }

    /**
     * Méthode utilisée pour automatiquement mettre à jour la colorimétrie
     *
     * @return la tache associée
     */
    private ComputeHightlightsTask autoComputeHighlightingAsync() {
        ComputeHightlightsTask task = new ComputeHightlightsTask(this);

        executor.execute(task);
        return task;
    }

    /**
     * @return le chemin d'accès du fichier
     */
    public @Nullable File getPath() {
        return path;
    }

    /**
     * @return l'indice visuel de l'éditeur
     */
    public int getVisualIndex() {
        return getController().filesTabPane.getTabs().indexOf(fileTab);
    }
}
