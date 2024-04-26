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

package fr.dwightstudio.jarmemu.base.gui;

import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;

public class ShortcutHandler {

    public void handle(KeyEvent event) {
        try {
            if (event.isShortcutDown()) {
                if (event.isShiftDown()) {
                    switch (event.getCode()) {
                        case S -> JArmEmuApplication.getMainMenuController().onSaveAll();
                        case R -> JArmEmuApplication.getMainMenuController().onReloadAll();
                    }
                } else {
                    switch (event.getCode()) {
                        case S -> JArmEmuApplication.getMainMenuController().onSave();
                        case O -> JArmEmuApplication.getMainMenuController().onOpen();
                        case R -> JArmEmuApplication.getMainMenuController().onReload();
                        case N -> JArmEmuApplication.getMainMenuController().onNewFile();
                        case B -> JArmEmuApplication.getEditorController().currentFileEditor().getContextMenu().onToggleBreakpoint(new ActionEvent());
                        case F -> JArmEmuApplication.getEditorController().currentFileEditor().toggleFindAndReplace();
                    }
                }
            } else {
                switch (event.getCode()) {
                    case F2 -> JArmEmuApplication.getSimulationMenuController().onSimulate();
                    case F3 -> JArmEmuApplication.getSimulationMenuController().onStepInto();
                    case F4 -> JArmEmuApplication.getSimulationMenuController().onStepOver();
                    case F5 -> JArmEmuApplication.getSimulationMenuController().onContinue();
                    case F6 -> JArmEmuApplication.getSimulationMenuController().onPause();
                    case F7 -> JArmEmuApplication.getSimulationMenuController().onStop();
                    case F8 -> JArmEmuApplication.getSimulationMenuController().onRestart();
                }
            }
        } catch (IndexOutOfBoundsException | NullPointerException ignored) {}
    }
}