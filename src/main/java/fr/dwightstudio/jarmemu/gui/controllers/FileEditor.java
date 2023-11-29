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
import fr.dwightstudio.jarmemu.gui.editor.ComputeHightlightsTask;
import fr.dwightstudio.jarmemu.gui.editor.EditorContextMenu;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.enums.LineStatus;
import fr.dwightstudio.jarmemu.gui.factory.JArmEmuLineFactory;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileEditor extends AbstractJArmEmuModule {

    private Logger logger = Logger.getLogger(getClass().getName());

    private final CodeArea codeArea;
    private final VirtualizedScrollPane<CodeArea> editorScroll;
    private final Tab fileTab;
    private final EditorContextMenu contextMenu;
    private final JArmEmuLineFactory lineFactory;
    private final String fileName;
    private String lastSave;
    private final ExecutorService executor;
    private Subscription hightlightUpdateSubscription;


    public FileEditor(JArmEmuApplication application, String fileName, String content) {
        super(application);
        this.executor = Executors.newSingleThreadExecutor();
        codeArea = new CodeArea();
        editorScroll = new VirtualizedScrollPane<>(codeArea);
        fileTab = new Tab(fileName, editorScroll);
        this.fileName = fileName;

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
                }).subscribe((highlighting) -> codeArea.setStyleSpans(0, highlighting));

        fileTab.setOnCloseRequest(event -> {
            getController().filesTabPane.getSelectionModel().select(fileTab);
            if (!getApplication().getSaveState()) {
                event.consume();
                getDialogs().unsavedAlert().thenAccept(rtn -> {
                    switch (rtn) {
                        case SAVE_AND_CONTINUE -> {
                            getMainMenuController().onSave();
                            close();
                        }

                        case DISCARD_AND_CONTINUE -> close();

                        default -> {}
                    }
                });
            }
        });

        codeArea.replaceText(content);
        codeArea.getStylesheets().add(JArmEmuApplication.getResource("editor-style.css").toExternalForm());

        contextMenu = new EditorContextMenu(codeArea);
        codeArea.setContextMenu(contextMenu);

        lineFactory = new JArmEmuLineFactory();
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
        AtomicBoolean flag = new AtomicBoolean(false);
        lineFactory.breakpoints.forEach(ln -> {
            if (ln == line) flag.set(true);
        });
        return flag.get();
    }

    /**
     * Marque une ligne comme étant éxécuté ou prévue
     *
     * @param line le numéro de la ligne
     * @param status le nouveau statut
     */
    public void markLine(int line, LineStatus status) {
        if (line >= 0) {
            if (status == LineStatus.SCHEDULED) {
                codeArea.moveTo(line, 0);
                codeArea.requestFollowCaret();
            }

            this.lineFactory.markLine(line, status == null ? LineStatus.NONE : status);
        }
    }

    /**
     * Supprime le marquage des lignes
     */
    public void clearLineMarking() {
        for (int i = 0; i < codeArea.getParagraphs().size() ; i++) {
            this.lineFactory.markLine(i, LineStatus.NONE);
        }
    }

    /**
     * Ferme le fichier
     */
    public void close() {
        getController().filesTabPane.getTabs().remove(fileTab);
        hightlightUpdateSubscription.unsubscribe();
        executor.close();
    }

    /**
     * Prépare la simulation (pré-géneration des lignes)
     */
    public void prepareSimulation() {
        int lineNum = codeArea.getParagraphs().size();
        logger.info("Pre-generate " + lineNum + " lines");
        lineFactory.pregenAll(codeArea.getParagraphs().size());
        Platform.runLater(this::clearLineMarking);
    }

    /**
     * Défini le contenu de la dernière sauvegarde
     *
     * @apiNote Sert à déterminer l'état actuel de la sauvegarde ('*' dans le titre)
     */
    public void setSaved() {
        lastSave = String.valueOf(codeArea.getText());
        fileTab.setText(fileName);
    }

    /**
     * Met à jour l'état de sauvegarde
     *
     * @return
     */
    public boolean updateSaveState() {
        boolean saved = codeArea.getText().equals(lastSave);

        if (saved) {
            fileTab.setText(fileName);
        } else {
            fileTab.setText(fileName + "*");
        }

        return saved;
    }

    /**
     * Méthode utilisée pour automatiquement mettre à jour la colorimétrie
     *
     * @return la tache associée
     */
    private ComputeHightlightsTask autoComputeHighlightingAsync() {
        ComputeHightlightsTask task = new ComputeHightlightsTask(getApplication());

        executor.execute(task);
        return task;
    }
}
