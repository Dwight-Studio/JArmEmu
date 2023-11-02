package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.JArmEmuApplication;
import fr.dwightstudio.jarmemu.Status;
import fr.dwightstudio.jarmemu.asm.args.ArgumentParsers;
import fr.dwightstudio.jarmemu.gui.LineStatus;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.util.MathUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.controlsfx.dialog.ExceptionDialog;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class JArmEmuController implements Initializable {

    private final JArmEmuApplication application;

    private final Logger logger = Logger.getLogger(getClass().getName());

    @FXML
    protected CodeArea codeArea;

    @FXML
    protected VBox notifications;
    @FXML protected Button simulate;
    @FXML protected Button stepInto;
    @FXML protected Button stepOver;
    @FXML protected Button conti;
    @FXML protected Button pause;
    @FXML protected Button stop;
    @FXML protected Button restart;
    @FXML protected Button reset;

    @FXML protected Text R0;
    @FXML protected Text R1;
    @FXML protected Text R2;
    @FXML protected Text R3;
    @FXML protected Text R4;
    @FXML protected Text R5;
    @FXML protected Text R6;
    @FXML protected Text R7;
    @FXML protected Text R8;
    @FXML protected Text R9;
    @FXML protected Text R10;
    @FXML protected Text R11;
    @FXML protected Text R12;
    @FXML protected Text R13;
    @FXML protected Text R14;
    @FXML protected Text R15;
    @FXML protected Text CPSR;
    @FXML protected Text CPSRT;
    @FXML protected Text SPSR;
    @FXML protected Text SPSRT;
    @FXML protected GridPane memoryGrid;
    @FXML protected ScrollPane memoryScroll;
    @FXML protected ScrollBar memoryScrollBar;
    @FXML protected AnchorPane memoryPane;
    @FXML protected Pagination memoryPage;
    @FXML protected TextField addressField;
    protected ArrayList<Text> stack;

    public JArmEmuController(JArmEmuApplication application) {
        this.application = application;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        application.getEditorController().initialize(url, resourceBundle);
        application.getMainMenuController().initialize(url, resourceBundle);
        application.getMemoryController().initialize(url, resourceBundle);
        application.getRegistersController().initialize(url, resourceBundle);
        application.getSettingsController().initialize(url, resourceBundle);
        application.getSimulationMenuController().initialize(url, resourceBundle);
        application.getStackController().initialize(url, resourceBundle);

        stack = new ArrayList<>();

        onNewFile();

        application.getExecutionWorker().revive();
        application.getExecutionWorker().updateGUI();
    }

    @FXML
    protected void onNewFile() {
        application.getMainMenuController().onNewFile();
    }

    @FXML
    protected void onOpen() {
        application.getMainMenuController().onOpen();
    }

    @FXML
    protected void onSave() {
        application.getMainMenuController().onSave();
    }

    @FXML
    protected void onSaveAs() {
        application.getMainMenuController().onSaveAs();
    }

    @FXML
    protected void onReload() {
        application.getMainMenuController().onReload();
    }

    @FXML
    protected void onExit() {
        application.getMainMenuController().onExit();
    }

    @FXML
    protected void onSimulate() {
        application.getSimulationMenuController().onSimulate();
    }

    @FXML
    protected void onStepInto() {
        application.getSimulationMenuController().onStepInto();
    }

    @FXML
    protected void onStepOver() {
        application.getSimulationMenuController().onStepOver();
    }

    @FXML
    protected void onContinue() {
        application.getSimulationMenuController().onContinue();
    }

    @FXML
    protected void onPause() {
        application.getSimulationMenuController().onPause();
    }

    @FXML
    protected void onStop() {
        application.getSimulationMenuController().onStop();
    }

    @FXML
    protected void onRestart() {
        application.getSimulationMenuController().onRestart();
    }

    @FXML
    protected void onReset() {
        application.getSimulationMenuController().onReset();
    }

    @FXML
    protected void onRegexParser() {

    }

    @FXML
    protected void onLegacyParser() {

    }
}