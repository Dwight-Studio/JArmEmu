package fr.dwightstudio.jarmemu.gui;

import atlantafx.base.layout.ModalBox;
import atlantafx.base.theme.Styles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

public class ModalDialog {

    private final ModalBox box;

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
    }

    public ModalDialog(Node content) {
        box = new ModalBox(content);
    }

    public ModalBox getModalBox() {
        return box;
    }
}
