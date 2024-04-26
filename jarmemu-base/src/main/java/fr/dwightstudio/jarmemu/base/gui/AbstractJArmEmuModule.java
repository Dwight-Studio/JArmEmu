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

import fr.dwightstudio.jarmemu.base.asm.parser.SourceParser;
import fr.dwightstudio.jarmemu.base.gui.controllers.*;
import fr.dwightstudio.jarmemu.base.sim.CodeInterpreter;
import fr.dwightstudio.jarmemu.base.sim.ExecutionWorker;
import fr.dwightstudio.jarmemu.base.gui.controllers.*;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class AbstractJArmEmuModule implements Initializable {

    protected final JArmEmuApplication application;

    public AbstractJArmEmuModule(JArmEmuApplication application) {
        this.application = application;
    }

    protected JArmEmuApplication getApplication() {
        return application;
    }

    protected JArmEmuController getController() {
        return application.getController();
    }

    protected MainMenuController getMainMenuController() {
        return application.getMainMenuController();
    }

    protected MemoryDetailsController getMemoryDetailsController() {
        return application.getMemoryDetailsController();
    }
    protected MemoryOverviewController getMemoryOverviewController() {
        return application.getMemoryOverviewController();
    }

    protected RegistersController getRegistersController() {
        return application.getRegistersController();
    }

    protected SettingsController getSettingsController() {
        return application.getSettingsController();
    }

    protected StackController getStackController() {
        return application.getStackController();
    }

    protected SymbolsController getSymbolsController() {
        return application.getSymbolsController();
    }
    protected LabelsController getLabelsController() {
        return application.getLabelsController();
    }

    protected SourceParser getSourceParser() {
        return application.getSourceParser();
    }

    protected CodeInterpreter getCodeInterpreter() {
        return application.getCodeInterpreter();
    }

    protected ExecutionWorker getExecutionWorker() {
        return application.getExecutionWorker();
    }

    protected EditorController getEditorController() {
        return application.getEditorController();
    }

    protected SimulationMenuController getSimulationMenuController() {
        return application.getSimulationMenuController();
    }

    protected JArmEmuDialogs getDialogs() {
        return application.getDialogs();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
