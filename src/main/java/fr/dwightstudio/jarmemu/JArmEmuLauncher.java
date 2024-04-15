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

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class JArmEmuLauncher {

    private static final int BASE_DPI = 96;
    public static final SplashScreen splashScreen = SplashScreen.getSplashScreen();

    private static Logger logger = Logger.getLogger(JArmEmuLauncher.class.getSimpleName());

    public static void main(String[] args) throws IOException {
        System.setProperty("prism.dirtyopts", "false");
        setUpLogger();

        if (splashScreen != null) {
            int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
            int scale = (dpi * 100) / BASE_DPI;
            logger.info("Screen resolution of " + dpi + " DPI");
            logger.info("Adapting SplashScreen to current screen scale (" + scale + "%)");

            URL url;

            if (scale >= 125 && scale < 150) {
                url = JArmEmuApplication.getResource("medias/splash@125pct.png");
            } else if (scale >= 150 && scale < 200) {
                url = JArmEmuApplication.getResource("medias/splash@150pct.png");
            } else if (scale >= 200 && scale < 250) {
                url = JArmEmuApplication.getResource("medias/splash@200pct.png");
            } else if (scale >= 250 && scale < 300) {
                url = JArmEmuApplication.getResource("medias/splash@250pct.png");
            } else if (scale >= 300) {
                url = JArmEmuApplication.getResource("jarmemu/medias/splash@300pct.png");
            } else {
                url = JArmEmuApplication.getResource("medias/splash.png");
            }

            logger.info("Loading SplashScreen: " + url);
            splashScreen.setImageURL(url);
        } else {
            logger.info("No SplashScreen detected");
        }

        JArmEmuApplication.main(args);
    }

    public static void setUpLogger() throws IOException {
        LogManager.getLogManager().readConfiguration(JArmEmuApplication.getResourceAsStream("logging.properties"));
    }
}
