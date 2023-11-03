package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fxmisc.richtext.CodeArea;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class JArmEmuController extends AbstractJArmEmuModule {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @FXML protected CodeArea codeArea;

    @FXML protected VBox notifications;
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

    @FXML protected Tab memoryTab;
    @FXML protected GridPane memoryGrid;
    @FXML protected ScrollPane memoryScroll;
    @FXML protected ScrollBar memoryScrollBar;
    @FXML protected AnchorPane memoryPane;
    @FXML protected Pagination memoryPage;
    @FXML protected TextField addressField;

    @FXML protected Tab settingsTab;
    @FXML protected Spinner<Integer> settingsSimInterval;
    @FXML protected SplitMenuButton settingsParser;
    @FXML protected Spinner<Integer> settingsStackAddress;
    @FXML protected Spinner<Integer> settingsSymbolsAddress;


    public JArmEmuController(JArmEmuApplication application) {
        super(application);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        getEditorController().initialize(url, resourceBundle);
        getMainMenuController().initialize(url, resourceBundle);
        getMemoryController().initialize(url, resourceBundle);
        getRegistersController().initialize(url, resourceBundle);
        getSettingsController().initialize(url, resourceBundle);
        getSimulationMenuController().initialize(url, resourceBundle);
        getStackController().initialize(url, resourceBundle);

        application.newSourceParser();

        getMainMenuController().openLastSave();

        getExecutionWorker().revive();
        getExecutionWorker().updateGUI();
    }

    @FXML
    protected void onNewFile() {
        getMainMenuController().onNewFile();
    }

    @FXML
    protected void onOpen() {
        getMainMenuController().onOpen();
    }

    @FXML
    protected void onSave() {
        getMainMenuController().onSave();
    }

    @FXML
    protected void onSaveAs() {
        getMainMenuController().onSaveAs();
    }

    @FXML
    protected void onReload() {
        getMainMenuController().onReload();
    }

    @FXML
    protected void onExit() {
        getMainMenuController().onExit();
    }

    @FXML
    protected void onSimulate() {
        getSimulationMenuController().onSimulate();
    }

    @FXML
    protected void onStepInto() {
        getSimulationMenuController().onStepInto();
    }

    @FXML
    protected void onStepOver() {
        getSimulationMenuController().onStepOver();
    }

    @FXML
    protected void onContinue() {
        getSimulationMenuController().onContinue();
    }

    @FXML
    protected void onPause() {
        getSimulationMenuController().onPause();
    }

    @FXML
    protected void onStop() {
        getSimulationMenuController().onStop();
    }

    @FXML
    protected void onRestart() {
        getSimulationMenuController().onRestart();
    }

    @FXML
    protected void onReset() {
        getSimulationMenuController().onReset();
    }

    @FXML
    protected void onClearNotifs() {
        getEditorController().clearNotifs();
    }

    @FXML
    public void onSettingsRegex() {
        getSettingsController().onSettingsRegex();
    }

    @FXML
    public void onSettingsLegacy() {
        getSettingsController().onSettingsLegacy();
    }

    @FXML
    public void onResetSettings() {
        getMainMenuController().onResetSettings();
    }
}