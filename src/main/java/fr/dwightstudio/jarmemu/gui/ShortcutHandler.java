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

import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;

public class ShortcutHandler extends AbstractJArmEmuModule {
    public ShortcutHandler(JArmEmuApplication application) {
        super(application);
    }

    public void handle(KeyEvent event) {
        try {
            if (event.isShortcutDown()) {
                if (event.isShiftDown()) {
                    switch (event.getCode()) {
                        case S -> getMainMenuController().onSaveAll();
                        case R -> getMainMenuController().onReloadAll();
                    }
                } else {
                    switch (event.getCode()) {
                        case S -> getMainMenuController().onSave();
                        case O -> getMainMenuController().onOpen();
                        case R -> getMainMenuController().onReload();
                        case N -> getMainMenuController().onNewFile();
                        case B -> getEditorController().currentFileEditor().getContextMenu().onToggleBreakpoint(new ActionEvent());
                        case F -> getEditorController().currentFileEditor().toggleFindAndReplace();
                    }
                }
            } else {
                switch (event.getCode()) {
                    case F2 -> getSimulationMenuController().onSimulate();
                    case F3 -> getSimulationMenuController().onStepInto();
                    case F4 -> getSimulationMenuController().onStepOver();
                    case F5 -> getSimulationMenuController().onContinue();
                    case F6 -> getSimulationMenuController().onPause();
                    case F7 -> getSimulationMenuController().onStop();
                    case F8 -> getSimulationMenuController().onRestart();
                }
            }
        } catch (IndexOutOfBoundsException | NullPointerException ignored) {}
    }
}