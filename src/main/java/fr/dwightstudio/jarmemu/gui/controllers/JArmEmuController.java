/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2023 Dwight Studio
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

package fr.dwightstudio.jarmemu.gui.controllers;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.ModalPane;
import atlantafx.base.controls.ToggleSwitch;
import fr.dwightstudio.jarmemu.gui.AbstractJArmEmuModule;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.ModalDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class JArmEmuController extends AbstractJArmEmuModule {

    private final Logger logger = Logger.getLogger(getClass().getName());

    @FXML protected StackPane mainPane;
    protected ModalPane modalPaneBack;
    protected ModalPane modalPaneMiddle;
    protected ModalPane modalPaneFront;

    @FXML protected StackPane editorStackPane;
    @FXML protected TabPane filesTabPane;
    @FXML protected VBox notifications;
    @FXML protected Button simulate;
    @FXML protected Button stepInto;
    @FXML protected Button stepOver;
    @FXML protected Button conti;
    @FXML protected Button pause;
    @FXML protected Button stop;
    @FXML protected Button restart;

    @FXML protected Tab registersTab;

    @FXML protected TabPane memorySettingsTab;

    @FXML protected Tab memoryTab;
    @FXML protected Menu memoryMenu;
    @FXML protected AnchorPane memoryAnchorPane;
    @FXML protected Pagination memoryPage;
    @FXML protected CustomTextField addressField;

    @FXML protected Tab settingsTab;
    @FXML protected Spinner<Integer> settingsSimInterval;
    @FXML protected ToggleButton settingsRegex;
    @FXML protected ToggleButton settingsLegacy;
    @FXML protected ToggleSwitch autoBreakSwitch;
    @FXML protected ToggleSwitch memoryAlignBreakSwitch;
    @FXML protected ToggleSwitch stackAlignBreakSwitch;
    @FXML protected ToggleSwitch programAlignBreakSwitch;
    @FXML protected ToggleSwitch functionNestingBreakSwitch;
    @FXML protected ToggleSwitch readOnlyWritingBreakSwitch;
    @FXML protected ToggleSwitch followSPSwitch;
    @FXML protected Spinner<Integer> settingsStackAddress;
    @FXML protected Spinner<Integer> settingsSymbolsAddress;
    @FXML protected ChoiceBox<String> settingsFormat;
    @FXML protected ChoiceBox<String> settingsFamily;
    @FXML protected ToggleButton settingsDark;
    @FXML protected ToggleButton settingsLight;

    @FXML protected Tab stackTab;


    public JArmEmuController(JArmEmuApplication application) {
        super(application);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        modalPaneBack = new ModalPane();
        modalPaneMiddle = new ModalPane();
        modalPaneFront = new ModalPane();
        mainPane.getChildren().addAll(modalPaneBack, modalPaneMiddle, modalPaneFront);

        getEditorController().initialize(url, resourceBundle);
        getMainMenuController().initialize(url, resourceBundle);
        getMemoryController().initialize(url, resourceBundle);
        getRegistersController().initialize(url, resourceBundle);
        getSettingsController().initialize(url, resourceBundle);
        getSimulationMenuController().initialize(url, resourceBundle);
        getStackController().initialize(url, resourceBundle);

        application.newSourceParser();

        getMainMenuController().openLastSave();

        getMemoryController().attach(getCodeInterpreter().getStateContainer());
        getRegistersController().attach(getCodeInterpreter().getStateContainer());

        getExecutionWorker().revive();
    }

    public void openDialogFront(ModalDialog dialog) {
        modalPaneFront.show(dialog.getModalBox());
    }

    public void openDialogMiddle(ModalDialog dialog) {
        modalPaneMiddle.show(dialog.getModalBox());
    }

    public void openDialogBack(ModalDialog dialog) {
        modalPaneBack.show(dialog.getModalBox());
    }

    public void closeDialogFront() {
        modalPaneFront.hide();
    }

    public void closeDialogMiddle() {
        modalPaneMiddle.hide();
    }

    public void closeDialogBack() {
        modalPaneBack.hide();
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
    protected void onAbout() {getMainMenuController().onAbout();}

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
        getEditorController().currentFileEditor().getContextMenu().onCopy(new ActionEvent());
    }

    @FXML void onCut() {
        getEditorController().currentFileEditor().getContextMenu().onCut(new ActionEvent());
    }

    @FXML void onPaste() {
        getEditorController().currentFileEditor().getContextMenu().onPaste(new ActionEvent());
    }

    @FXML void onDelete() {
        getEditorController().currentFileEditor().getContextMenu().onDelete(new ActionEvent());
    }
}