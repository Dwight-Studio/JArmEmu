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

import com.sun.javafx.application.LauncherImpl;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class JArmEmuLauncher {

    private static Logger logger = Logger.getLogger(JArmEmuLauncher.class.getSimpleName());

    public static void main(String[] args) throws IOException {
        System.setProperty("prism.dirtyopts", "false");
        LogManager.getLogManager().readConfiguration(JArmEmuPreloader.getResourceAsStream("logging.properties"));

        LauncherImpl.launchApplication(JArmEmuApplication.class, JArmEmuPreloader.class, args);
    }
}
