package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.ExecutionWorker;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.sim.parse.SourceParser;
import fr.dwightstudio.jarmemu.util.SafeAddressConverter;
import fr.dwightstudio.jarmemu.util.SafeStringConverter;
import javafx.scene.control.Alert;
import javafx.scene.control.SpinnerValueFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class SettingsController extends AbstractJArmEmuModule {

    private static final String VERSION_KEY = "version";
    private static final String LAST_SAVE_PATH_KEY = "lastSavePath";
    private static final String SIMULATION_INTERVAL_KEY = "simulationInterval";
    private static final String SOURCE_PARSER_KEY = "sourceParser";
    private static final String DATA_FORMAT_KEY = "dataFormat";
    private static final String STACK_ADDRESS_KEY = "stackAddress";
    private static final String SYMBOLS_ADDRESS_KEY = "symbolsAddress";
    private static final String THEME_KEY = "theme";

    private static final String[] SOURCE_PARSER_LABEL_DICT = new String[]{"Regex Parser (default)", "Legacy Parser"};
    private static final String[] DATA_FORMAT_LABEL_DICT = new String[]{"Hexadecimal (default)", "Signed Decimal", "Unsigned Decimal"};
    private static final String[] THEME_LABEL_DICT = new String[]{"Dark (default)", "Light"};

    private final Logger logger = Logger.getLogger(getClass().getName());

    private Preferences preferences;
    private SpinnerValueFactory<Integer> simIntervalValue;
    private SpinnerValueFactory<Integer> stackAddressValue;
    private SpinnerValueFactory<Integer> symbolsAddressValue;


    public SettingsController(JArmEmuApplication application) {
        super(application);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        simIntervalValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, ExecutionWorker.UPDATE_THRESHOLD, 50);
        stackAddressValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, StateContainer.DEFAULT_STACK_ADDRESS, 4);
        symbolsAddressValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, StateContainer.DEFAULT_SYMBOLS_ADDRESS, 4);

        simIntervalValue.setConverter(new SafeStringConverter(simIntervalValue));
        stackAddressValue.setConverter(new SafeAddressConverter(stackAddressValue));
        symbolsAddressValue.setConverter(new SafeAddressConverter(symbolsAddressValue));

        getController().settingsSimInterval.setValueFactory(simIntervalValue);
        getController().settingsStackAddress.setValueFactory(stackAddressValue);
        getController().settingsSymbolsAddress.setValueFactory(symbolsAddressValue);

        preferences = Preferences.userRoot().node(getApplication().getClass().getPackage().getName());

        if (preferences.get("version", "").isEmpty()) {
            setToDefaults();
        } else {
            updateGUI();
        }

        simIntervalValue.valueProperty().addListener(((obs, oldVal, newVal) -> setSimulationInterval(newVal)));
        stackAddressValue.valueProperty().addListener(((obs, oldVal, newVal) -> setStackAddress(newVal)));
        symbolsAddressValue.valueProperty().addListener(((obs, oldVal, newVal) -> setSymbolsAddress(newVal)));
    }

    /**
     * Met à jour les paramètres sur le GUI.
     */
    private void updateGUI() {
        simIntervalValue.setValue(getSimulationInterval());
        getController().settingsParser.setText(SOURCE_PARSER_LABEL_DICT[getSourceParserSetting()]);
        getController().settingsFormat.setText(DATA_FORMAT_LABEL_DICT[getDataFormat()]);
        getController().settingsTheme.setText(THEME_LABEL_DICT[getTheme()]);
        stackAddressValue.setValue(getStackAddress());
        symbolsAddressValue.setValue(getSymbolsAddress());
    }

    /**
     * Remet les paramètres aux valeurs d'origine
     */
    public void setToDefaults() {
        preferences.put(VERSION_KEY, JArmEmuApplication.VERSION);
        preferences.put(LAST_SAVE_PATH_KEY, "");

        preferences.putInt(SIMULATION_INTERVAL_KEY, ExecutionWorker.UPDATE_THRESHOLD);
        preferences.putInt(SOURCE_PARSER_KEY, SourceParser.DEFAULT_SOURCE_PARSER);
        preferences.putInt(STACK_ADDRESS_KEY, StateContainer.DEFAULT_STACK_ADDRESS);
        preferences.putInt(SYMBOLS_ADDRESS_KEY, StateContainer.DEFAULT_SYMBOLS_ADDRESS);
        preferences.putInt(DATA_FORMAT_KEY, JArmEmuApplication.DEFAULT_DATA_FORMAT);
        updateGUI();
    }

    /**
     * Méthode invoquée par JavaFX
     */
    protected void onSettingsRegex() {
        setSourceParser(0);
        getController().settingsParser.setText(SOURCE_PARSER_LABEL_DICT[0]);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    protected void onSettingsLegacy() {
        setSourceParser(1);
        getController().settingsParser.setText(SOURCE_PARSER_LABEL_DICT[1]);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    protected void onSettingsHex() {
        setDataFormat(0);
        getController().settingsFormat.setText(DATA_FORMAT_LABEL_DICT[0]);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    protected void onSettingsDec() {
        setDataFormat(1);
        getController().settingsFormat.setText(DATA_FORMAT_LABEL_DICT[1]);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    protected void onSettingsUDec() {
        setDataFormat(2);
        getController().settingsFormat.setText(DATA_FORMAT_LABEL_DICT[2]);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    public void onSettingsDark() {
        setTheme(0);
        getController().settingsTheme.setText(THEME_LABEL_DICT[0]);
    }

    /**
     * Méthode invoquée par JavaFX
     */
    protected void onSettingsLight() {
        setTheme(1);
        getController().settingsTheme.setText(THEME_LABEL_DICT[1]);
    }

    private void setSimulationInterval(int nb) {
        if (nb < ExecutionWorker.UPDATE_THRESHOLD) {
            new Alert(Alert.AlertType.WARNING, "Setting the simulation interval below 50ms disables systematic GUI update to prevent glitches with the front-end. You may see steps being skipped (this is just visual, the back-end is still running as usual).").show();
        }
        preferences.putInt(SIMULATION_INTERVAL_KEY, nb);
    }

    public int getSimulationInterval() {
        return preferences.getInt(SIMULATION_INTERVAL_KEY, ExecutionWorker.UPDATE_THRESHOLD);
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
        return Math.max(Math.min(preferences.getInt(DATA_FORMAT_KEY, JArmEmuApplication.DEFAULT_DATA_FORMAT), 2), 0);
    }

    public void setDataFormat(int nb) {
        preferences.putInt(DATA_FORMAT_KEY, nb);
    }

    public int getTheme() {
        return Math.max(Math.min(preferences.getInt(THEME_KEY, 0), 1), 0);
    }

    public void setTheme(int nb) {
        preferences.putInt(THEME_KEY, nb);
        getApplication().updateUserAgentStyle(nb);
    }
}
