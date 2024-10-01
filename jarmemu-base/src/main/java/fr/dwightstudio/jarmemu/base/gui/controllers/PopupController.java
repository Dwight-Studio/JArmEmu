package fr.dwightstudio.jarmemu.base.gui.controllers;

import atlantafx.base.controls.Popover;
import atlantafx.base.theme.Styles;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuPopups;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2RoundAL;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class PopupController implements Initializable {

    public static final Runnable NOTHING = () -> {};

    private static final ArrayList<Popup> popovers = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(PopupController.class.getSimpleName());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        JArmEmuPopups.build();
    }

    public static void popoverMaker(Node location, Popover.ArrowLocation arrowLocation) {
        popoverMaker(location, arrowLocation, NOTHING, NOTHING);
    }

    public static void popoverMaker(Node location, Popover.ArrowLocation arrowLocation, Runnable before, Runnable after) {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(15, 15, 0, 15));

        Text text = new Text();
        TextFlow textFlow = new TextFlow(text);
        textFlow.setMaxWidth(300);
        textFlow.setTextAlignment(TextAlignment.JUSTIFY);

        Button prev = new Button();
        Button next = new Button();

        AnchorPane secondRow = new AnchorPane(prev, next);
        AnchorPane.setLeftAnchor(prev, 0.0);
        AnchorPane.setRightAnchor(next, 0.0);

        vbox.getChildren().addAll(
                textFlow,
                secondRow
        );
        Popover popover = new Popover(vbox);
        popover.setArrowLocation(arrowLocation);
        popover.setDetachable(false);
        popover.setAutoHide(false);
        popovers.add(new Popup(popover, location, text, prev, next, before, after));
    }

    public static void addLinks() {
        for (int i = 0; i < popovers.size(); i++) {
            Popup popup = popovers.get(i);
            Text text = popup.text;
            Button prev = popup.prev;
            Button next = popup.next;

            String title = JArmEmuApplication.formatMessage("%tour." + i + ".title");
            String message = JArmEmuApplication.formatMessage("%tour." + i + ".message");

            if (!title.isBlank()) {
                popup.popover.setTitle(title);
                popup.popover.setHeaderAlwaysVisible(true);
            } else {
                popup.popover.setHeaderAlwaysVisible(false);
            }

            text.setText(message);

            prev.setContentDisplay(ContentDisplay.LEFT);
            next.setContentDisplay(ContentDisplay.RIGHT);

            if (i == 0) {
                prev.setVisible(false);
            } else {;
                prev.setGraphic(new FontIcon(Material2RoundAL.ARROW_BACK));
                prev.getStyleClass().addAll(Styles.ROUNDED, Styles.FLAT, Styles.ACCENT);
                prev.setText(JArmEmuApplication.formatMessage("%tour.buttons.prev"));

                setOpenPrev(prev, i);
            }

            if (i == popovers.size() - 1) {
                next.setGraphic(new FontIcon(Material2RoundAL.CHECK));
                next.getStyleClass().addAll(Styles.ROUNDED, Styles.FLAT, Styles.SUCCESS);
                next.setText(JArmEmuApplication.formatMessage("%tour.buttons.done"));

                setClose(next, i);
            } else {
                next.setGraphic(new FontIcon(Material2RoundAL.ARROW_FORWARD));
                next.getStyleClass().addAll(Styles.ROUNDED, Styles.FLAT, Styles.ACCENT);
                next.setText(JArmEmuApplication.formatMessage("%tour.buttons.next"));

                setOpenNext(next, i);
            }
        }
    }

    private static void setOpenPrev(Button prev, int i) {
        prev.setOnAction(event -> {
            Popup popup = popovers.get(i);
            popup.popover.hide();
            popup.after.run();
            for (int j = i - 1; j >= 0; j--) {
                try {
                    popup = popovers.get(j);
                    popup.before.run();
                    popup.popover.show(popovers.get(j).location);
                    break;
                } catch (Exception exception) {
                    logger.warning("Cannot open Popup: " + exception.getMessage());
                    popup.after.run();
                }
            }
        });
    }

    private static void setOpenNext(Button next, int i) {
        next.setOnAction(event -> {
            Popup popup = popovers.get(i);
            popup.popover.hide();
            popup.after.run();
            for (int j = i + 1; j < popovers.size(); j++) {
                try {
                    popup = popovers.get(j);
                    popup.before.run();
                    popup.popover.show(popovers.get(j).location);
                    break;
                } catch (Exception exception) {
                    logger.warning("Cannot open Popup: " + exception.getMessage());
                    popup.after.run();
                }
            }
        });
    }

    private static void setClose(Button button, int i) {
        button.setOnAction(event -> {
            Popup popup = popovers.get(i);
            popup.popover.hide();
            popup.after.run();
        });
    }

    public static Node getMainPane() {
        return JArmEmuApplication.getController().mainPane;
    }

    public static Node getTabPaneHeader() {
        return JArmEmuApplication.getController().filesTabPane.lookup(".tab-header-area");
    }

    public static Node getToolSimulate() {
        return JArmEmuApplication.getController().toolSimulate;
    }

    public static Node getToolContinue() {
        return JArmEmuApplication.getController().toolContinue;
    }

    public static Node getToolStop() {
        return JArmEmuApplication.getController().toolStop;
    }

    public static Node getRegistersPane() {
        return JArmEmuApplication.getController().registersPane;
    }

    public static ArrayList<Popup> getPopovers() {
        return popovers;
    }

    public record Popup(Popover popover, Node location, Text text, Button prev, Button next, Runnable before, Runnable after) {
    }
}
