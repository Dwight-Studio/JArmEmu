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

import fr.dwightstudio.jarmemu.base.asm.parser.SourceParser;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.editor.RealTimeParser;
import fr.dwightstudio.jarmemu.base.sim.ExecutionWorker;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.base.util.converters.SpinnerAddressConverter;
import fr.dwightstudio.jarmemu.base.util.converters.SpinnerStringConverter;
import javafx.beans.value.ChangeListener;
import javafx.fxml.Initializable;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class SettingsController implements Initializable {

    public static final ChangeListener<Toggle> PREVENT_UNSELECTION = (obs, oldVal, newVal) -> {
        if (newVal == null) {
            oldVal.setSelected(true);
        }
    };

    // Valeurs par défaut
    public static final boolean DEFAULT_AUTO_COMPLETION = true;

    public static final boolean DEFAULT_IGNORE_UNIMPLEMENTED = false;
    public static final boolean DEFAULT_IGNORE_DEPRECATED = false;

    public static final boolean DEFAULT_MANUAL_BREAK = true;
    public static final boolean DEFAULT_CODE_BREAK = true;
    public static final boolean DEFAULT_AUTO_BREAK = true;
    public static final boolean DEFAULT_MEMORY_ALIGN_BREAK = false;
    public static final boolean DEFAULT_STACK_ALIGN_BREAK = true;
    public static final boolean DEFAULT_PROGRAM_ALIGN_BREAK = true;
    public static final boolean DEFAULT_FUNCTION_NESTING_BREAK = true;
    public static final boolean DEFAULT_READ_ONLY_WRITING_BREAK = true;

    public static final int DEFAULT_DATA_FORMAT = 0;
    public static final boolean DEFAULT_FOLLOW_SP = true;
    public static final boolean DEFAULT_HIGHLIGHT_UPDATES = true;

    public static final int DEFAULT_THEME_FAMILY = 0;
    public static final int DEFAULT_THEME_VARIATION = 0;
    public static final int DEFAULT_MAX_NOTIFICATION = 4;

    public static final boolean DEFAULT_MAXIMIZED = false;
    public static final String DEFAULT_MAXIMIZED_LAYOUT = "{\"windowSize\":[1920,1051],\"splitPanes\":{\"mainSplitPane\":[0.15,0.7],\"leftSplitPane\":[0.5]},\"memoryColumns\":{\"memoryDetails\":[true,true,false,true,true,true,true],\"memoryOverview\":[true,false,true,true,true,true]}}";
    public static final String DEFAULT_MINIMIZED_LAYOUT = "{\"windowSize\":[1440,788],\"splitPanes\":{\"mainSplitPane\":[0.2,0.75],\"leftSplitPane\":[0.5]},\"memoryColumns\":{\"memoryDetails\":[true,true,false,true,true,true,true],\"memoryOverview\":[true,false,true,true,true,true]}}";

    // Clés
    public static final String VERSION_KEY = "version";
    public static final String LAST_SAVE_PATH_KEY = "lastSavePath";
    public static final String IGNORE_VERSION_KEY = "ignoreVersion";

    public static final String REAL_TIME_PARSER_KEY = "realTimeParser";
    public static final String AUTO_COMPLETION_KEY = "autoCompletion";

    public static final String SIMULATION_INTERVAL_KEY = "simulationInterval";
    public static final String SOURCE_PARSER_KEY = "sourceParser";
    public static final String IGNORE_UNIMPLEMENTED_KEY = "ignoreUnimplemented";
    public static final String IGNORE_DEPRECATED_KEY = "ignoreDeprecated";

    public static final String MANUAL_BREAK_KEY = "manualBreakpoints";
    public static final String CODE_BREAK_KEY = "codeBreakpoints";
    public static final String AUTO_BREAK_KEY = "automaticBreakpoints";
    public static final String MEMORY_ALIGN_BREAK_KEY = "memoryAlignmentBreakpoint";
    public static final String STACK_ALIGN_BREAK_KEY = "stackPointerAlignmentBreakpoint";
    public static final String PROGRAM_ALIGN_BREAK_KEY = "programCounterAlignmentBreakpoint";
    public static final String FUNCTION_NESTING_BREAK_KEY = "functionNestingBreakpoint";
    public static final String READ_ONLY_WRITING_BREAK_KEY = "readOnlyDataOverwrittenBreakpoint";

    public static final String STACK_ADDRESS_KEY = "stackAddress";
    public static final String PROGRAM_ADDRESS_KEY = "programAddress";
    public static final String DATA_FORMAT_KEY = "dataFormat";
    public static final String FOLLOW_SP_KEY = "followSP";
    public static final String HIGHLIGHT_UPDATES_KEY = "highlightUpdates";

    public static final String THEME_FAMILY_KEY = "themeFamily";
    public static final String THEME_VARIATION_KEY = "theme";
    public static final String MAX_NOTIFICATION_KEY = "maxNotification";

    public static final String MAXIMIZED_KEY = "maximized";
    public static final String MAXIMIZED_LAYOUT_KEY = "maximizedLayout";
    public static final String MINIMIZED_LAYOUT_KEY = "minimizedLayout";

    private static final String[] DATA_FORMAT_LABEL_DICT = new String[]{
            JArmEmuApplication.formatMessage("%settings.dataManagement.hexadecimal"),
            JArmEmuApplication.formatMessage("%settings.dataManagement.signedDecimal"),
            JArmEmuApplication.formatMessage("%settings.dataManagement.unsignedDecimal")
    };
    private static final String[] THEME_FAMILY_LABEL_DICT = new String[]{"Primer", "Nord", "Cupertino"};
    public static final String[] DATA_FORMAT_DICT = new String[]{"%08x", "%d", "%d"};
    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    private boolean initiated;

    private Preferences preferences;

    // Spinners
    private SpinnerValueFactory<Integer> simIntervalValue;
    private SpinnerValueFactory<Integer> stackAddressValue;
    private SpinnerValueFactory<Integer> programAddressValue;
    private SpinnerValueFactory<Integer> maxNotificationValue;

    // ToggleGroup
    private ToggleGroup realTimeParserGroup;
    private ToggleGroup parserGroup;
    private ToggleGroup themeGroup;

    private ToggleButton[] realTimeParserToggles;
    private ToggleButton[] parserToggles;
    private ToggleButton[] themeToggles;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Gestion des spinners
        simIntervalValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, ExecutionWorker.FALLBACK_UPDATE_INTERVAL, ExecutionWorker.UPDATE_THRESHOLD);
        stackAddressValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, StateContainer.DEFAULT_STACK_ADDRESS, 4);
        programAddressValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, StateContainer.DEFAULT_PROGRAM_ADDRESS, 4);
        maxNotificationValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, DEFAULT_MAX_NOTIFICATION, 1);

        simIntervalValue.setConverter(new SpinnerStringConverter(simIntervalValue));
        stackAddressValue.setConverter(new SpinnerAddressConverter(stackAddressValue));
        programAddressValue.setConverter(new SpinnerAddressConverter(programAddressValue));
        maxNotificationValue.setConverter(new SpinnerStringConverter(maxNotificationValue));

        JArmEmuApplication.getController().settingsSimInterval.setValueFactory(simIntervalValue);
        JArmEmuApplication.getController().settingsStackAddress.setValueFactory(stackAddressValue);
        JArmEmuApplication.getController().settingsProgramAddress.setValueFactory(programAddressValue);
        JArmEmuApplication.getController().settingsMaxNotification.setValueFactory(maxNotificationValue);

        // Gestion des ToggleGroups
        realTimeParserGroup = new ToggleGroup();
        parserGroup = new ToggleGroup();
        themeGroup = new ToggleGroup();

        realTimeParserToggles = new ToggleButton[] {JArmEmuApplication.getController().settingsSmart, JArmEmuApplication.getController().settingsSimple};
        parserToggles = new ToggleButton[] {JArmEmuApplication.getController().settingsRegex, JArmEmuApplication.getController().settingsLegacy};
        themeToggles = new ToggleButton[] {JArmEmuApplication.getController().settingsDark, JArmEmuApplication.getController().settingsLight};

        realTimeParserGroup.getToggles().addAll(realTimeParserToggles);
        parserGroup.getToggles().addAll(Arrays.asList(parserToggles));
        themeGroup.getToggles().addAll(Arrays.asList(themeToggles));

        realTimeParserGroup.selectedToggleProperty().addListener(PREVENT_UNSELECTION);
        parserGroup.selectedToggleProperty().addListener(PREVENT_UNSELECTION);
        themeGroup.selectedToggleProperty().addListener(PREVENT_UNSELECTION);

        // Gestion des ToggleSwitches
        JArmEmuApplication.getController().autoCompletionSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setAutoCompletion(newVal));
        JArmEmuApplication.getController().notImplementedSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setIgnoreUnimplemented(newVal));
        JArmEmuApplication.getController().deprecatedSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setIgnoreDeprecated(newVal));
        JArmEmuApplication.getController().manualBreakSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setManualBreak(newVal));
        JArmEmuApplication.getController().codeBreakSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setCodeBreak(newVal));
        JArmEmuApplication.getController().autoBreakSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setAutoBreak(newVal));
        JArmEmuApplication.getController().memoryAlignBreakSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setMemoryAlignBreak(newVal));
        JArmEmuApplication.getController().stackAlignBreakSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setStackAlignBreak(newVal));
        JArmEmuApplication.getController().programAlignBreakSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setProgramAlignBreak(newVal));
        JArmEmuApplication.getController().functionNestingBreakSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setFunctionNestingBreak(newVal));
        JArmEmuApplication.getController().readOnlyWritingBreakSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setReadOnlyWritingBreak(newVal));
        JArmEmuApplication.getController().followSPSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setFollowSPSetting(newVal));
        JArmEmuApplication.getController().updateSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setHighlightUpdates(newVal));

        // Gestion des ChoiceBoxes
        JArmEmuApplication.getController().settingsFormat.getItems().addAll(Arrays.asList(DATA_FORMAT_LABEL_DICT));

        JArmEmuApplication.getController().settingsFormat.valueProperty().addListener((obs, oldVal, newVal) -> {
            for (int i = 0 ; i < DATA_FORMAT_LABEL_DICT.length ; i++) {
                if (DATA_FORMAT_LABEL_DICT[i].equals(newVal)) {
                    setDataFormat(i);
                    if (initiated) JArmEmuApplication.getExecutionWorker().updateFormat();
                }
            }
        });

        JArmEmuApplication.getController().settingsFamily.getItems().addAll(Arrays.asList(THEME_FAMILY_LABEL_DICT));

        JArmEmuApplication.getController().settingsFamily.valueProperty().addListener((obs, oldVal, newVal) -> {
            for (int i = 0 ; i < THEME_FAMILY_LABEL_DICT.length ; i++) {
                if (THEME_FAMILY_LABEL_DICT[i].equals(newVal)) setThemeFamily(i);
            }
        });

        String path = JArmEmuApplication.class.getPackage().getName().replaceAll("\\.", "/");
        logger.info("Setting preferences node to '" + path + "'");
        preferences = Preferences.userRoot().node(path);

        if (preferences.get("version", "").isEmpty()) {
            setToDefaults();
        }

        updateGUI();

        // Listeners pour les spinners
        simIntervalValue.valueProperty().addListener((obs, oldVal, newVal) -> setSimulationInterval(newVal));
        stackAddressValue.valueProperty().addListener((obs, oldVal, newVal) -> setStackAddress(newVal));
        programAddressValue.valueProperty().addListener((obs, oldVal, newVal) -> setProgramAddress(newVal));
        maxNotificationValue.valueProperty().addListener((obs, oldVal, newVal) -> setMaxNotification(newVal));

        initiated = true;
    }

    /**
     * Met à jour les paramètres sur le GUI.
     */
    public void updateGUI() {
        // Spinners
        simIntervalValue.setValue(getSimulationInterval());
        stackAddressValue.setValue(getStackAddress());
        programAddressValue.setValue(getProgramAddress());
        maxNotificationValue.setValue(getMaxNotification());

        // Toggles
        realTimeParserGroup.selectToggle(realTimeParserToggles[getRealTimeParser()]);
        parserGroup.selectToggle(parserToggles[getSourceParser()]);
        themeGroup.selectToggle(themeToggles[getThemeVariation()]);

        // ToggleSwitches
        JArmEmuApplication.getController().autoCompletionSwitch.setDisable(getRealTimeParser() != 0);

        JArmEmuApplication.getController().autoCompletionSwitch.setSelected(getAutoCompletion());
        JArmEmuApplication.getController().notImplementedSwitch.setSelected(getIgnoreUnimplemented());
        JArmEmuApplication.getController().deprecatedSwitch.setSelected(getIgnoreDeprecated());
        JArmEmuApplication.getController().manualBreakSwitch.setSelected(getManualBreak());
        JArmEmuApplication.getController().codeBreakSwitch.setSelected(getCodeBreak());
        JArmEmuApplication.getController().autoBreakSwitch.setSelected(getAutoBreak());
        JArmEmuApplication.getController().memoryAlignBreakSwitch.setSelected(getMemoryAlignBreak());
        JArmEmuApplication.getController().stackAlignBreakSwitch.setSelected(getStackAlignBreak());
        JArmEmuApplication.getController().programAlignBreakSwitch.setSelected(getProgramAlignBreak());
        JArmEmuApplication.getController().functionNestingBreakSwitch.setSelected(getFunctionNestingBreak());
        JArmEmuApplication.getController().readOnlyWritingBreakSwitch.setSelected(getReadOnlyWritingBreak());
        JArmEmuApplication.getController().followSPSwitch.setSelected(getFollowSPSetting());
        JArmEmuApplication.getController().updateSwitch.setSelected(getHighlightUpdates());

        // ChoiceBoxes
        JArmEmuApplication.getController().settingsFamily.setValue(THEME_FAMILY_LABEL_DICT[getThemeFamily()]);
        JArmEmuApplication.getController().settingsFormat.setValue(DATA_FORMAT_LABEL_DICT[getDataFormat()]);

        if (initiated) JArmEmuApplication.getController().applyLayout(getLayout());
    }

    /**
     * Remet les paramètres aux valeurs d'origine
     */
    public void setToDefaults() {
        preferences.put(VERSION_KEY, JArmEmuApplication.VERSION);
        preferences.put(LAST_SAVE_PATH_KEY, "");
        preferences.put(IGNORE_VERSION_KEY, "");

        setRealTimeParser(RealTimeParser.DEFAULT_REAL_TIME_PARSER);
        setAutoCompletion(DEFAULT_AUTO_COMPLETION);

        setSimulationInterval(ExecutionWorker.FALLBACK_UPDATE_INTERVAL);
        setSourceParser(SourceParser.DEFAULT_SOURCE_PARSER);
        setIgnoreUnimplemented(DEFAULT_IGNORE_UNIMPLEMENTED);
        setIgnoreDeprecated(DEFAULT_IGNORE_DEPRECATED);

        setManualBreak(DEFAULT_MANUAL_BREAK);
        setCodeBreak(DEFAULT_CODE_BREAK);
        setAutoBreak(DEFAULT_AUTO_BREAK);
        setMemoryAlignBreak(DEFAULT_MEMORY_ALIGN_BREAK);
        setStackAlignBreak(DEFAULT_STACK_ALIGN_BREAK);
        setProgramAlignBreak(DEFAULT_PROGRAM_ALIGN_BREAK);
        setFunctionNestingBreak(DEFAULT_FUNCTION_NESTING_BREAK);
        setReadOnlyWritingBreak(DEFAULT_READ_ONLY_WRITING_BREAK);

        setStackAddress(StateContainer.DEFAULT_STACK_ADDRESS);
        setProgramAddress(StateContainer.DEFAULT_PROGRAM_ADDRESS);
        setDataFormat(DEFAULT_DATA_FORMAT);
        setFollowSPSetting(DEFAULT_FOLLOW_SP);

        setThemeFamily(DEFAULT_THEME_FAMILY);
        setThemeVariation(DEFAULT_THEME_VARIATION);
        setMaxNotification(DEFAULT_MAX_NOTIFICATION);
        setMaximized(DEFAULT_MAXIMIZED);
        setLayout(DEFAULT_MAXIMIZED ? DEFAULT_MAXIMIZED_LAYOUT : DEFAULT_MINIMIZED_LAYOUT);
    }

    /**
     * Invoked by JavaFX
     */
    protected void onSettingsSmart() {
        setRealTimeParser(0);
    }

    /**
     * Invoked by JavaFX
     */
    protected void onSettingsSimple() {
        setRealTimeParser(1);
    }

    /**
     * Invoked by JavaFX
     */
    protected void onSettingsRegex() {
        setSourceParser(0);
    }

    /**
     * Invoked by JavaFX
     */
    protected void onSettingsLegacy() {
        setSourceParser(1);
    }

    /**
     * Invoked by JavaFX
     */
    public void onSettingsDark() {
        setThemeVariation(0);
    }

    /**
     * Invoked by JavaFX
     */
    protected void onSettingsLight() {
        setThemeVariation(1);
    }

    public void setRealTimeParser(int nb) {
        JArmEmuApplication.getController().autoCompletionSwitch.setDisable(nb != 0);
        preferences.putInt(REAL_TIME_PARSER_KEY, nb);

        JArmEmuApplication.getEditorController().reinitializeRealTimeParsers();
    }

    public int getRealTimeParser() {
        return Math.min(Math.max(preferences.getInt(REAL_TIME_PARSER_KEY, RealTimeParser.DEFAULT_REAL_TIME_PARSER), 0), 1);
    }

    public void setAutoCompletion(boolean autoCompletion) {
        preferences.putBoolean(AUTO_COMPLETION_KEY, autoCompletion);
    }

    public boolean getAutoCompletion() {
        return preferences.getBoolean(AUTO_COMPLETION_KEY, DEFAULT_AUTO_COMPLETION);
    }

    public void setSimulationInterval(int nb) {
        if (nb < ExecutionWorker.UPDATE_THRESHOLD) {
            JArmEmuApplication.getDialogs().warningAlert(JArmEmuApplication.formatMessage("%dialog.simulationInterval.message", ExecutionWorker.UPDATE_THRESHOLD));
        }
        preferences.putInt(SIMULATION_INTERVAL_KEY, nb);
    }

    public int getSimulationInterval() {
        return preferences.getInt(SIMULATION_INTERVAL_KEY, ExecutionWorker.FALLBACK_UPDATE_INTERVAL);
    }

    public void setSourceParser(int nb) {
        preferences.putInt(SOURCE_PARSER_KEY, nb);
        JArmEmuApplication.getInstance().newSourceParser();
    }

    public int getSourceParser() {
        return Math.min(Math.max(preferences.getInt(SOURCE_PARSER_KEY, SourceParser.DEFAULT_SOURCE_PARSER), 0), 1);
    }

    public void setStackAddress(int nb) {
        preferences.putInt(STACK_ADDRESS_KEY, nb);
    }

    public int getStackAddress() {
        return preferences.getInt(STACK_ADDRESS_KEY, StateContainer.DEFAULT_STACK_ADDRESS);
    }

    public void setProgramAddress(int nb) {
        preferences.putInt(PROGRAM_ADDRESS_KEY, nb);
    }

    public int getProgramAddress() {
        return preferences.getInt(PROGRAM_ADDRESS_KEY, StateContainer.DEFAULT_PROGRAM_ADDRESS);
    }

    public String getLastSavePath() {
        return preferences.get(LAST_SAVE_PATH_KEY, "");
    }

    public void setLastSavePath(String path) {
        preferences.put(LAST_SAVE_PATH_KEY, path);
    }

    public int getDataFormat() {
        return Math.max(Math.min(preferences.getInt(DATA_FORMAT_KEY, DEFAULT_DATA_FORMAT), DATA_FORMAT_LABEL_DICT.length-1), 0);
    }

    public void setDataFormat(int nb) {
        preferences.putInt(DATA_FORMAT_KEY, nb);
    }

    public int getThemeVariation() {
        return Math.max(Math.min(preferences.getInt(THEME_VARIATION_KEY, DEFAULT_THEME_VARIATION), 1), 0);
    }

    public void setThemeVariation(int nb) {
        preferences.putInt(THEME_VARIATION_KEY, nb);
        JArmEmuApplication.getInstance().updateUserAgentStyle(nb, this.getThemeFamily());
    }

    public int getThemeFamily() {
        return Math.max(Math.min(preferences.getInt(THEME_FAMILY_KEY, DEFAULT_THEME_FAMILY), THEME_FAMILY_LABEL_DICT.length-1), 0);
    }

    public void setThemeFamily(int nb) {
        preferences.putInt(THEME_FAMILY_KEY, nb);
        JArmEmuApplication.getInstance().updateUserAgentStyle(this.getThemeVariation(), nb);
    }

    public boolean getManualBreak() {
        return preferences.getBoolean(MANUAL_BREAK_KEY, DEFAULT_MANUAL_BREAK);
    }

    public void setManualBreak(boolean b) {
        preferences.putBoolean(MANUAL_BREAK_KEY, b);
    }

    public boolean getCodeBreak() {
        return preferences.getBoolean(CODE_BREAK_KEY, DEFAULT_CODE_BREAK);
    }

    public void setCodeBreak(boolean b) {
        preferences.putBoolean(CODE_BREAK_KEY, b);
    }

    public boolean getAutoBreak() {
        return preferences.getBoolean(AUTO_BREAK_KEY, DEFAULT_AUTO_BREAK);
    }

    public void setAutoBreak(boolean b) {
        JArmEmuApplication.getController().memoryAlignBreakSwitch.setDisable(!b);
        JArmEmuApplication.getController().stackAlignBreakSwitch.setDisable(!b);
        JArmEmuApplication.getController().programAlignBreakSwitch.setDisable(!b);
        JArmEmuApplication.getController().functionNestingBreakSwitch.setDisable(!b);
        JArmEmuApplication.getController().readOnlyWritingBreakSwitch.setDisable(!b);
        preferences.putBoolean(AUTO_BREAK_KEY, b);
    }

    public boolean getMemoryAlignBreak() {
        return preferences.getBoolean(MEMORY_ALIGN_BREAK_KEY, DEFAULT_MEMORY_ALIGN_BREAK);
    }

    public void setMemoryAlignBreak(boolean b) {
        preferences.putBoolean(MEMORY_ALIGN_BREAK_KEY, b);
    }

    public boolean getStackAlignBreak() {
        return preferences.getBoolean(STACK_ALIGN_BREAK_KEY, DEFAULT_STACK_ALIGN_BREAK);
    }

    public void setStackAlignBreak(boolean b) {
        preferences.putBoolean(STACK_ALIGN_BREAK_KEY, b);
    }

    public boolean getProgramAlignBreak() {
        return preferences.getBoolean(PROGRAM_ALIGN_BREAK_KEY, DEFAULT_PROGRAM_ALIGN_BREAK);
    }

    public void setProgramAlignBreak(boolean b) {
        preferences.putBoolean(PROGRAM_ALIGN_BREAK_KEY, b);
    }

    public boolean getFunctionNestingBreak() {
        return preferences.getBoolean(FUNCTION_NESTING_BREAK_KEY, DEFAULT_FUNCTION_NESTING_BREAK);
    }

    public void setFunctionNestingBreak(boolean b) {
        preferences.putBoolean(FUNCTION_NESTING_BREAK_KEY, b);
    }

    public boolean getReadOnlyWritingBreak() {
        return preferences.getBoolean(READ_ONLY_WRITING_BREAK_KEY, DEFAULT_READ_ONLY_WRITING_BREAK);
    }

    public void setReadOnlyWritingBreak(boolean b) {
        preferences.putBoolean(READ_ONLY_WRITING_BREAK_KEY, b);
    }

    public boolean getFollowSPSetting() {
        return preferences.getBoolean(FOLLOW_SP_KEY, DEFAULT_FOLLOW_SP);
    }

    public void setFollowSPSetting(boolean b) {
        preferences.putBoolean(FOLLOW_SP_KEY, b);
    }

    public boolean getHighlightUpdates() {
        return preferences.getBoolean(HIGHLIGHT_UPDATES_KEY, DEFAULT_HIGHLIGHT_UPDATES);
    }

    public void setHighlightUpdates(boolean b) {
        preferences.putBoolean(HIGHLIGHT_UPDATES_KEY, b);
    }

    public boolean getMaximized() {
        return preferences.getBoolean(MAXIMIZED_KEY, DEFAULT_MAXIMIZED);
    }

    public void setMaximized(boolean b) {
        preferences.putBoolean(MAXIMIZED_KEY, b);
    }

    public String getLayout() {
        if (getMaximized()) return preferences.get(MAXIMIZED_LAYOUT_KEY, DEFAULT_MAXIMIZED_LAYOUT);
        else return preferences.get(MINIMIZED_LAYOUT_KEY, DEFAULT_MINIMIZED_LAYOUT);
    }

    public void setLayout(String s) {
        if (getMaximized()) preferences.put(MAXIMIZED_LAYOUT_KEY, s);
        else preferences.put(MINIMIZED_LAYOUT_KEY, s);
    }

    public int getMaxNotification() {
        return preferences.getInt(MAX_NOTIFICATION_KEY, DEFAULT_MAX_NOTIFICATION);
    }

    public void setMaxNotification(int i) {
        if (i == 0) {
            JArmEmuApplication.getDialogs().warningAlert(JArmEmuApplication.formatMessage("%dialog.maxNotification.message"));
        }
        preferences.putInt(MAX_NOTIFICATION_KEY, i);
    }

    public String getIgnoreVersion() {
        return preferences.get(IGNORE_VERSION_KEY, "");
    }

    public void setIgnoreVersion(String s) {
        preferences.put(IGNORE_VERSION_KEY, s);
    }

    public void setIgnoreUnimplemented(boolean b) {
        preferences.putBoolean(IGNORE_UNIMPLEMENTED_KEY, b);
    }

    public boolean getIgnoreUnimplemented() {
        return preferences.getBoolean(IGNORE_UNIMPLEMENTED_KEY, DEFAULT_IGNORE_UNIMPLEMENTED);
    }

    public void setIgnoreDeprecated(boolean b) {
        preferences.putBoolean(IGNORE_DEPRECATED_KEY, b);
    }

    public boolean getIgnoreDeprecated() {
        return preferences.getBoolean(IGNORE_DEPRECATED_KEY, DEFAULT_IGNORE_DEPRECATED);
    }
}
