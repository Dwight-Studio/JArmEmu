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

package fr.dwightstudio.jarmemu.gui;

import javafx.application.Preloader;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.logging.Logger;

public class JArmEmuPreloader extends Preloader {
    public static Logger logger = Logger.getLogger(JArmEmuPreloader.class.getSimpleName());

    @Override
    public void start(Stage stage) {
        logger.info("Opening splash screen");

        stage.setFullScreen(false);
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);

        stage.getIcons().addAll(
                new Image(JArmEmuApplication.getResourceAsStream("medias/favicon@16.png")),
                new Image(JArmEmuApplication.getResourceAsStream("medias/favicon@32.png")),
                new Image(JArmEmuApplication.getResourceAsStream("medias/favicon@64.png")),
                new Image(JArmEmuApplication.getResourceAsStream("medias/favicon@128.png")),
                new Image(JArmEmuApplication.getResourceAsStream("medias/favicon@256.png")),
                new Image(JArmEmuApplication.getResourceAsStream("medias/favicon@512.png")),
                new Image(JArmEmuApplication.getResourceAsStream("medias/logo.png"))
        );

        stage.setOnCloseRequest(Event::consume);

        Image image = new Image(JArmEmuApplication.getResource("medias/splash.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        Pane wrapper = new Pane(imageView);

        stage.setScene(new Scene(wrapper));

        stage.show();
        stage.centerOnScreen();
    }
}
