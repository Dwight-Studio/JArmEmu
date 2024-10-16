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

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import fr.dwightstudio.jarmemu.base.Status;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.editor.*;
import fr.dwightstudio.jarmemu.base.gui.factory.JArmEmuLineFactory;
import fr.dwightstudio.jarmemu.base.sim.SourceScanner;
import fr.dwightstudio.jarmemu.base.util.FileUtils;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.*;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.controlsfx.dialog.ExceptionDialog;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.TwoDimensional;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2RoundAL;
import org.kordamp.ikonli.material2.Material2RoundMZ;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FileEditor {

    private Logger logger = Logger.getLogger(getClass().getSimpleName());

    // Key pressed
    private final static Pattern whiteSpace = Pattern.compile("^\\s+");

    // GUI
    private final CodeArea codeArea;
    private final VirtualizedScrollPane<CodeArea> editorScroll;
    private final StackPane stackPane;
    private final Tab fileTab;
    private final AnchorPane findPane;
    private final CustomTextField findTextField;
    private final CustomTextField replaceTextField;
    private final Button previousButton;
    private final Button nextButton;
    private final Button replace;
    private final Button replaceAll;
    private final ToggleButton caseSensitivity;
    private final ToggleButton word;
    private final ToggleButton regex;
    private final Button closeFind;

    private RealTimeParser realTimeParser;
    private final EditorContextMenu contextMenu;
    private final JArmEmuLineFactory lineFactory;

    // Propriétés du fichier
    private File path;
    private String lastSaveContent;
    private boolean saved;
    private boolean closed;
    private List<Find> previousFind;
    private int selectedFind;

    public FileEditor(String fileName, String content) {
        codeArea = new CodeArea();
        editorScroll = new VirtualizedScrollPane<>(codeArea);
        stackPane = new StackPane(editorScroll);
        fileTab = new Tab(fileName, stackPane);

        initializeRealTimeParser();

        Separator separator = new Separator(Orientation.VERTICAL);
        separator.setMouseTransparent(true);
        separator.setPadding(new Insets(0, 0, 0, 80));

        stackPane.setAlignment(Pos.CENTER_LEFT);
        stackPane.getChildren().add(separator);

        fileTab.setOnCloseRequest(event -> {
            JArmEmuApplication.getController().filesTabPane.getSelectionModel().select(fileTab);
            if (!getSaveState()) {
                event.consume();
                JArmEmuApplication.getDialogs().unsavedAlert().thenAccept(rtn -> {
                    switch (rtn) {
                        case SAVE_AND_CONTINUE -> {
                            JArmEmuApplication.getSimulationMenuController().onStop();
                            save();
                            close();
                            JArmEmuApplication.getEditorController().cleanClosedEditors();
                            JArmEmuApplication.getMainMenuController().setLastSave();
                        }

                        case DISCARD_AND_CONTINUE -> {
                            JArmEmuApplication.getSimulationMenuController().onStop();
                            close();
                            JArmEmuApplication.getEditorController().cleanClosedEditors();
                            JArmEmuApplication.getMainMenuController().setLastSave();
                        }

                        default -> {
                        }
                    }
                });
            } else {
                JArmEmuApplication.getSimulationMenuController().onStop();
                close();
                JArmEmuApplication.getEditorController().cleanClosedEditors();
                JArmEmuApplication.getMainMenuController().setLastSave();
            }
        });

        setSaved(content);
        codeArea.replaceText(content);
        codeArea.getStylesheets().add(JArmEmuApplication.getResource("editor-style.css").toExternalForm());

        contextMenu = new EditorContextMenu(this);
        codeArea.setContextMenu(contextMenu);

        lineFactory = new JArmEmuLineFactory(this);
        codeArea.setParagraphGraphicFactory(lineFactory);

        JArmEmuApplication.getController().filesTabPane.getTabs().add(fileTab);

        // Indentation automatique
        final Pattern whiteSpace = Pattern.compile("^\\s+");

        Nodes.addInputMap(codeArea, InputMap.consume(EventPattern.anyOf(EventPattern.keyPressed(KeyCode.TAB))));

        codeArea.setOnKeyPressed(this::handleKeyPressed);

        // Ajout automatique du caractère fermant
        codeArea.setOnKeyTyped(keyEvent -> {
            JArmEmuApplication.getAutocompletionController().onKeyTyped(keyEvent.getCharacter(), codeArea.getCaretPosition());
        });

        // Find & Replace
        findPane = new AnchorPane();
        findPane.setVisible(false);
        GridPane findContainer = new GridPane();
        findContainer.setHgap(10);
        findContainer.setVgap(10);
        findContainer.setPadding(new Insets(10, 10, 10, 10));
        findContainer.prefWidth(Double.MAX_VALUE);
        findContainer.getStyleClass().add("findAndReplace");

        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setHalignment(HPos.CENTER);
        constraints.setFillWidth(true);
        constraints.setHgrow(Priority.ALWAYS);
        findContainer.getColumnConstraints().add(constraints);

        constraints = new ColumnConstraints();
        constraints.setHalignment(HPos.CENTER);
        constraints.setFillWidth(true);
        findContainer.getColumnConstraints().add(constraints);

        constraints = new ColumnConstraints();
        constraints.setHalignment(HPos.CENTER);
        constraints.setFillWidth(true);
        findContainer.getColumnConstraints().add(constraints);

        findTextField = new CustomTextField();
        findTextField.setPromptText(JArmEmuApplication.formatMessage("%pop.find.find"));
        findTextField.setLeft(new FontIcon(Material2RoundMZ.SEARCH));
        findContainer.add(findTextField, 0, 0);

        previousButton = new Button(null, new FontIcon(Material2RoundAL.KEYBOARD_ARROW_UP));
        HBox.setHgrow(previousButton, Priority.ALWAYS);
        previousButton.setMaxWidth(Double.MAX_VALUE);
        previousButton.getStyleClass().add(Styles.LEFT_PILL);

        nextButton = new Button(null, new FontIcon(Material2RoundAL.KEYBOARD_ARROW_DOWN));
        HBox.setHgrow(nextButton, Priority.ALWAYS);
        nextButton.setMaxWidth(Double.MAX_VALUE);
        nextButton.getStyleClass().add(Styles.RIGHT_PILL);

        HBox navBox = new HBox(previousButton, nextButton);
        navBox.setAlignment(Pos.CENTER);
        findContainer.add(navBox, 1, 0);

        replaceTextField = new CustomTextField();
        replaceTextField.setPromptText(JArmEmuApplication.formatMessage("%pop.find.replace"));
        replaceTextField.setLeft(new FontIcon(Material2RoundAL.FIND_REPLACE));
        findContainer.add(replaceTextField, 0, 1);

        replace = new Button(JArmEmuApplication.formatMessage("%pop.find.replace"));
        replace.setMaxWidth(Double.MAX_VALUE);
        replace.getStyleClass().addAll(Styles.ACCENT, Styles.BUTTON_OUTLINED);

        findContainer.add(replace, 1, 1);

        replaceAll = new Button(JArmEmuApplication.formatMessage("%pop.find.replaceAll"));
        replaceAll.getStyleClass().addAll(Styles.ACCENT, Styles.BUTTON_OUTLINED);

        findContainer.add(replaceAll, 2, 1);

        caseSensitivity = new ToggleButton("Cc");
        HBox.setHgrow(caseSensitivity, Priority.ALWAYS);
        caseSensitivity.setMaxWidth(Double.MAX_VALUE);
        caseSensitivity.getStyleClass().addAll(Styles.LEFT_PILL);
        caseSensitivity.setTooltip(new Tooltip(JArmEmuApplication.formatMessage("%pop.find.caseSensitivity")));

        word = new ToggleButton("W");
        HBox.setHgrow(word, Priority.ALWAYS);
        word.setMaxWidth(Double.MAX_VALUE);
        word.getStyleClass().addAll(Styles.CENTER_PILL);
        word.setTooltip(new Tooltip(JArmEmuApplication.formatMessage("%pop.find.word")));

        regex = new ToggleButton(".*");
        HBox.setHgrow(regex, Priority.ALWAYS);
        regex.setMaxWidth(Double.MAX_VALUE);
        regex.getStyleClass().addAll(Styles.RIGHT_PILL);
        regex.setTooltip(new Tooltip(JArmEmuApplication.formatMessage("%pop.find.regex")));

        HBox optionBox = new HBox(caseSensitivity, word, regex);
        optionBox.setAlignment(Pos.CENTER);
        findContainer.add(optionBox, 2, 0);

        closeFind = new Button(null, new FontIcon(Material2RoundAL.CLEAR));
        closeFind.getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_ICON, Styles.ROUNDED);
        closeFind.setOnAction(actionEvent -> findPane.setVisible(false));
        findContainer.add(closeFind, 3, 0);

        HBox back = new HBox(findContainer);
        HBox.setHgrow(findContainer, Priority.ALWAYS);
        back.setMaxWidth(Double.MAX_VALUE);
        back.getStyleClass().addAll("popup");

        AnchorPane.setBottomAnchor(back, 20d);
        AnchorPane.setLeftAnchor(back, 20d);
        AnchorPane.setRightAnchor(back, 20d);
        findPane.getChildren().add(back);
        findPane.setPickOnBounds(false);
        stackPane.getChildren().add(findPane);

        // Mark search results' lines dirty
        findTextField.textProperty().addListener((obs, oldValue, newValue) -> {
            List<Find> newFinds = getSearch(codeArea.getText());
            Set<Find> finds = new HashSet<>(newFinds);
            if (previousFind != null) finds.addAll(previousFind);

            for (Find find : finds) {
                this.realTimeParser.markDirty(
                        getLineFromPos(find.start()),
                        getLineFromPos(find.end())
                );
            }

            previousFind = newFinds;
        });

        findPane.visibleProperty().addListener(obs -> updateAllSearches());

        previousButton.setOnAction(actionEvent -> {
            if (previousFind == null || previousFind.isEmpty()) return;

            if (previousFind.size() - 1 < selectedFind - 1) {
                selectedFind = previousFind.size() - 1;
            } else if (selectedFind - 1 > 0) {
                selectedFind--;
            } else {
                selectedFind = 0;
            }

            Find f = previousFind.get(selectedFind);
            codeArea.moveTo(f.start());
            codeArea.requestFollowCaret();
            codeArea.selectRange(f.start(), f.end());
        });

        nextButton.setOnAction(actionEvent -> {
            if (previousFind == null || previousFind.isEmpty()) return;

            if (previousFind.size() - 1 < selectedFind + 1) {
                selectedFind = previousFind.size() - 1;
            } else if (selectedFind + 1 < previousFind.size() - 1) {
                selectedFind++;
            } else {
                selectedFind = previousFind.size() - 1;
            }

            Find f = previousFind.get(selectedFind);
            codeArea.moveTo(f.start());
            codeArea.requestFollowCaret();
            codeArea.selectRange(f.start(), f.end());
        });

        replace.setOnAction(actionEvent -> {
            if (previousFind == null || previousFind.isEmpty()) return;
            if (replaceTextField.getText() == null) return;

            if (previousFind.size() - 1 < selectedFind) {
                selectedFind = previousFind.size() - 1;
            } else if (selectedFind >= previousFind.size() - 1) {
                selectedFind = previousFind.size() - 1;
            }

            Find f = previousFind.get(selectedFind);
            codeArea.moveTo(f.start());
            codeArea.requestFollowCaret();
            codeArea.selectRange(f.start(), f.end());
            codeArea.replaceSelection(replaceTextField.getText());

            updateAllSearches();
        });

        replaceAll.setOnAction(actionEvent -> {
            if (previousFind == null || previousFind.isEmpty()) return;
            if (replaceTextField.getText() == null) return;

            int offset = 0;
            for (Find f : previousFind) {
                codeArea.moveTo(offset + f.start());
                codeArea.requestFollowCaret();
                codeArea.selectRange(offset + f.start(), offset + f.end());
                offset += replaceTextField.getText().length() - codeArea.getSelection().getLength();
                codeArea.replaceSelection(replaceTextField.getText());
            }

            updateAllSearches();
        });

        codeArea.caretPositionProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || JArmEmuApplication.getStatus() != Status.EDITING) return;

            JArmEmuApplication.getAutocompletionController().caretMoved();
        });

        editorScroll.estimatedScrollYProperty().addListener((obs, oldValue, newValue) -> JArmEmuApplication.getAutocompletionController().scroll());

        regex.setOnAction(actionEvent -> updateAllSearches());
        word.setOnAction(actionEvent -> updateAllSearches());
        caseSensitivity.setOnAction(actionEvent -> updateAllSearches());

        this.realTimeParser.start();
        closed = false;
    }

    public FileEditor(@NotNull File path) {
        this(path.getName(), "");
        this.path = path;
        reload();
    }

    public CodeArea getCodeArea() {
        return codeArea;
    }

    public VirtualizedScrollPane<CodeArea> getScrollPane() {
        return editorScroll;
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public EditorContextMenu getContextMenu() {
        return contextMenu;
    }

    /**
     * @param line the number of the line to test
     * @return true if the line contains a breakpoint, false otherwise
     */
    public boolean hasBreakPoint(int line) {
        return lineFactory.hasBreakpoint(line);
    }

    public void toggleBreakpoint(int line) {
        lineFactory.onToggleBreakpoint(line);
    }

    public void clearLineMarkings() {
        this.lineFactory.clearMarkings();
    }

    /**
     * Mark the scheduled line as executed and unmark the previous line.
     */
    public void markExecuted() {
        this.lineFactory.markExecuted();
    }

    /**
     * Mark the line as scheduled (next to be executed) and the scheduled line as executed.
     *
     * @param line the line number to mark as scheduled
     */
    public void markForward(int line) {
        if (line >= 0) {
            codeArea.moveTo(line, 0);
            codeArea.requestFollowCaret();

            this.lineFactory.markForward(line);
        }
    }

    /**
     * Move the cursor to the line.
     *
     * @param line the line number in the editor
     */
    public void goTo(int line) {
        codeArea.moveTo(line, 0);
        codeArea.requestFollowCaret();

        lineFactory.goTo(line);
    }

    /**
     * @return the line number over which is the mouse
     */
    public int getMouseLine() {
        int lineNum = codeArea.getParagraphs().size();
        try {
            for (int i = 0; i < lineNum; i++) {
                Optional<Bounds> optionalBounds = codeArea.getParagraphBoundsOnScreen(i);
                if (optionalBounds.isPresent()) {
                    Bounds bounds = optionalBounds.get();
                    Point mousePos = MouseInfo.getPointerInfo().getLocation();
                    if (bounds.contains(new Point2D(mousePos.x, mousePos.y))) return i;
                }
            }
        } catch (NullPointerException e) {
            return -1;
        }

        return -1;
    }

    /**
     * @return current line number (starting from 1, same as visually)
     */
    public int getCurrentLine() {
        return codeArea.getCurrentParagraph() + 1;
    }

    /**
     * @param pos the position in the text (starting from 1, same as visually)
     * @return line number from pos
     */
    public int getLineFromPos(int pos) {
        return codeArea.offsetToPosition(pos, TwoDimensional.Bias.Forward).getMajor();
    }

    /**
     * @return the number of lines in the editor
     */
    public int getTotalLineNumber() {
        return codeArea.getParagraphs().size();
    }

    /**
     * @return le numéro du caractère au-dessus duquel se trouve la souris
     */
    public int getMousePosition() {
        int lineNum = codeArea.getLength();
        for (int i = 0; i < lineNum; i++) {
            Optional<Bounds> optionalBounds = codeArea.getCharacterBoundsOnScreen(i, i + 1);
            if (optionalBounds.isPresent()) {
                Bounds bounds = optionalBounds.get();
                Point mousePos = MouseInfo.getPointerInfo().getLocation();
                if (bounds.contains(new Point2D(mousePos.x, mousePos.y))) return i;
            }
        }

        return -1;
    }

    public boolean isMouseOverSelection() {
        Optional<Bounds> optionalBounds = codeArea.getSelectionBounds();
        if (optionalBounds.isPresent()) {
            Bounds bounds = optionalBounds.get();
            Point mousePos = MouseInfo.getPointerInfo().getLocation();
            return bounds.contains(new Point2D(mousePos.x, mousePos.y));
        }

        return false;
    }

    /**
     * Ouvre le menu rechercher/remplacer
     */
    public void openFindAndReplace() {
        findPane.setVisible(true);
        findTextField.requestFocus();
    }

    /**
     * Ferme le menu rechercher/remplacer
     */
    public void closeFindAndReplace() {
        findPane.setVisible(false);
    }

    /**
     * Alterne l'ouverture du menu rechercher/remplacer
     */
    public void toggleFindAndReplace() {
        if (findPane.isVisible() || JArmEmuApplication.getStatus() == Status.SIMULATING) closeFindAndReplace();
        else openFindAndReplace();
    }

    /**
     * Nettoie la dernière ligne marquée comme exécutée.
     *
     * @apiNote Utile lors du changement d'éditeur
     */
    public void clearLastExecuted() {
        this.lineFactory.clearLastExecuted();
    }

    public void requestClose() {
        this.fileTab.getOnCloseRequest().handle(new Event(fileTab, null, null));
    }

    /**
     * Ferme le fichier
     */
    public void close() {
        logger.info("Closing " + getFileName());
        realTimeParser.interrupt();
        JArmEmuApplication.getController().filesTabPane.getTabs().remove(fileTab);
        this.closed = true;
    }

    public boolean isClosed() {
        return closed;
    }

    /**
     * Prépare la simulation (pré-géneration des lignes)
     */
    public void prepareSimulation() {
        int lineNum = getTotalLineNumber();
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
                getSourceScanner().exportCode(path);
                setSaved(getSourceScanner().exportCode());
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
        File file = fileChooser.showSaveDialog(JArmEmuApplication.getStage());
        if (file != null && !file.isDirectory()) {
            try {
                if (!file.getAbsolutePath().endsWith(".s")) file = new File(file.getAbsolutePath() + ".s");
                logger.info("File located: " + file.getAbsolutePath());
                path = file;
                logger.info("Saving file...");
                getSourceScanner().exportCode(path);
                setSaved(getSourceScanner().exportCode());
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
                String fileContent = new SourceScanner(path, JArmEmuApplication.getEditorController().getFileIndex(this)).exportCode();
                setSaved(fileContent);
                this.codeArea.clear();
                this.codeArea.replaceText(fileContent);
                logger.info("File reloaded: " + path.getAbsolutePath());
                this.codeArea.getUndoManager().forgetHistory();
            } catch (Exception exception) {
                Platform.runLater(() -> new ExceptionDialog(exception).show());
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
     * Define the file as "Saved" with a specified content
     *
     * @param text the content which was saved
     * @apiNote Used to manage the '*' in the file title name
     */
    private void setSaved(@NotNull String text) {
        saved = true;
        lastSaveContent = text;
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
     * @return a new SourceScanner of the modified file
     */
    public SourceScanner getSourceScanner() {
        return new SourceScanner(codeArea.getText(), path == null ? JArmEmuApplication.formatMessage("%menu.file.newFile") : path.getName(), JArmEmuApplication.getEditorController().getFileIndex(this));
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
        return JArmEmuApplication.getController().filesTabPane.getTabs().indexOf(fileTab);
    }

    /**
     * @return l'indice réel du fichier lié à l'éditeur
     */
    public int getRealIndex() {
        return JArmEmuApplication.getEditorController().getFileIndex(this);
    }

    /**
     * Initializes a new real time parser corresponding to the current configs
     */
    public void initializeRealTimeParser() {
        if (realTimeParser != null && realTimeParser.isAlive()) {
            realTimeParser.interrupt();
        }
        if (JArmEmuApplication.getSettingsController().getRealTimeParser() == 0) {
            logger.info("Initializing Smart Highlighter in " + getFileName());
            realTimeParser = new SmartHighlighter(this);
        } else {
            logger.info("Initializing Keyword Highlighter in " + getFileName());
            realTimeParser = new KeywordHighlighter(this);
        }
    }

    /**
     * Force-refreshes the real time parser
     */
    public void forceRefreshRealTimeParser() {
        logger.info("Refreshing real time parser in " + getFileName());
        codeArea.clearStyle(0, codeArea.getLength());
        if (!realTimeParser.isAlive() && !realTimeParser.isInterrupted()) {
            realTimeParser.start();
        }
        realTimeParser.markDirty(1, getTotalLineNumber() + 1);
    }

    public RealTimeParser getRealTimeParser() {
        return realTimeParser;
    }

    /**
     * Indique les coordonnées des recherches dans le texte
     *
     * @param text le texte à scanner
     * @return une liste de Find qui décrit le début et la fin de chaque résultat
     */
    public List<Find> getSearch(String text) {
        if (!findPane.isVisible()) return Collections.emptyList();
        ArrayList<Find> rtn = new ArrayList<>();

        String find = findTextField.getText();
        if (find != null && !find.isEmpty()) {
            if (!regex.isSelected()) {
                find = Pattern.quote(find);
            }

            if (word.isSelected()) {
                find = "\\b" + find + "\\b";
            }

            if (!caseSensitivity.isSelected()) {
                find = "(?i)" + find + "(?-i)";
            }

            try {
                Matcher matcher = Pattern.compile(find).matcher(text);

                while (matcher.find()) {
                    rtn.add(new Find(matcher.start(), matcher.end()));
                }
            } catch (PatternSyntaxException ignored) {
            }
        }

        return rtn;
    }

    public void updateAllSearches() {
        List<Find> newFinds = getSearch(codeArea.getText());
        Set<Find> finds = new HashSet<>(newFinds);
        if (previousFind != null) finds.addAll(previousFind);

        for (Find find : finds) {
            this.realTimeParser.markDirty(
                    getLineFromPos(find.start()),
                    getLineFromPos(find.end())
            );
        }

        previousFind = newFinds;
    }

    public synchronized void handleKeyPressed(KeyEvent keyEvent) {
        if (!codeArea.isEditable()) return;

        if (keyEvent.getCode() == KeyCode.ENTER) {
            String add = "";
            synchronized (realTimeParser.getLock()) {
                if (getRealTimeParser().lineDefinesLabel(getCurrentLine() - 1)) {
                    add = "\t";
                }
            }

            Matcher matcher = whiteSpace.matcher(codeArea.getParagraph(getCurrentLine() - 2).getSegments().getFirst());
            if (matcher.find()) {
                String finalAdd = add;
                Platform.runLater(() -> codeArea.insertText(codeArea.getCaretPosition(), matcher.group() + finalAdd));
            }
            else if (!add.isEmpty()) {
                String finalAdd = add;
                Platform.runLater(() -> codeArea.insertText(codeArea.getCaretPosition(), finalAdd));
            }
        } else if (keyEvent.getCode() == KeyCode.TAB) {
            int selStart = codeArea.getCaretSelectionBind().getStartPosition();
            int selEnd = codeArea.getCaretSelectionBind().getEndPosition();

            boolean shift = keyEvent.isShiftDown();

            if (selStart == selEnd) {
                if (shift) {
                    int parN = getCurrentLine() - 1;
                    Paragraph<?, ?, ?> par = codeArea.getParagraph(parN);
                    if (par.charAt(0) == '\t' || par.charAt(0) == '\t') {
                        codeArea.deleteText(parN, 0, parN, 1);
                    }

                    int nPos = 0;
                    for (int i = 0; i < par.length(); i++) {
                        if (par.charAt(i) == '\t' || par.charAt(i) == '\t') nPos = i;
                        else break;
                    }
                    codeArea.moveTo(parN, nPos);
                } else {
                    codeArea.insertText(codeArea.getCaretPosition(), "\t");
                }
            } else {
                int start = codeArea.getCaretSelectionBind().getStartParagraphIndex();
                int end = codeArea.getCaretSelectionBind().getEndParagraphIndex();

                for (int parN = start; parN <= end; parN++) {
                    if (shift) {
                        Paragraph<?, ?, ?> par = codeArea.getParagraph(parN);
                        if (par.charAt(0) == '\t' || par.charAt(0) == '\t') {
                            codeArea.deleteText(parN, 0, parN, 1);
                        }
                    } else {
                        codeArea.insertText(parN, 0, "\t");
                    }
                }

                codeArea.selectRange(start, 0, end, codeArea.getParagraph(end).length());
                this.realTimeParser.markDirty(start + 1, end + 2);
            }
        }
    }

    public JArmEmuLineFactory getLineFactory() {
        return lineFactory;
    }
}
