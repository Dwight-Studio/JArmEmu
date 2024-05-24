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

package fr.dwightstudio.jarmemu.base.gui.controllers;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.ModalPane;
import atlantafx.base.controls.ToggleSwitch;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.ModalDialog;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class JArmEmuController implements Initializable {

    public static final String WINDOW_SIZE_KEY = "windowSize";
    public static final String SPLIT_PANES_KEY = "splitPanes";
    public static final String MAIN_SPLIT_PANE_KEY = "mainSplitPane";
    public static final String LEFT_SPLIT_PANE_KEY = "leftSplitPane";
    public static final String MEMORY_COLUMNS_KEY = "memoryColumns";
    public static final String MEMORY_DETAILS_KEY = "memoryDetails";
    public static final String MEMORY_OVERVIEW_KEY = "memoryOverview";

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    private final Timeline LAYOUT_SAVING_TIMELINE = new Timeline(
            new KeyFrame(Duration.millis(1000), event -> saveLayout())
    );


    @FXML protected StackPane mainPane;
    @FXML protected SplitPane mainSplitPane;
    @FXML protected SplitPane leftSplitPane;
    protected ModalPane modalPaneBack;
    protected ModalPane modalPaneMiddle;
    protected ModalPane modalPaneFront;

    @FXML protected TabPane filesTabPane;
    @FXML protected VBox notifications;
    @FXML protected Button toolSimulate;
    @FXML protected Button toolStepInto;
    @FXML protected Button toolStepOver;
    @FXML protected Button toolContinue;
    @FXML protected Button toolPause;
    @FXML protected Button toolStop;
    @FXML protected Button toolRestart;

    @FXML protected MenuItem findAndReplace;

    @FXML protected MenuItem menuSimulate;
    @FXML protected MenuItem menuStepInto;
    @FXML protected MenuItem menuStepOver;
    @FXML protected MenuItem menuContinue;
    @FXML protected MenuItem menuPause;
    @FXML protected MenuItem menuStop;
    @FXML protected MenuItem menuRestart;

    @FXML protected AnchorPane registersPane;

    @FXML protected AnchorPane memoryDetailsPane;
    @FXML protected AnchorPane memoryDetailsAnchorPane;
    @FXML protected Menu memoryDetailsMenu;
    @FXML protected Pagination memoryDetailsPage;
    @FXML protected CustomTextField memoryDetailsAddressField;

    @FXML protected AnchorPane memoryOverviewPane;
    @FXML protected AnchorPane memoryOverviewAnchorPane;
    @FXML protected Menu memoryOverviewMenu;
    @FXML protected Pagination memoryOverviewPage;
    @FXML protected CustomTextField memoryOverviewAddressField;

    @FXML protected AnchorPane settingsPane;
    @FXML protected ToggleButton settingsSmart;
    @FXML protected ToggleButton settingsSimple;
    @FXML protected ToggleSwitch autoCompletionSwitch;
    @FXML protected Spinner<Integer> settingsSimInterval;
    @FXML protected ToggleButton settingsRegex;
    @FXML protected ToggleButton settingsLegacy;
    @FXML protected ToggleSwitch notImplementedSwitch;
    @FXML protected ToggleSwitch deprecatedSwitch;

    @FXML protected ToggleSwitch manualBreakSwitch;
    @FXML protected ToggleSwitch codeBreakSwitch;
    @FXML protected ToggleSwitch autoBreakSwitch;
    @FXML protected ToggleSwitch memoryAlignBreakSwitch;
    @FXML protected ToggleSwitch stackAlignBreakSwitch;
    @FXML protected ToggleSwitch programAlignBreakSwitch;
    @FXML protected ToggleSwitch functionNestingBreakSwitch;
    @FXML protected ToggleSwitch readOnlyWritingBreakSwitch;

    @FXML protected ToggleSwitch followSPSwitch;
    @FXML protected ToggleSwitch updateSwitch;
    @FXML protected Spinner<Integer> settingsStackAddress;
    @FXML protected Spinner<Integer> settingsSymbolsAddress;

    @FXML protected ChoiceBox<String> settingsFormat;
    @FXML protected ChoiceBox<String> settingsFamily;
    @FXML protected ToggleButton settingsDark;
    @FXML protected ToggleButton settingsLight;
    @FXML protected Spinner<Integer> settingsMaxNotification;

    @FXML protected AnchorPane stackPane;

    @FXML protected AnchorPane symbolsPane;

    @FXML protected AnchorPane labelsPane;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        modalPaneBack = new ModalPane();
        modalPaneMiddle = new ModalPane();
        modalPaneFront = new ModalPane();
        mainPane.getChildren().addAll(modalPaneBack, modalPaneMiddle, modalPaneFront);

        JArmEmuApplication.notifyPreloader("Initializing GUI");
        JArmEmuApplication.getEditorController().initialize(url, resourceBundle);
        JArmEmuApplication.getMemoryDetailsController().initialize(url, resourceBundle);
        JArmEmuApplication.getMemoryOverviewController().initialize(url, resourceBundle);
        JArmEmuApplication.getRegistersController().initialize(url, resourceBundle);
        JArmEmuApplication.getSettingsController().initialize(url, resourceBundle);
        JArmEmuApplication.getStackController().initialize(url, resourceBundle);
        JArmEmuApplication.getSymbolsController().initialize(url, resourceBundle);
        JArmEmuApplication.getLabelsController().initialize(url, resourceBundle);
        JArmEmuApplication.getAutocompletionController().initialize(url, resourceBundle);

        JArmEmuApplication.getInstance().newSourceParser();

        JArmEmuApplication.notifyPreloader("Opening last save");
        JArmEmuApplication.getMainMenuController().openLastSave();

        JArmEmuApplication.notifyPreloader("Attaching State Container controllers");
        JArmEmuApplication.getMemoryDetailsController().attach(JArmEmuApplication.getCodeInterpreter().getStateContainer());
        JArmEmuApplication.getMemoryOverviewController().attach(JArmEmuApplication.getCodeInterpreter().getStateContainer());
        JArmEmuApplication.getRegistersController().attach(JArmEmuApplication.getCodeInterpreter().getStateContainer());
        JArmEmuApplication.getLabelsController().attach(JArmEmuApplication.getCodeInterpreter().getStateContainer());
        JArmEmuApplication.getSymbolsController().attach(JArmEmuApplication.getCodeInterpreter().getStateContainer());

        JArmEmuApplication.notifyPreloader("Launching Execution Worker");
        JArmEmuApplication.getExecutionWorker().revive();
    }

    public void registerLayoutChangeListener() {
        logger.info("Initializing layout listeners");

        Platform.runLater(() -> {
            JArmEmuApplication.getInstance().maximizedProperty().addListener((obs, oldValue, newValue) -> {
                if (!newValue.equals(JArmEmuApplication.getSettingsController().getMaximized())) {
                    logger.info("Saving maximization");
                    JArmEmuApplication.getSettingsController().setMaximized(newValue);

                    Platform.runLater(() -> JArmEmuApplication.getController().applyLayout(JArmEmuApplication.getSettingsController().getLayout()));
                }
            });

            JArmEmuApplication.getStage().widthProperty().addListener(obs -> notifyLayoutChange());
            JArmEmuApplication.getStage().heightProperty().addListener(obs -> notifyLayoutChange());

            mainSplitPane.getDividers().forEach(divider -> divider.positionProperty().addListener(obs -> notifyLayoutChange()));
            leftSplitPane.getDividers().forEach(divider -> divider.positionProperty().addListener(obs -> notifyLayoutChange()));

            JArmEmuApplication.getMemoryDetailsController().memoryTable.getColumns().forEach(column -> column.visibleProperty().addListener(obs -> notifyLayoutChange()));
            JArmEmuApplication.getMemoryOverviewController().memoryTable.getColumns().forEach(column -> column.visibleProperty().addListener(obs -> notifyLayoutChange()));

            Platform.runLater(LAYOUT_SAVING_TIMELINE::stop);
        });
    }

    /**
     * Indique un changement de layout et enclenche une timeline de sauvegarde.
     */
    public void notifyLayoutChange() {
        if (!getLayoutJSON().equals(JArmEmuApplication.getSettingsController().getLayout())) {
            LAYOUT_SAVING_TIMELINE.stop();
            LAYOUT_SAVING_TIMELINE.play();
        }
    }

    /**
     * @return une chaîne de caractères contenant les données du layout au format JSON
     */
    public String getLayoutJSON() {
        HashMap<String, Object> layout = new HashMap<>();

        layout.put(WINDOW_SIZE_KEY, new JSONArray(new double[]{JArmEmuApplication.getStage().getWidth(), JArmEmuApplication.getStage().getHeight()}));

        HashMap<String, JSONArray> splitPanes = new HashMap<>();
        splitPanes.put(MAIN_SPLIT_PANE_KEY, new JSONArray(mainSplitPane.getDividerPositions()));
        splitPanes.put(LEFT_SPLIT_PANE_KEY, new JSONArray(leftSplitPane.getDividerPositions()));
        layout.put(SPLIT_PANES_KEY, splitPanes);

        HashMap<String, JSONArray> memoryColumns = new HashMap<>();
        memoryColumns.put(
                MEMORY_DETAILS_KEY,
                new JSONArray(JArmEmuApplication.getMemoryDetailsController().memoryTable.getColumns().stream().map(TableColumnBase::isVisible).toArray(Boolean[]::new))
        );
        memoryColumns.put(
                MEMORY_OVERVIEW_KEY,
                new JSONArray(JArmEmuApplication.getMemoryOverviewController().memoryTable.getColumns().stream().map(TableColumnBase::isVisible).toArray(Boolean[]::new))
        );
        layout.put(MEMORY_COLUMNS_KEY, memoryColumns);;

        return new JSONObject(layout).toString();
    }

    /**
     * Lit et applique le layout à partir d'une chaîne de caractères.
     *
     * @param json une chaîne de caractères contenant les données du layout au format JSON
     */
    public void applyLayout(String json) {
        logger.info("Applying layout");
        try {
            JSONObject layout = new JSONObject(json);

            if (!JArmEmuApplication.getStage().isMaximized()) {
                JSONArray windowSize = layout.getJSONArray(WINDOW_SIZE_KEY);
                JArmEmuApplication.getStage().setWidth(windowSize.getDouble(0));
                JArmEmuApplication.getStage().setHeight(windowSize.getDouble(1));
            }

            JSONObject splitPanes = layout.getJSONObject(SPLIT_PANES_KEY);

            JSONArray mainSplitPaneData = splitPanes.getJSONArray(MAIN_SPLIT_PANE_KEY);
            for (int i = 0; i < mainSplitPaneData.length(); i++) {
                mainSplitPane.setDividerPosition(i, mainSplitPaneData.getDouble(i));
            }

            JSONArray leftSplitPaneData = splitPanes.getJSONArray(LEFT_SPLIT_PANE_KEY);
            for (int i = 0; i < leftSplitPaneData.length(); i++) {
                leftSplitPane.setDividerPosition(i, leftSplitPaneData.getDouble(i));
            }

            JSONObject memoryColumns = layout.getJSONObject(MEMORY_COLUMNS_KEY);

            JSONArray memoryDetailsData = memoryColumns.getJSONArray(MEMORY_DETAILS_KEY);
            for (int i = 0; i < memoryDetailsData.length(); i++) {
                JArmEmuApplication.getMemoryDetailsController().memoryTable.getColumns().get(i).setVisible(memoryDetailsData.getBoolean(i));
            }

            JSONArray memoryOverviewData = memoryColumns.getJSONArray(MEMORY_OVERVIEW_KEY);
            for (int i = 0; i < memoryOverviewData.length(); i++) {
                JArmEmuApplication.getMemoryOverviewController().memoryTable.getColumns().get(i).setVisible(memoryOverviewData.getBoolean(i));
            }
        } catch (JSONException exception) {
            logger.severe("Error while parsing layout");
            logger.severe(ExceptionUtils.getStackTrace(exception));
        }

        Platform.runLater(LAYOUT_SAVING_TIMELINE::stop);
    }

    /**
     * Sauvegarde le layout actuel.
     */
    public void saveLayout() {
        if (!getLayoutJSON().equals(JArmEmuApplication.getSettingsController().getLayout())) {
            logger.info("Saving layout");
            JArmEmuApplication.getSettingsController().setLayout(getLayoutJSON());
        }
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
        JArmEmuApplication.getMainMenuController().onNewFile();
    }

    @FXML
    protected void onOpen() {
        JArmEmuApplication.getMainMenuController().onOpen();
    }
    @FXML
    protected void onSaveAll() {
        JArmEmuApplication.getMainMenuController().onSaveAll();
    }

    @FXML
    protected void onSave() {
        JArmEmuApplication.getMainMenuController().onSave();
    }

    @FXML
    protected void onSaveAs() {
        JArmEmuApplication.getMainMenuController().onSaveAs();
    }

    @FXML
    protected void onReloadAll() {
        JArmEmuApplication.getMainMenuController().onReloadAll();
    }

    @FXML
    protected void onReload() {
        JArmEmuApplication.getMainMenuController().onReload();
    }

    @FXML
    protected void onCloseAll() {
        JArmEmuApplication.getMainMenuController().onCloseAll();
    }

    @FXML
    protected void onClose() {
        JArmEmuApplication.getMainMenuController().onClose();
    }

    @FXML
    protected void onExit() {
        JArmEmuApplication.getMainMenuController().onExit();
    }

    @FXML
    protected void onSimulate() {
        JArmEmuApplication.getSimulationMenuController().onSimulate();
    }

    @FXML
    protected void onStepInto() {
        JArmEmuApplication.getSimulationMenuController().onStepInto();
    }

    @FXML
    protected void onStepOver() {
        JArmEmuApplication.getSimulationMenuController().onStepOver();
    }

    @FXML
    protected void onContinue() {
        JArmEmuApplication.getSimulationMenuController().onContinue();
    }

    @FXML
    protected void onPause() {
        JArmEmuApplication.getSimulationMenuController().onPause();
    }

    @FXML
    protected void onStop() {
        JArmEmuApplication.getSimulationMenuController().onStop();
    }

    @FXML
    protected void onRestart() {
        JArmEmuApplication.getSimulationMenuController().onRestart();
    }

    @FXML
    protected void onClearNotifs() {
        JArmEmuApplication.getEditorController().clearNotifications();
    }

    @FXML
    protected void onAbout() {JArmEmuApplication.getMainMenuController().onAbout();}

    @FXML
    public void onSettingsSmart() {
        JArmEmuApplication.getSettingsController().onSettingsSmart();
    }

    @FXML
    public void onSettingsSimple() {
        JArmEmuApplication.getSettingsController().onSettingsSimple();
    }

    @FXML
    public void onSettingsRegex() {
        JArmEmuApplication.getSettingsController().onSettingsRegex();
    }

    @FXML
    public void onSettingsLegacy() {
        JArmEmuApplication.getSettingsController().onSettingsLegacy();
    }

    @FXML
    public void onResetSettings() {
        JArmEmuApplication.getMainMenuController().onResetSettings();
    }

    @FXML
    public void onSettingsDark() {
        JArmEmuApplication.getSettingsController().onSettingsDark();
    }

    @FXML
    public void onSettingsLight() {
        JArmEmuApplication.getSettingsController().onSettingsLight();
    }

    @FXML void onCopy() {
        JArmEmuApplication.getEditorController().currentFileEditor().getContextMenu().onCopy(new ActionEvent());
    }

    @FXML void onCut() {
        JArmEmuApplication.getEditorController().currentFileEditor().getContextMenu().onCut(new ActionEvent());
    }

    @FXML void onPaste() {
        JArmEmuApplication.getEditorController().currentFileEditor().getContextMenu().onPaste(new ActionEvent());
    }

    @FXML void onDelete() {
        JArmEmuApplication.getEditorController().currentFileEditor().getContextMenu().onDelete(new ActionEvent());
    }

    @FXML void onToggleBreakpoint() {
        JArmEmuApplication.getEditorController().currentFileEditor().getContextMenu().onToggleBreakpoint(new ActionEvent());
    }

    @FXML void onFindAndReplace() {
        JArmEmuApplication.getEditorController().currentFileEditor().toggleFindAndReplace();
    }
}