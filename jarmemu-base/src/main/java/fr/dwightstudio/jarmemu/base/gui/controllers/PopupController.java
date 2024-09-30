package fr.dwightstudio.jarmemu.base.gui.controllers;

import atlantafx.base.controls.Popover;
import atlantafx.base.theme.Styles;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuPopups;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

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
        vbox.setPadding(new Insets(15));
        HBox firstRow = new HBox(new TextFlow(new Text(JArmEmuApplication.formatMessage(text))));
        ((TextFlow) firstRow.getChildren().getFirst()).setMaxWidth(250);
        ((TextFlow) firstRow.getChildren().getFirst()).setTextAlignment(TextAlignment.JUSTIFY);
        HBox secondRow = new HBox(new Button(), new Button());
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
        for (int i = 0; i < popovers.size(); i++) {
            Button prev = ((Button) ((HBox) ((VBox) popovers.get(i).popover.getContentNode()).getChildren().getLast()).getChildren().getFirst());
            prev.setText("prev");
            prev.getStyleClass().add(Styles.ACCENT);
            Button next = ((Button) ((HBox) ((VBox) popovers.get(i).popover.getContentNode()).getChildren().getLast()).getChildren().getLast());
            next.setText("next");
            next.getStyleClass().add(Styles.ACCENT);
            if (i == 0) {
                prev.setDisable(true);
                int finalI = i;
                next.setOnAction(event -> {
                    popovers.get(finalI).popover.hide();
                    for (int j = finalI + 1; j < popovers.size(); j++) {
                        try {
                            popovers.get(j).popover.show(popovers.get(j).location);
                            break;
                        } catch (Exception ignored) {}
                    }
                });
            } else if (i == popovers.size() - 1) {
                int finalI = i;
                prev.setOnAction(event -> {
                    popovers.get(finalI).popover.hide();
                    for (int j = finalI - 1; j >= 0; j--) {
                        try {
                            popovers.get(j).popover.show(popovers.get(j).location);
                            break;
                        } catch (Exception ignored) {}
                    }
                });
                next.setDisable(true);
            } else {
                int finalI = i;
                prev.setOnAction(event -> {
                    popovers.get(finalI).popover.hide();
                    for (int j = finalI - 1; j >= 0; j--) {
                        try {
                            popovers.get(j).popover.show(popovers.get(j).location);
                            break;
                        } catch (Exception ignored) {}
                    }
                });
                next.setOnAction(event -> {
                    popovers.get(finalI).popover.hide();
                    for (int j = finalI + 1; j < popovers.size(); j++) {
                        try {
                            popovers.get(j).popover.show(popovers.get(j).location);
                            break;
                        } catch (Exception ignored) {}
                    }
                });
            }
        }
    }

    public static Node getMainPane() {
        return JArmEmuApplication.getController().mainPane;
    }

    public static Node getToolSimulate() {
        return JArmEmuApplication.getController().toolSimulate;
    }

    public static Node getToolStop() {
        return JArmEmuApplication.getController().toolStop;
    }

    public static Node getRegistersPane() {
        return JArmEmuApplication.getController().registersPane;
    }

    public static ArrayList<PopupWithLocation> getPopovers() {
        return popovers;
    }

    public record PopupWithLocation(Popover popover, Node location) {}
}
