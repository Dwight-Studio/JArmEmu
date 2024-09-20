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

package fr.dwightstudio.jarmemu.base.gui.factory;

import fr.dwightstudio.jarmemu.base.Status;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.controllers.FileEditor;
import fr.dwightstudio.jarmemu.base.gui.enums.LineStatus;
import fr.dwightstudio.jarmemu.base.sim.entity.FilePos;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.css.PseudoClass;
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

    public static final PseudoClass PSEUDO_CLASS_EXECUTED = PseudoClass.getPseudoClass("executed");
    public static final PseudoClass PSEUDO_CLASS_SCHEDULED = PseudoClass.getPseudoClass("scheduled");
    public static final PseudoClass PSEUDO_CLASS_FLAGGED = PseudoClass.getPseudoClass("flagged");

    private final HashMap<Integer, LineManager> managers;
    private LineManager lastScheduled;
    private LineManager lastExecuted;
    private final FileEditor fileEditor;

    public JArmEmuLineFactory(FileEditor fileEditor) {
        this.managers = new HashMap<>();
        this.fileEditor = fileEditor;
    }

    /**
     * Get a line from the cache or generate it if absent.
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
     * Clear markings.
     */
    public void clearMarkings() {
        managers.values().forEach(lineManager -> lineManager.markLine(LineStatus.NONE));
    }

    /**
     * Mark the scheduled line as executed and unmark the previous line.
     */
    public void markExecuted() {
        if (lastScheduled != null) lastScheduled.markLine(LineStatus.EXECUTED);

        if (lastExecuted != null && lastScheduled != lastExecuted && lastExecuted.getStatus() != LineStatus.SCHEDULED) lastExecuted.markLine(LineStatus.NONE);
        lastExecuted = lastScheduled;
    }

    /**
     * Mark the line as scheduled (next to be executed) and the scheduled line as executed.
     *
     * @param line the line number to mark as scheduled
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
     * Unmark previously executed line.
     *
     * @apiNote Used when changing editor
     */
    public void clearLastExecuted() {
        if (lastExecuted != null) lastExecuted.markLine(LineStatus.NONE);
    }

    /**
     * @param line the line to test
     * @return true if the line contains a breakpoint, false otherwise
     */
    public boolean hasBreakpoint(int line) {
        if (!managers.containsKey(line)) return false;

        return managers.get(line).hasBreakpoint();
    }

    /**
     * Add a breakpoint.
     *
     * @param line the line number
     */
    public void onToggleBreakpoint(int line) {
        if (!managers.containsKey(line)) return;

        managers.get(line).toggle();
    }

    /**
     * Pre-generate lines to reduce impact on performances.
     *
     * @param lineNum last line number (exclusive)
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

            grid.getStyleClass().add("lineno");
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

            JArmEmuApplication.getInstance().status.addListener((obs, oldVal, newVal) -> {
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
                    lineNo.setText(String.format("%4d", line + 1));
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

                grid.pseudoClassStateChanged(PSEUDO_CLASS_EXECUTED, status == LineStatus.EXECUTED);
                grid.pseudoClassStateChanged(PSEUDO_CLASS_SCHEDULED, status == LineStatus.SCHEDULED);
                grid.pseudoClassStateChanged(PSEUDO_CLASS_FLAGGED, status == LineStatus.FLAGGED);
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
                int pos = JArmEmuApplication.getCodeInterpreter().getPosition(
                        new FilePos(
                                JArmEmuApplication.getEditorController().getFileIndex(fileEditor),
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
                lineNo.setText(String.format("%4d", line + 1));
                lineNo.getStyleClass().clear();
                lineNo.getStyleClass().add("lineno");
            }
        }
    }
}