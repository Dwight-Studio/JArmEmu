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

package fr.dwightstudio.jarmemu.gui.factory;

import fr.dwightstudio.jarmemu.Status;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.controllers.FileEditor;
import fr.dwightstudio.jarmemu.gui.enums.LineStatus;
import fr.dwightstudio.jarmemu.sim.entity.FilePos;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.function.IntFunction;

public class JArmEmuLineFactory implements IntFunction<Node> {

    private final JArmEmuApplication application;
    private final HashMap<Integer, LineManager> managers;
    private LineManager lastScheduled;
    private LineManager lastExecuted;
    private final FileEditor fileEditor;

    public JArmEmuLineFactory(JArmEmuApplication application, FileEditor fileEditor) {
        this.application = application;
        this.managers = new HashMap<>();
        this.fileEditor = fileEditor;
    }

    /**
     * Récupère une marge de ligne (dans le cache, ou fraiche).
     *
     * @param line le numéro de ligne
     * @return une marge de ligne
     */
    @Override
    public Node apply(int line) {
        if (managers.containsKey(line)) {
            return managers.get(line).getNode();
        } else {
            LineManager manager = new LineManager(line);
            return manager.getNode();
        }
    }

    /**
     * Marque une ligne.
     *
     * @param line le numéro de la ligne
     * @param lineStatus le status de la ligne
     */
    public void markLine(int line, LineStatus lineStatus) {
        if (managers.containsKey(line)) {
            LineManager manager = managers.get(line);
            manager.markLine(lineStatus);
            if (lineStatus == LineStatus.SCHEDULED) lastScheduled = manager;
        }
    }

    /**
     * Nettoie le marquage.
     */
    public void clearMarkings() {
        managers.values().forEach(lineManager -> lineManager.markLine(LineStatus.NONE));
    }

    /**
     * Marque comme executé la dernière ligne prévue tout en nettoyant l'ancienne ligne exécutée.
     */
    public void markExecuted() {
        if (lastScheduled != null) lastScheduled.markLine(LineStatus.EXECUTED);

        if (lastExecuted != null && lastScheduled != lastExecuted) lastExecuted.markLine(LineStatus.NONE);
        lastExecuted = lastScheduled;
    }

    /**
     * Marque comme prévu une ligne tout en marquant executé l'ancienne ligne prévue.
     *
     * @param line le numéro de la ligne
     */
    public void markForward(int line) {
        if (managers.containsKey(line)) {
            LineManager manager = managers.get(line);
            manager.markLine(LineStatus.SCHEDULED);

            markExecuted();
            lastScheduled = manager;
        }
    }

    /**
     * Nettoie la dernière ligne marquée comme exécutée.
     *
     * @apiNote Utile lors du changement d'éditeur
     */
    public void clearLastExecuted() {
        if (lastExecuted != null) lastExecuted.markLine(LineStatus.NONE);
    }

    /**
     * @param line le numéro de la ligne
     * @return vrai si la ligne contient un breakpoint, faux sinon
     */
    public boolean hasBreakpoint(int line) {
        if (!managers.containsKey(line)) return false;

        return managers.get(line).hasBreakpoint();
    }

    /**
     * Ajoute un breakpoint
     *
     * @param line le numéro de la ligne
     */
    public void onToggleBreakpoint(int line) {
        if (!managers.containsKey(line)) return;

        managers.get(line).toggle();
    }

    /**
     * Pré-génère des lignes pour améliorer les performances.
     * @param lineNum le numéro de la dernière ligne (exclusif)
     */
    public void pregen(int lineNum) {
        for (int i = 0; i < lineNum; i++) {
            apply(i);
        }
    }

    public void goTo(int line) {
        LineManager manager = managers.get(line);

        new Timeline(
                new KeyFrame(Duration.millis(0), event -> manager.markLine(LineStatus.FLAGGED)),
                new KeyFrame(Duration.millis(500), event -> manager.markLine(LineStatus.NONE)),
                new KeyFrame(Duration.millis(1000), event -> manager.markLine(LineStatus.FLAGGED)),
                new KeyFrame(Duration.millis(1500), event -> manager.markLine(LineStatus.NONE)),
                new KeyFrame(Duration.millis(2000), event -> manager.markLine(LineStatus.FLAGGED)),
                new KeyFrame(Duration.millis(2500), event -> manager.markLine(LineStatus.NONE))
        ).play();
    }

    public class LineManager {

        private final int line;
        private final Text lineNo;
        private final Text linePos;
        private final GridPane grid;
        private final HBox hBox;
        private boolean breakpoint;
        private boolean show;
        private LineStatus status;

        public LineManager(int line) {
            this.line = line;
            breakpoint = false;
            show = false;
            status = LineStatus.NONE;

            grid = new GridPane();

            grid.getStyleClass().add("none");
            grid.setMaxWidth(80);
            grid.setPrefWidth(GridPane.USE_COMPUTED_SIZE);
            grid.setMinWidth(80);
            grid.setHgap(0);
            grid.setVgap(0);
            grid.setAlignment(Pos.CENTER_RIGHT);
            grid.getColumnConstraints().addAll(new ColumnConstraints(40), new ColumnConstraints(40));

            grid.setPadding(new Insets(0, 5, 0, 0));

            lineNo = new Text();
            linePos = new Text();

            lineNo.getStyleClass().add("lineno");
            lineNo.setText(String.format("%4d", line + 1));
            lineNo.setTextAlignment(TextAlignment.RIGHT);


            linePos.getStyleClass().add("breakpoint");
            linePos.setTextAlignment(TextAlignment.RIGHT);

            grid.add(lineNo, 0, 0);
            grid.add(linePos, 1, 0);

            hBox = new HBox(grid);
            HBox.setMargin(grid, new Insets(0, 5, 0, 0));

            application.status.addListener((obs, oldVal, newVal) -> {
                show = (newVal == Status.SIMULATING);
                update();
            });

            lineNo.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) toggle();
            });
            lineNo.setOnMouseEntered(event -> {

                if (!breakpoint) {
                    lineNo.setText("  ⬤ ");
                    lineNo.getStyleClass().clear();
                    lineNo.getStyleClass().add("lineno-over");
                }
            });
            lineNo.setOnMouseExited(event -> {

                if (!breakpoint) {
                    lineNo.setText(String.format("%4d", line));
                    lineNo.getStyleClass().clear();
                    lineNo.getStyleClass().add("lineno");
                }
            });

            managers.put(line, this);
        }

        public void toggle() {
            breakpoint = !breakpoint;
            update();
        }

        public void markLine(LineStatus status) {
            if (status != this.status) {

                this.status = status;
                if (status == LineStatus.EXECUTED) lastScheduled = this;

                grid.getStyleClass().clear();
                switch (status) {
                    case EXECUTED -> grid.getStyleClass().add("executed");
                    case SCHEDULED -> grid.getStyleClass().add("scheduled");
                    case FLAGGED -> grid.getStyleClass().add("flagged");
                    case NONE -> grid.getStyleClass().add("none");
                }
            }
        }

        public Node getNode() {
            return hBox;
        }

        public LineStatus getStatus() {
            return status;
        }

        public boolean hasBreakpoint() {
            return breakpoint;
        }

        public void addBreakpoint() {
            breakpoint = true;
        }

        private void update() {
            if (show) {
                int pos = application.getCodeInterpreter().getPosition(
                        new FilePos(
                                application.getEditorController().getFileIndex(fileEditor),
                                line
                        )
                );

                if (pos < 0) {
                     linePos.setText("     ");
                } else {
                    linePos.setText(String.format("%04x", pos).toUpperCase());
                }
            } else {
                linePos.setText("     ");
            }


            if (breakpoint) {
                lineNo.setText("  ⬤ ");
                lineNo.getStyleClass().clear();
                lineNo.getStyleClass().add("breakpoint");
            } else {
                lineNo.setText(String.format("%4d", line));
                lineNo.getStyleClass().clear();
                lineNo.getStyleClass().add("lineno");
            }
        }
    }
}