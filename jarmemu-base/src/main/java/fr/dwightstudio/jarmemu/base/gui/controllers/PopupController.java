package fr.dwightstudio.jarmemu.base.gui.controllers;

import atlantafx.base.controls.Popover;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuPopups;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PopupController implements Initializable {

    private static final ArrayList<PopupWithLocation> popovers = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        JArmEmuPopups.build();
    }

    public static void popoverMaker(String title, String text, Node location, Popover.ArrowLocation arrowLocation) {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));
        HBox firstRow = new HBox(new Text(JArmEmuApplication.formatMessage(text)));
        HBox secondRow = new HBox(new Hyperlink(), new Hyperlink());
        secondRow.setSpacing(10);
        secondRow.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(
                firstRow,
                secondRow
        );
        Popover popover = new Popover(vbox);
        popover.setArrowLocation(arrowLocation);
        if (title != null) {
            popover.setTitle(JArmEmuApplication.formatMessage(title));
            popover.setHeaderAlwaysVisible(true);
        } else {
            popover.setHeaderAlwaysVisible(false);
        }
        popover.setDetachable(false);
        popover.setAutoHide(false);
        popovers.add(new PopupWithLocation(popover, location));
    }

    public static void addLinks() {
        //TODO: Add a fail guard if the master node is not in the scene
        for (int i = 0; i < popovers.size(); i++) {
            Hyperlink prev = ((Hyperlink) ((HBox) ((VBox) popovers.get(i).popover.getContentNode()).getChildren().getLast()).getChildren().getFirst());
            prev.setText("prev");
            Hyperlink next = ((Hyperlink) ((HBox) ((VBox) popovers.get(i).popover.getContentNode()).getChildren().getLast()).getChildren().getLast());
            next.setText("next");
            if (i == 0) {
                prev.setDisable(true);
                int finalI = i;
                next.setOnAction(event -> {
                    popovers.get(finalI).popover.hide();
                    popovers.get(finalI + 1).popover.show(popovers.get(finalI + 1).location);
                });
            } else if (i == popovers.size() - 1) {
                int finalI = i;
                prev.setOnAction(event -> {
                    popovers.get(finalI).popover.hide();
                    popovers.get(finalI - 1).popover.show(popovers.get(finalI - 1).location);
                });
                next.setDisable(true);
            } else {
                int finalI = i;
                prev.setOnAction(event -> {
                    popovers.get(finalI).popover.hide();
                    popovers.get(finalI - 1).popover.show(popovers.get(finalI - 1).location);
                });
                next.setOnAction(event -> {
                    popovers.get(finalI).popover.hide();
                    popovers.get(finalI + 1).popover.show(popovers.get(finalI + 1).location);
                });
            }
        }
    }

    public static Node getRegistersPane() {
        return JArmEmuApplication.getController().registersPane;
    }

    public static Node getToolSimulate() {
        return JArmEmuApplication.getController().toolSimulate;
    }

    public static Node getToolStop() {
        return JArmEmuApplication.getController().toolStop;
    }

    public static ArrayList<PopupWithLocation> getPopovers() {
        return popovers;
    }

    public static class PopupWithLocation {
        public final Popover popover;
        public final Node location;
        public PopupWithLocation(Popover popover, Node location) {
            this.popover = popover;
            this.location = location;
        }
    }
}
