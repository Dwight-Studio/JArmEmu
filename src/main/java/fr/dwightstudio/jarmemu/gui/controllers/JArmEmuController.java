package fr.dwightstudio.jarmemu.gui.controllers;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.ModalPane;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.ModalDialog;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class JArmEmuController extends AbstractJArmEmuModule {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @FXML protected ModalPane modalPaneBack;
    @FXML protected ModalPane modalPaneMiddle;
    @FXML protected ModalPane modalPaneFront;

    @FXML protected StackPane editorStackPane;
    protected CodeArea codeArea;
    protected VirtualizedScrollPane<CodeArea> editorScroll;

    @FXML protected VBox notifications;
    @FXML protected Button simulate;
    @FXML protected Button stepInto;
    @FXML protected Button stepOver;
    @FXML protected Button conti;
    @FXML protected Button pause;
    @FXML protected Button stop;
    @FXML protected Button restart;

    @FXML protected Tab RegistersTab;
    protected TableView<Register> registersTable;

    @FXML protected Tab memoryTab;
    @FXML protected GridPane memoryGrid;
    @FXML protected ScrollPane memoryScroll;
    @FXML protected ScrollBar memoryScrollBar;
    @FXML protected AnchorPane memoryPane;
    @FXML protected Pagination memoryPage;
    @FXML protected CustomTextField addressField;

    @FXML protected Tab settingsTab;
    @FXML protected Spinner<Integer> settingsSimInterval;
    @FXML protected ToggleButton settingsRegex;
    @FXML protected ToggleButton settingsLegacy;
    @FXML protected Spinner<Integer> settingsStackAddress;
    @FXML protected Spinner<Integer> settingsSymbolsAddress;
    @FXML protected ChoiceBox<String> settingsFormat;
    @FXML protected ChoiceBox<String> settingsFamily;
    @FXML protected ToggleButton settingsDark;
    @FXML protected ToggleButton settingsLight;

    @FXML protected Tab stackTab;
    @FXML protected GridPane stackGrid;
    @FXML protected ScrollPane stackScroll;


    public JArmEmuController(JArmEmuApplication application) {
        super(application);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        codeArea = new CodeArea();
        editorScroll = new VirtualizedScrollPane<>(codeArea);
        editorStackPane.getChildren().addFirst(editorScroll);

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

    public void openDialog(ModalDialog dialog) {
        modalPaneFront.show(dialog.getModalBox());
    }

    public void closeDialog() {
        modalPaneFront.hide();
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

    @FXML
    public void onSettingsDark() {
        getSettingsController().onSettingsDark();
    }

    @FXML
    public void onSettingsLight() {
        getSettingsController().onSettingsLight();
    }

    @FXML void onCopy() {
        getEditorController().getContextMenu().onCopy(new ActionEvent());
    }

    @FXML void onCut() {
        getEditorController().getContextMenu().onCut(new ActionEvent());
    }

    @FXML void onPaste() {
        getEditorController().getContextMenu().onPaste(new ActionEvent());
    }

    @FXML void onDelete() {
        getEditorController().getContextMenu().onDelete(new ActionEvent());
    }
}