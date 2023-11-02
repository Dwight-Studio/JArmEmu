package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.CodeInterpreter;
import fr.dwightstudio.jarmemu.sim.ExecutionWorker;
import fr.dwightstudio.jarmemu.sim.parse.SourceParser;
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

    protected MemoryController getMemoryController() {
        return application.getMemoryController();
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
