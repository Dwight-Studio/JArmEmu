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

import fr.dwightstudio.jarmemu.gui.enums.LineStatus;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.IntFunction;

public class JArmEmuLineFactory implements IntFunction<Node> {

    public ArrayList<Integer> breakpoints = new ArrayList<>();
    private final HashMap<Integer, HBox> nodes = new HashMap<>();

    @Override
    public Node apply(int line) {
        Node rtn = nodes.get(line);

        if (rtn == null) {
            rtn = generate(line);
        }

        return rtn;
    }

    public HBox generate(int line) {
        HBox rtn = new HBox();

        rtn.setMaxWidth(80);
        rtn.setMinWidth(80);
        rtn.setPrefWidth(80);

        Text lineNo = new Text();
        Text breakpoint = new Text();

        lineNo.getStyleClass().add("lineno");
        breakpoint.getStyleClass().add("breakpoint");

        lineNo.setText(String.format("%5d", line));
        if (breakpoints.contains(line)) breakpoint.setText("  ⬤"); else breakpoint.setText("   ");

        breakpoint.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) toggle(line, breakpoint);
        });

        rtn.getChildren().add(lineNo);
        rtn.getChildren().add(breakpoint);

        nodes.put(line, rtn);
        return rtn;
    }

    private void toggle(int id, Text label) {
        if (breakpoints.contains(id)) breakpoints.remove((Integer) id); else breakpoints.add(id);
        if (breakpoints.contains(id)) label.setText("  ⬤"); else label.setText("   ");
    }

    public void markLine(int line, LineStatus lineStatus) {
        if (nodes.containsKey(line)) {
            HBox hBox = nodes.get(line);
            hBox.getStyleClass().clear();
            switch (lineStatus) {
                case EXECUTED -> hBox.getStyleClass().add("executed");
                case SCHEDULED -> hBox.getStyleClass().add("scheduled");
            }
        }
    }

    public void pregenAll(int lineNum) {
        for (int i = 0; i < lineNum; i++) {
            apply(i);
        }
    }
}