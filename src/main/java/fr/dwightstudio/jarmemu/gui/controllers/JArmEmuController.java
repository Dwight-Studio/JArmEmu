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

package fr.dwightstudio.jarmemu.gui.controllers;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.ModalPane;
import atlantafx.base.controls.ToggleSwitch;
import fr.dwightstudio.jarmemu.gui.AbstractJArmEmuModule;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.ModalDialog;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

public class JArmEmuController extends AbstractJArmEmuModule {

    public static final String MAXIMIZED_KEY = "maximized";
    public static final String SPLIT_PANES_KEY = "splitPanes";
    public static final String MAIN_SPLIT_PANE_KEY = "mainSplitPane";
    public static final String LEFT_SPLIT_PANE_KEY = "leftSplitPane";
    public static final String MEMORY_COLUMNS_KEY = "memoryColumns";
    public static final String MEMORY_DETAILS_KEY = "memoryDetails";
    public static final String MEMORY_OVERVIEW_KEY = "memoryOverview";

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    private final Timeline LAYOUT_INIT_TIMELINE = new Timeline(
            new KeyFrame(Duration.ZERO, event -> applyLayout(getSettingsController().getLayout())),
            new KeyFrame(Duration.millis(500), event -> registerLayoutChangeListener())
    );
    private final Timeline LAYOUT_SAVING_TIMELINE = new Timeline(
            new KeyFrame(Duration.millis(2000), event -> saveLayout())
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
    @FXML protected Spinner<Integer> settingsSimInterval;
    @FXML protected ToggleButton settingsRegex;
    @FXML protected ToggleButton settingsLegacy;
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
        getMemoryDetailsController().initialize(url, resourceBundle);
        getMemoryOverviewController().initialize(url, resourceBundle);
        getRegistersController().initialize(url, resourceBundle);
        getSettingsController().initialize(url, resourceBundle);
        getSimulationMenuController().initialize(url, resourceBundle);
        getStackController().initialize(url, resourceBundle);
        getSymbolsController().initialize(url, resourceBundle);
        getLabelsController().initialize(url, resourceBundle);

        application.newSourceParser();

        getMainMenuController().openLastSave();

        getMemoryDetailsController().attach(getCodeInterpreter().getStateContainer());
        getMemoryOverviewController().attach(getCodeInterpreter().getStateContainer());
        getRegistersController().attach(getCodeInterpreter().getStateContainer());
        getLabelsController().attach(getCodeInterpreter().getStateContainer());
        getSymbolsController().attach(getCodeInterpreter().getStateContainer());

        getExecutionWorker().revive();
    }

    public void registerLayoutChangeListener() {
        logger.info("Initializing layout listeners");
        applyLayout(getSettingsController().getLayout());

        Platform.runLater(() -> {
            getApplication().maximizedProperty().addListener(obs -> notifyLayoutChange());

            mainSplitPane.getDividers().forEach(divider -> divider.positionProperty().addListener(obs -> notifyLayoutChange()));
            leftSplitPane.getDividers().forEach(divider -> divider.positionProperty().addListener(obs -> notifyLayoutChange()));

            getMemoryDetailsController().memoryTable.getColumns().forEach(column -> column.visibleProperty().addListener(obs -> notifyLayoutChange()));
            getMemoryOverviewController().memoryTable.getColumns().forEach(column -> column.visibleProperty().addListener(obs -> notifyLayoutChange()));

            Platform.runLater(LAYOUT_SAVING_TIMELINE::stop);
        });
    }

    /**
     * Indique un changement de layout et enclenche une timeline de sauvegarde.
     */
    public void notifyLayoutChange() {
        if (!getLayoutJSON().equals(getSettingsController().getLayout())) {
            LAYOUT_SAVING_TIMELINE.stop();
            LAYOUT_SAVING_TIMELINE.play();
        }
    }

    /**
     * @return une chaîne de caractères contenant les données du layout au format JSON
     */
    public String getLayoutJSON() {
        HashMap<String, Object> layout = new HashMap<>();

        layout.put(MAXIMIZED_KEY, getApplication().isMaximized());

        HashMap<String, JSONArray> splitPanes = new HashMap<>();
        splitPanes.put(MAIN_SPLIT_PANE_KEY, new JSONArray(mainSplitPane.getDividerPositions()));
        splitPanes.put(LEFT_SPLIT_PANE_KEY, new JSONArray(leftSplitPane.getDividerPositions()));
        layout.put(SPLIT_PANES_KEY, splitPanes);

        HashMap<String, JSONArray> memoryColumns = new HashMap<>();
        memoryColumns.put(
                MEMORY_DETAILS_KEY,
                new JSONArray(getMemoryDetailsController().memoryTable.getColumns().stream().map(TableColumnBase::isVisible).toArray(Boolean[]::new))
        );
        memoryColumns.put(
                MEMORY_OVERVIEW_KEY,
                new JSONArray(getMemoryOverviewController().memoryTable.getColumns().stream().map(TableColumnBase::isVisible).toArray(Boolean[]::new))
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

            boolean maximized = layout.getBoolean(MAXIMIZED_KEY);

            if (maximized != getApplication().isMaximized()) {
                getApplication().setMaximized(maximized);
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
                getMemoryDetailsController().memoryTable.getColumns().get(i).setVisible(memoryDetailsData.getBoolean(i));
            }

            JSONArray memoryOverviewData = memoryColumns.getJSONArray(MEMORY_OVERVIEW_KEY);
            for (int i = 0; i < memoryOverviewData.length(); i++) {
                getMemoryOverviewController().memoryTable.getColumns().get(i).setVisible(memoryOverviewData.getBoolean(i));
            }
        } catch (JSONException exception) {
            logger.severe("Error while parsing layout");
            logger.severe(ExceptionUtils.getStackTrace(exception));
        }

        LAYOUT_SAVING_TIMELINE.stop();
    }

    /**
     * Sauvegarde le layout actuel.
     */
    public void saveLayout() {
        if (!getLayoutJSON().equals(getSettingsController().getLayout())) {
            logger.info("Saving layout");
            getSettingsController().setLayout(getLayoutJSON());
        }
    }

    /**
     * Initialise le layout.
     */
    public void initLayout() {
        logger.info("Initialising layout");
        LAYOUT_INIT_TIMELINE.play();
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
    protected void onSaveAll() {
        getMainMenuController().onSaveAll();
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
    protected void onReloadAll() {
        getMainMenuController().onReloadAll();
    }

    @FXML
    protected void onReload() {
        getMainMenuController().onReload();
    }

    @FXML
    protected void onCloseAll() {
        getMainMenuController().onCloseAll();
    }

    @FXML
    protected void onClose() {
        getMainMenuController().onClose();
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