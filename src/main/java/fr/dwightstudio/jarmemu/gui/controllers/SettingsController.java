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

import fr.dwightstudio.jarmemu.gui.AbstractJArmEmuModule;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.ExecutionWorker;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.asm.parser.SourceParser;
import fr.dwightstudio.jarmemu.util.converters.SpinnerAddressConverter;
import fr.dwightstudio.jarmemu.util.converters.SpinnerStringConverter;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class SettingsController extends AbstractJArmEmuModule {

    public static final ChangeListener<Toggle> PREVENT_UNSELECTION = (obs, oldVal, newVal) -> {
        if (newVal == null) {
            oldVal.setSelected(true);
        }
    };

    // Valeurs par défaut
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
    public static final String DEFAULT_LAYOUT = "{\"splitPanes\":{\"mainSplitPane\":[0.2,0.75],\"leftSplitPane\":[0.5]},\"maximized\":true,\"memoryColumns\":{\"memoryDetails\":[true,true,false,true,true,true,true],\"memoryOverview\":[true,false,true,true,true,true]}}";

    public static final String VERSION_KEY = "version";
    public static final String LAST_SAVE_PATH_KEY = "lastSavePath";

    public static final String SIMULATION_INTERVAL_KEY = "simulationInterval";
    public static final String SOURCE_PARSER_KEY = "sourceParser";

    public static final String MANUAL_BREAK_KEY = "manualBreakpoints";
    public static final String CODE_BREAK_KEY = "codeBreakpoints";
    public static final String AUTO_BREAK_KEY = "automaticBreakpoints";
    public static final String MEMORY_ALIGN_BREAK_KEY = "memoryAlignmentBreakpoint";
    public static final String STACK_ALIGN_BREAK_KEY = "stackPointerAlignmentBreakpoint";
    public static final String PROGRAM_ALIGN_BREAK_KEY = "programCounterAlignmentBreakpoint";
    public static final String FUNCTION_NESTING_BREAK_KEY = "functionNestingBreakpoint";
    public static final String READ_ONLY_WRITING_BREAK_KEY = "readOnlyDataOverwrittenBreakpoint";

    public static final String STACK_ADDRESS_KEY = "stackAddress";
    public static final String SYMBOLS_ADDRESS_KEY = "symbolsAddress";
    public static final String DATA_FORMAT_KEY = "dataFormat";
    public static final String FOLLOW_SP_KEY = "followSP";
    public static final String HIGHLIGHT_UPDATES_KEY = "highlightUpdates";

    public static final String THEME_FAMILY_KEY = "themeFamily";
    public static final String THEME_VARIATION_KEY = "theme";
    public static final String MAX_NOTIFICATION_KEY = "maxNotification";
    public static final String LAYOUT_KEY = "layout";

    private static final String[] DATA_FORMAT_LABEL_DICT = new String[]{
            JArmEmuApplication.formatMessage("%settings.dataManagement.hexadecimal"),
            JArmEmuApplication.formatMessage("%settings.dataManagement.signedDecimal"),
            JArmEmuApplication.formatMessage("%settings.dataManagement.unsignedDecimal")
    };
    private static final String[] THEME_FAMILY_LABEL_DICT = new String[]{"Primer", "Nord", "Cupertino"};
    public static final String[] DATA_FORMAT_DICT = new String[]{"%08x", "%d", "%d"};
    private final Logger logger = Logger.getLogger(getClass().getName());
    private boolean initiated;

    private Preferences preferences;

    // Spinners
    private SpinnerValueFactory<Integer> simIntervalValue;
    private SpinnerValueFactory<Integer> stackAddressValue;
    private SpinnerValueFactory<Integer> symbolsAddressValue;
    private SpinnerValueFactory<Integer> maxNotificationValue;

    // ToggleGroup
    private ToggleGroup parserGroup;
    private ToggleGroup themeGroup;

    private ToggleButton[] parserToggles;
    private ToggleButton[] themeToggles;


    public SettingsController(JArmEmuApplication application) {
        super(application);
        initiated = false;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Gestion des spinners
        simIntervalValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, ExecutionWorker.FALLBACK_UPDATE_INTERVAL, ExecutionWorker.UPDATE_THRESHOLD);
        stackAddressValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, StateContainer.DEFAULT_STACK_ADDRESS, 4);
        symbolsAddressValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, StateContainer.DEFAULT_SYMBOLS_ADDRESS, 4);
        maxNotificationValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, DEFAULT_MAX_NOTIFICATION, 1);

        simIntervalValue.setConverter(new SpinnerStringConverter(simIntervalValue));
        stackAddressValue.setConverter(new SpinnerAddressConverter(stackAddressValue));
        symbolsAddressValue.setConverter(new SpinnerAddressConverter(symbolsAddressValue));
        maxNotificationValue.setConverter(new SpinnerStringConverter(maxNotificationValue));

        getController().settingsSimInterval.setValueFactory(simIntervalValue);
        getController().settingsStackAddress.setValueFactory(stackAddressValue);
        getController().settingsSymbolsAddress.setValueFactory(symbolsAddressValue);
        getController().settingsMaxNotification.setValueFactory(maxNotificationValue);

        // Gestion des ToggleGroups
        parserGroup = new ToggleGroup();
        themeGroup = new ToggleGroup();

        parserToggles = new ToggleButton[] {getController().settingsRegex, getController().settingsLegacy};
        themeToggles = new ToggleButton[] {getController().settingsDark, getController().settingsLight};

        parserGroup.getToggles().addAll(Arrays.asList(parserToggles));
        themeGroup.getToggles().addAll(Arrays.asList(themeToggles));

        parserGroup.selectedToggleProperty().addListener(PREVENT_UNSELECTION);
        themeGroup.selectedToggleProperty().addListener(PREVENT_UNSELECTION);

        // Gestion des ToggleSwitches
        getController().manualBreakSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setManualBreak(newVal));
        getController().codeBreakSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setCodeBreak(newVal));
        getController().autoBreakSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setAutoBreak(newVal));
        getController().memoryAlignBreakSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setMemoryAlignBreak(newVal));
        getController().stackAlignBreakSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setStackAlignBreak(newVal));
        getController().programAlignBreakSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setProgramAlignBreak(newVal));
        getController().functionNestingBreakSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setFunctionNestingBreak(newVal));
        getController().readOnlyWritingBreakSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setReadOnlyWritingBreak(newVal));
        getController().followSPSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setFollowSPSetting(newVal));
        getController().updateSwitch.selectedProperty().addListener((obs, oldVal, newVal) -> setHighlightUpdates(newVal));

        // Gestion des ChoiceBoxes
        getController().settingsFormat.getItems().addAll(Arrays.asList(DATA_FORMAT_LABEL_DICT));

        getController().settingsFormat.valueProperty().addListener((obs, oldVal, newVal) -> {
            for (int i = 0 ; i < DATA_FORMAT_LABEL_DICT.length ; i++) {
                if (DATA_FORMAT_LABEL_DICT[i].equals(newVal)) {
                    setDataFormat(i);
                    if (initiated) getExecutionWorker().updateFormat();
                }
            }
        });

        getController().settingsFamily.getItems().addAll(Arrays.asList(THEME_FAMILY_LABEL_DICT));

        getController().settingsFamily.valueProperty().addListener((obs, oldVal, newVal) -> {
            for (int i = 0 ; i < THEME_FAMILY_LABEL_DICT.length ; i++) {
                if (THEME_FAMILY_LABEL_DICT[i].equals(newVal)) setThemeFamily(i);
            }
        });

        String path = getApplication().getClass().getPackage().getName().replaceAll("\\.", "/");
        logger.info("Setting preferences node to '" + path + "'");
        preferences = Preferences.userRoot().node(path);

        if (preferences.get("version", "").isEmpty()) {
            setToDefaults();
        }

        updateGUI();

        // Listeners pour les spinners
        simIntervalValue.valueProperty().addListener((obs, oldVal, newVal) -> setSimulationInterval(newVal));
        stackAddressValue.valueProperty().addListener((obs, oldVal, newVal) -> setStackAddress(newVal));
        symbolsAddressValue.valueProperty().addListener((obs, oldVal, newVal) -> setSymbolsAddress(newVal));
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
        symbolsAddressValue.setValue(getSymbolsAddress());
        maxNotificationValue.setValue(getMaxNotification());

        // Toggles
        parserGroup.selectToggle(parserToggles[getSourceParserSetting()]);
        themeGroup.selectToggle(themeToggles[getThemeVariation()]);

        // ToggleSwitches
        getController().manualBreakSwitch.setSelected(getManualBreak());
        getController().codeBreakSwitch.setSelected(getCodeBreak());
        getController().autoBreakSwitch.setSelected(getAutoBreak());
        getController().memoryAlignBreakSwitch.setSelected(getMemoryAlignBreak());
        getController().stackAlignBreakSwitch.setSelected(getStackAlignBreak());
        getController().programAlignBreakSwitch.setSelected(getProgramAlignBreak());
        getController().functionNestingBreakSwitch.setSelected(getFunctionNestingBreak());
        getController().readOnlyWritingBreakSwitch.setSelected(getReadOnlyWritingBreak());
        getController().followSPSwitch.setSelected(getFollowSPSetting());
        getController().updateSwitch.setSelected(getHighlightUpdates());

        // ChoiceBoxes
        getController().settingsFamily.setValue(THEME_FAMILY_LABEL_DICT[getThemeFamily()]);
        getController().settingsFormat.setValue(DATA_FORMAT_LABEL_DICT[getDataFormat()]);

        if (initiated) getController().applyLayout(getLayout());
    }

    /**
     * Remet les paramètres aux valeurs d'origine
     */
    public void setToDefaults() {
        preferences.put(VERSION_KEY, JArmEmuApplication.VERSION);
        preferences.put(LAST_SAVE_PATH_KEY, "");

        setSimulationInterval(ExecutionWorker.FALLBACK_UPDATE_INTERVAL);
        setSourceParser(SourceParser.DEFAULT_SOURCE_PARSER);

        setManualBreak(DEFAULT_MANUAL_BREAK);
        setCodeBreak(DEFAULT_CODE_BREAK);
        setAutoBreak(DEFAULT_AUTO_BREAK);
        setMemoryAlignBreak(DEFAULT_MEMORY_ALIGN_BREAK);
        setStackAlignBreak(DEFAULT_STACK_ALIGN_BREAK);
        setProgramAlignBreak(DEFAULT_PROGRAM_ALIGN_BREAK);
        setFunctionNestingBreak(DEFAULT_FUNCTION_NESTING_BREAK);
        setReadOnlyWritingBreak(DEFAULT_READ_ONLY_WRITING_BREAK);

        setStackAddress(StateContainer.DEFAULT_STACK_ADDRESS);
        setSymbolsAddress(StateContainer.DEFAULT_SYMBOLS_ADDRESS);
        setDataFormat(DEFAULT_DATA_FORMAT);
        setFollowSPSetting(DEFAULT_FOLLOW_SP);

        setThemeFamily(DEFAULT_THEME_FAMILY);
        setThemeVariation(DEFAULT_THEME_VARIATION);
        setMaxNotification(DEFAULT_MAX_NOTIFICATION);
        setLayout(DEFAULT_LAYOUT);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    protected void onSettingsRegex() {
        setSourceParser(0);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    protected void onSettingsLegacy() {
        setSourceParser(1);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onSettingsDark() {
        setThemeVariation(0);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    protected void onSettingsLight() {
        setThemeVariation(1);
    }

    private void setSimulationInterval(int nb) {
        if (nb < ExecutionWorker.UPDATE_THRESHOLD) {
            getDialogs().warningAlert(JArmEmuApplication.formatMessage("%dialog.simulationInterval.message", ExecutionWorker.UPDATE_THRESHOLD));
        }
        preferences.putInt(SIMULATION_INTERVAL_KEY, nb);
    }

    public int getSimulationInterval() {
        return preferences.getInt(SIMULATION_INTERVAL_KEY, ExecutionWorker.FALLBACK_UPDATE_INTERVAL);
    }

    public void setSourceParser(int nb) {
        preferences.putInt(SOURCE_PARSER_KEY, nb);
        application.newSourceParser();
    }

    public int getSourceParserSetting() {
        return Math.min(Math.max(preferences.getInt(SOURCE_PARSER_KEY, SourceParser.DEFAULT_SOURCE_PARSER), 0), 1);
    }

    public void setStackAddress(int nb) {
        preferences.putInt(STACK_ADDRESS_KEY, nb);
    }

    public int getStackAddress() {
        return preferences.getInt(STACK_ADDRESS_KEY, StateContainer.DEFAULT_STACK_ADDRESS);
    }

    public void setSymbolsAddress(int nb) {
        preferences.putInt(SYMBOLS_ADDRESS_KEY, nb);
    }

    public int getSymbolsAddress() {
        return preferences.getInt(SYMBOLS_ADDRESS_KEY, StateContainer.DEFAULT_SYMBOLS_ADDRESS);
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
        getApplication().updateUserAgentStyle(nb, this.getThemeFamily());
    }

    public int getThemeFamily() {
        return Math.max(Math.min(preferences.getInt(THEME_FAMILY_KEY, DEFAULT_THEME_FAMILY), THEME_FAMILY_LABEL_DICT.length-1), 0);
    }

    public void setThemeFamily(int nb) {
        preferences.putInt(THEME_FAMILY_KEY, nb);
        getApplication().updateUserAgentStyle(this.getThemeVariation(), nb);
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
        getController().memoryAlignBreakSwitch.setDisable(!b);
        getController().stackAlignBreakSwitch.setDisable(!b);
        getController().programAlignBreakSwitch.setDisable(!b);
        getController().functionNestingBreakSwitch.setDisable(!b);
        getController().readOnlyWritingBreakSwitch.setDisable(!b);
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

    public String getLayout() {
        return preferences.get(LAYOUT_KEY, DEFAULT_LAYOUT);
    }

    public void setLayout(String s) {
        preferences.put(LAYOUT_KEY, s);
    }

    public int getMaxNotification() {
        return preferences.getInt(MAX_NOTIFICATION_KEY, DEFAULT_MAX_NOTIFICATION);
    }

    public void setMaxNotification(int i) {
        if (i == 0) {
            getDialogs().warningAlert(JArmEmuApplication.formatMessage("%dialog.maxNotification.message"));
        }
        preferences.putInt(MAX_NOTIFICATION_KEY, i);
    }
}
