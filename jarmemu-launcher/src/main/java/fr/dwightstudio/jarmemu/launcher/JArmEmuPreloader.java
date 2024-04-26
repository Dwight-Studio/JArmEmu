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

package fr.dwightstudio.jarmemu.launcher;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.Styles;
import fr.dwightstudio.jarmemu.base.gui.CloseNotification;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.LoadingNotification;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class JArmEmuPreloader extends Preloader {

    private static final int MAXIMUM_PROGRESS = 6;

    private static final Logger logger = Logger.getLogger(JArmEmuPreloader.class.getSimpleName());

    private List<Image> icons;
    private Stage stage;
    private Scene scene;
    private ProgressBar progressBar;
    private Label progressLabel;
    private int progress;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        icons = List.of(
                new Image(getMediaAsStream("images/favicon@16.png")),
                new Image(getMediaAsStream("images/favicon@32.png")),
                new Image(getMediaAsStream("images/favicon@64.png")),
                new Image(getMediaAsStream("images/favicon@128.png")),
                new Image(getMediaAsStream("images/favicon@256.png")),
                new Image(getMediaAsStream("images/favicon@512.png")),
                new Image(getMediaAsStream("images/logo.png"))
        );

        progressBar = new ProgressBar();
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.getStyleClass().add(Styles.LARGE);

        progressLabel = new Label();
        progressLabel.setStyle("-fx-text-fill: white;");
        progressLabel.setLabelFor(progressLabel);

        StackPane stackPane = new StackPane(progressBar);
        stackPane.getChildren().add(progressLabel);

        Image image = new Image(getMedia("images/splash.png").toExternalForm());
        ImageView imageView = new ImageView(image);

        VBox.setMargin(stackPane, new Insets(0, 40, 0, 40));

        VBox wrapper = new VBox(imageView);
        wrapper.setStyle("-fx-background-color: rgba(0, 0, 0, 0)");
        wrapper.getChildren().add(stackPane);
        wrapper.setFillWidth(true);
        wrapper.setSpacing(20);

        scene = new Scene(wrapper);
        scene.setFill(Paint.valueOf("rgba(0,0,0,0)"));
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        stage.setFullScreen(false);
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getIcons().addAll(icons);

        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        stage.setOnCloseRequest(Event::consume);
        stage.setScene(scene);

        stage.show();
        stage.centerOnScreen();
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        stage.setY((dimension.getHeight() - stage.getHeight()) / 2);
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_START) {

        }
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification preloaderNotification) {
        if (preloaderNotification instanceof LoadingNotification notification) {
            progressBar.setProgress((double) progress++ / MAXIMUM_PROGRESS);
            progressLabel.setText(notification.message());
        } else if (preloaderNotification instanceof CloseNotification) {
            stage.close();
        }
    }

    @Override
    public boolean handleErrorNotification(ErrorNotification errorNotification) {
        logger.severe(ExceptionUtils.getStackTrace(errorNotification.getCause()));
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error while starting JArmEmu");
        alert.setHeaderText("The application encountered a error while starting");
        alert.setContentText("JArmEmu encountered an error while starting and is unable to continue its starting process.\n\n" +
                "You can contact the development team on Discord. For more informations, visit https://dwightstudio.fr.");

        TextArea textArea = new TextArea(ExceptionUtils.getStackTrace(errorNotification.getCause()));
        textArea.setEditable(false);
        textArea.setWrapText(false);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        var content = new GridPane();
        content.setMaxWidth(Double.MAX_VALUE);
        content.add(new Label("Full stacktrace:"), 0, 0);
        content.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(content);
        alert.initOwner(scene.getWindow());
        alert.showAndWait();
        return true;
    }

    public static @NotNull URL getResource(String name) {
        return Objects.requireNonNull(JArmEmuPreloader.class.getResource("/fr/dwightstudio/jarmemu/launcher/" + name));
    }

    public static @NotNull InputStream getResourceAsStream(String name) {
        return Objects.requireNonNull(JArmEmuPreloader.class.getResourceAsStream("/fr/dwightstudio/jarmemu/launcher/" + name));
    }

    public static @NotNull URL getMedia(String name) {
        return Objects.requireNonNull(JArmEmuApplication.class.getResource("/fr/dwightstudio/jarmemu/medias/" + name));
    }

    public static @NotNull InputStream getMediaAsStream(String name) {
        return Objects.requireNonNull(JArmEmuApplication.class.getResourceAsStream("/fr/dwightstudio/jarmemu/medias/" + name));
    }
}
