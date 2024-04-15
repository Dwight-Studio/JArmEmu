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

package fr.dwightstudio.jarmemu;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class JArmEmuLauncher {

    private static Logger logger = Logger.getLogger(JArmEmuLauncher.class.getSimpleName());

    public static void main(String[] args) throws IOException {
        System.setProperty("prism.dirtyopts", "false");
        setUpLogger();

        if (SplashScreen.getSplashScreen() != null) SplashScreen.getSplashScreen().close();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        JFrame splashScreen = new JFrame("JArmEmu SplashScreen");

        double scale = splashScreen.getGraphicsConfiguration().getDefaultTransform().getScaleY();

        splashScreen.setBounds((screenSize.width - 100) / 2, (screenSize.height - 200) / 2, 100, 200);

        splashScreen.setSize(100,200);
        splashScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        splashScreen.setAlwaysOnTop(true);
        splashScreen.setUndecorated(true);
        splashScreen.setAutoRequestFocus(true);
        splashScreen.setVisible(true);

        Graphics g = splashScreen.getContentPane().getGraphics();
        g.drawImage(ImageIO.read(JArmEmuApplication.getResource("medias/splash@300pct.png")), 0, 0, null);

        logger.info("Adapting SplashScreen to current screen scale (" + scale + "%)");

        /*
        logger.info("Adapting SplashScreen to current screen scale (" + scale + "%)");

        URL url;

        if (scale >= 1.25 && scale < 1.5) {
            url = JArmEmuApplication.getResource("medias/splash@125pct.png");
        } else if (scale >= 1.5 && scale < 2.0) {
            url = JArmEmuApplication.getResource("medias/splash@150pct.png");
        } else if (scale >= 2.0 && scale < 2.5) {
            url = JArmEmuApplication.getResource("medias/splash@200pct.png");
        } else if (scale >= 2.5 && scale < 3.0) {
            url = JArmEmuApplication.getResource("medias/splash@250pct.png");
        } else if (scale >= 3.0) {
            url = JArmEmuApplication.getResource("medias/splash@300pct.png");
        } else {
            url = JArmEmuApplication.getResource("medias/splash.png");
        }

        logger.info("Loading SplashScreen: " + url);
         */

        JArmEmuApplication.main(args);
    }

    public static void setUpLogger() throws IOException {
        LogManager.getLogManager().readConfiguration(JArmEmuApplication.getResourceAsStream("logging.properties"));
    }
}
