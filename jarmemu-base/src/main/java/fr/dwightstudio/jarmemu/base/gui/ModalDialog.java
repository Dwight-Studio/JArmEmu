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

package fr.dwightstudio.jarmemu.base.gui;

import atlantafx.base.layout.ModalBox;
import atlantafx.base.theme.Styles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ModalDialog {

    private final ModalBox box;
    private final VBox vBox;

    public ModalDialog(Node graphic, String title, Node content, Control ... controls) {
        Text titleText = new Text(title);
        titleText.getStyleClass().add(Styles.TITLE_3);

        VBox contentBox = new VBox(10,
                titleText,
                content
        );

        HBox bodyBox = new HBox(20,
                graphic,
                contentBox
        );

        HBox controlBox = new HBox(20, controls);
        controlBox.setAlignment(Pos.CENTER_RIGHT);

        VBox mainBox = new VBox(20,
                bodyBox,
                controlBox
        );

        VBox.setVgrow(mainBox, Priority.ALWAYS);

        AnchorPane.setTopAnchor(mainBox, 20.0);
        AnchorPane.setRightAnchor(mainBox, 20.0);
        AnchorPane.setLeftAnchor(mainBox, 20.0);
        AnchorPane.setBottomAnchor(mainBox, 20.0);

        box = new ModalBox(mainBox);
        box.setMaxSize(mainBox.getPrefWidth() + 40, mainBox.getPrefHeight() + 40);

        vBox = null;
    }

    public ModalDialog(Node content, double width, double height) {
        AnchorPane.setTopAnchor(content, 20.0);
        AnchorPane.setRightAnchor(content, 20.0);
        AnchorPane.setLeftAnchor(content, 20.0);
        AnchorPane.setBottomAnchor(content, 20.0);
        box = new ModalBox(content);
        box.setMaxSize(width + 40, height + 40);

        vBox = null;
    }

    public ModalDialog(Node content) {
        AnchorPane.setTopAnchor(content, 20.0);
        AnchorPane.setRightAnchor(content, 20.0);
        AnchorPane.setLeftAnchor(content, 20.0);
        AnchorPane.setBottomAnchor(content, 20.0);

        box = new ModalBox(content);

        vBox = new VBox(box);
        vBox.setPickOnBounds(false);
        VBox.setMargin(box, new Insets(50));
        vBox.setAlignment(Pos.CENTER);
    }

    public ModalBox getModalBox() {
        return box;
    }

    public Node getNode() {
        return vBox == null ? box : vBox;
    }
}
