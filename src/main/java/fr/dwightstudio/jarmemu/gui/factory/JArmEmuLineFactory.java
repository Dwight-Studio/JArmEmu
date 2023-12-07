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

package fr.dwightstudio.jarmemu.gui.factory;

import fr.dwightstudio.jarmemu.Status;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.controllers.FileEditor;
import fr.dwightstudio.jarmemu.gui.enums.LineStatus;
import fr.dwightstudio.jarmemu.sim.obj.FileLine;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;
import java.util.function.IntFunction;

public class JArmEmuLineFactory implements IntFunction<Node> {

    private final JArmEmuApplication application;

    private final HashMap<Integer, GridPane> cache;
    private final HashMap<Integer, LineManager> managers;
    private final FileEditor fileEditor;

    public JArmEmuLineFactory(JArmEmuApplication application, FileEditor fileEditor) {
        this.application = application;
        this.cache = new HashMap<>();
        this.managers = new HashMap<>();
        this.fileEditor = fileEditor;
    }

    @Override
    public Node apply(int line) {
        Node rtn = cache.get(line);

        if (rtn == null) {
            rtn = generate(line);
        } else {
            rtn = rtn.getParent();
        }

        return rtn;
    }

    /**
     * Génère une ligne et la stocke dans le cache.
     *
     * @param line
     * @return
     */
    public HBox generate(int line) {
        GridPane grid = new GridPane();

        grid.getStyleClass().add("none");
        grid.setMaxWidth(80);
        grid.setPrefWidth(GridPane.USE_COMPUTED_SIZE);
        grid.setMinWidth(80);
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setAlignment(Pos.CENTER_RIGHT);
        grid.getColumnConstraints().addAll(new ColumnConstraints(40), new ColumnConstraints(40));

        grid.setPadding(new Insets(0, 5, 0, 0));

        Text lineNo = new Text();
        Text linePos = new Text();


        LineManager property = new LineManager(line, lineNo, linePos);

        lineNo.getStyleClass().add("lineno");
        lineNo.setText(String.format("%4d", line));
        lineNo.setTextAlignment(TextAlignment.RIGHT);


        linePos.getStyleClass().add("breakpoint");
        linePos.setTextAlignment(TextAlignment.RIGHT);

        managers.put(line, property);

        grid.add(lineNo, 0, 0);
        grid.add(linePos, 1, 0);

        HBox rtn = new HBox(grid);
        HBox.setMargin(grid, new Insets(0, 5, 0, 0));

        cache.put(line, grid);
        return rtn;
    }

    public void markLine(int line, LineStatus lineStatus) {
        if (cache.containsKey(line)) {
            GridPane grid = cache.get(line);
            grid.getStyleClass().clear();
            switch (lineStatus) {
                case EXECUTED -> grid.getStyleClass().add("executed");
                case SCHEDULED -> grid.getStyleClass().add("scheduled");
                case NONE -> grid.getStyleClass().add("none");
            }
        }
    }

    /**
     * @param line le numéro de la ligne
     * @return vrai si la ligne contient un breakpoint, faux sinon
     */
    public boolean hasBreakpoint(int line) {
        if (!managers.containsKey(line)) return false;

        return managers.get(line).hasBreakpoint();
    }

    public void pregenAll(int lineNum) {
        for (int i = 0; i < lineNum; i++) {
            apply(i);
        }
    }

    public class LineManager {

        private final int line;
        private final Text lineNo;
        private final Text linePos;
        private boolean breakpoint;
        private boolean show;

        public LineManager(int line, Text lineNo, Text linePos) {
            this.line = line;
            this.lineNo = lineNo;
            this.linePos = linePos;
            breakpoint = false;
            show = false;

            application.status.addListener((obs, oldVal, newVal) -> {
                show = (newVal == Status.SIMULATING);
                update();
            });

            lineNo.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) toggle();
            });
            lineNo.setOnMouseEntered(event -> {

                if (!breakpoint) {
                    lineNo.setText("   ⬤");
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
        }

        public void toggle() {
            breakpoint = !breakpoint;
            update();
        }

        public boolean hasBreakpoint() {
            return breakpoint;
        }

        private void update() {
            if (show) {
                int pos = application.getCodeInterpreter().getPosition(
                        new FileLine(
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
                lineNo.setText("   ⬤");
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