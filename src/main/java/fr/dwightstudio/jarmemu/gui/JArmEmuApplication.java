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

package fr.dwightstudio.jarmemu.gui;

import atlantafx.base.theme.*;
import fr.dwightstudio.jarmemu.Status;
import fr.dwightstudio.jarmemu.asm.parser.SourceParser;
import fr.dwightstudio.jarmemu.asm.parser.regex.RegexSourceParser;
import fr.dwightstudio.jarmemu.gui.controllers.*;
import fr.dwightstudio.jarmemu.sim.CodeInterpreter;
import fr.dwightstudio.jarmemu.sim.ExecutionWorker;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Comparator;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class JArmEmuApplication extends Application {

    public static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    public static final String OS_ARCH = System.getProperty("os.arch").toLowerCase();
    public static final String OS_VERSION = System.getProperty("os.version").toLowerCase();
    public static final String VERSION = JArmEmuApplication.class.getPackage().getImplementationVersion() != null ? JArmEmuApplication.class.getPackage().getImplementationVersion() : "NotFound" ;
    public static final ResourceBundle BUNDLE = ResourceBundle.getBundle("fr.dwightstudio.jarmemu.bundles.Locale");
    public static String LICENCE;

    public static final Logger logger = Logger.getLogger(JArmEmuApplication.class.getSimpleName());

    // Controllers
    private JArmEmuController controller;

    private EditorController editorController;
    private MainMenuController mainMenuController;
    private MemoryDetailsController memoryDetailsController;
    private MemoryOverviewController memoryOverviewController;
    private RegistersController registersController;
    private SettingsController settingsController;
    private SimulationMenuController simulationMenuController;
    private StackController stackController;
    private SymbolsController symbolsController;
    private LabelsController labelsController;

    // Others
    private ShortcutHandler shortcutHandler;
    private SourceParser sourceParser;
    private CodeInterpreter codeInterpreter;
    private ExecutionWorker executionWorker;
    private JArmEmuDialogs dialogs;


    public Theme theme;
    public SimpleObjectProperty<Status> status;
    public Stage stage;
    public Scene scene;
    private String argSave;

    // TODO: Finir l'I18N (Directives)
    // TODO: Refaire les tests (Instructions, Arguments, Directives)
    // TODO: Ajouter l'Autocompletion (style intelliJ)
    // TODO: Ajouter un switch pour les instructions non implémentées
    // TODO: Ajouter un detection des boucles infinies
    // TODO: Ajouter un menu "miroir" du la toolbar dans un onglet "Simulation"
    // TODO: Améliorer les performances d'exécution (en utilisant un stockage des préférences???)

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        this.status = new SimpleObjectProperty<>(Status.INITIALIZING);

        logger.info("Starting up JArmEmu v" + VERSION + " on " + OS_NAME + " v" + OS_VERSION + " (" + OS_ARCH + ")");

        FXMLLoader fxmlLoader = new FXMLLoader(getResource("main-view.fxml"));

        editorController = new EditorController(this);
        mainMenuController = new MainMenuController(this);
        memoryDetailsController = new MemoryDetailsController(this);
        memoryOverviewController = new MemoryOverviewController(this);
        registersController = new RegistersController(this);
        settingsController = new SettingsController(this);
        simulationMenuController = new SimulationMenuController(this);
        stackController = new StackController(this);
        symbolsController = new SymbolsController(this);
        labelsController = new LabelsController(this);
        dialogs = new JArmEmuDialogs(this);

        fxmlLoader.setController(new JArmEmuController(this));
        controller = fxmlLoader.getController();

        fxmlLoader.setResources(BUNDLE);
        try {
            LICENCE = new BufferedReader(new InputStreamReader(getResourceAsStream("Licence.txt"))).lines().collect(Collectors.joining("\n"));
        } catch (Exception ignored) {}

        // Essayer d'ouvrir le fichier passé en paramètre
        if (!getParameters().getUnnamed().isEmpty()) {
            logger.info("Detecting file argument: " + getParameters().getUnnamed().getFirst());
            argSave = getParameters().getUnnamed().getFirst();
        } else {
            argSave = null;
        }

        // Autres
        shortcutHandler = new ShortcutHandler(this);
        codeInterpreter = new CodeInterpreter();
        executionWorker = new ExecutionWorker(this);

        logger.info("Font " + Font.loadFont(getResourceAsStream("fonts/Cantarell/Cantarell-Regular.ttf"), 14).getFamily() + " loaded");
        logger.info("Font " + Font.loadFont(getResourceAsStream("fonts/SourceCodePro/SourceCodePro-Regular.ttf"), 14).getFamily() + " loaded");

        scene = new Scene(fxmlLoader.load(), 1280, 720);
        updateUserAgentStyle(getSettingsController().getThemeVariation(), getSettingsController().getThemeFamily());
        scene.getStylesheets().add(getResource("jarmemu-style.css").toExternalForm());

        scene.setOnKeyPressed(shortcutHandler::handle);

        stage.setOnCloseRequest(this::onClosingRequest);
        stage.getIcons().addAll(
                new Image(getResourceAsStream("medias/favicon@16.png")),
                new Image(getResourceAsStream("medias/favicon@32.png")),
                new Image(getResourceAsStream("medias/favicon@64.png")),
                new Image(getResourceAsStream("medias/favicon@128.png")),
                new Image(getResourceAsStream("medias/favicon@256.png")),
                new Image(getResourceAsStream("medias/favicon@512.png")),
                new Image(getResourceAsStream("medias/logo.png"))
        );

        status.addListener((observable -> updateTitle()));
        getSimulationMenuController().onStop();

        updateTitle();
        stage.setScene(scene);
        stage.show();

        SplashScreen splashScreen = SplashScreen.getSplashScreen();

        int scale = (int) (stage.getOutputScaleY() * 100);
        Preferences.userRoot().node(getClass().getPackage().getName().replaceAll("\\.", "/")).putInt("scale", scale);
        logger.info("Computing scale: " + scale + "%");

        if (splashScreen != null) {
            splashScreen.close();
        }

        status.set(Status.EDITING);
        getController().initLayout();
        logger.info("Startup finished");
    }

    public void updateTitle() {
        stage.setTitle("JArmEmu v" + VERSION + " - " + status.get());
    }

    /**
     * Mise à jour de l'UserAgentStyle pour la modification du thème.
     *
     * @param variation l'indice de la variation
     * @param family l'indice' de la famille
     */
    public void updateUserAgentStyle(int variation, int family) {
        if (family == 0) {
            theme = (variation == 0) ? new PrimerDark() : new PrimerLight();
        } else if (family == 1) {
            theme = (variation == 0) ? new NordDark() : new NordLight();
        } else {
            theme = (variation == 0) ? new CupertinoDark() : new CupertinoLight();
        }

        Application.setUserAgentStylesheet(theme.getUserAgentStylesheet());
    }

    /**
     * Définie l'état de maximisation.
     *
     * @param maximized l'état de maximisation
     */
    public void setMaximized(boolean maximized) {
        stage.setMaximized(maximized);
    }

    /**
     * @return l'état de maximisation
     */
    public boolean isMaximized() {
        return stage.isMaximized();
    }

    public ReadOnlyBooleanProperty maximizedProperty() {
        return stage.maximizedProperty();
    }

    /**
     * Adapte le splashscreen à la dimension de l'écran.
     *
     * @param splashScreen l'instance du splashscreen
     */
    private static void adaptSplashScreen(SplashScreen splashScreen) {
        try {
            int scale = Preferences.userRoot().node(JArmEmuApplication.class.getPackage().getName().replaceAll("\\.", "/")).getInt("scale", 100);

            logger.info("Adapting SplashScreen to current screen scale (" + scale + "%)");

            URL url;
            
            if (scale >= 125 && scale < 150) {
                url = getResource("medias/splash@125pct.png");
            } else if (scale >= 150 && scale < 200) {
                url = getResource("medias/splash@150pct.png");
            } else if (scale >= 200 && scale < 250) {
                url = getResource("medias/splash@200pct.png");
            } else if (scale >= 250 && scale < 300) {
                url = getResource("medias/splash@250pct.png");
            } else if (scale >= 300) {
                url = getResource("jarmemu/medias/splash@300pct.png");
            } else {
                url = getResource("medias/splash.png");
            }

            logger.info("Loading SplashScreen: " + url);
            splashScreen.setImageURL(url);
        } catch (Exception e) {
            logger.severe(ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void stop() {

    }

    public static void main(String[] args) {
        System.setProperty("prism.dirtyopts", "false");
        SplashScreen splashScreen = SplashScreen.getSplashScreen();

        if (splashScreen != null) {
            adaptSplashScreen(splashScreen);
        }

        JArmEmuApplication.launch(args);
    }

    public void openURL(String url) {
        getHostServices().showDocument(url);
    }

    public JArmEmuController getController() {
        return controller;
    }

    public MainMenuController getMainMenuController() {
        return mainMenuController;
    }

    public MemoryDetailsController getMemoryDetailsController() {
        return memoryDetailsController;
    }

    public MemoryOverviewController getMemoryOverviewController() {
        return memoryOverviewController;
    }

    public RegistersController getRegistersController() {
        return registersController;
    }

    public SettingsController getSettingsController() {
        return settingsController;
    }

    public StackController getStackController() {
        return stackController;
    }

    public SymbolsController getSymbolsController() {
        return symbolsController;
    }

    public LabelsController getLabelsController() {
        return labelsController;
    }

    public SourceParser getSourceParser() {
        return sourceParser;
    }

    public CodeInterpreter getCodeInterpreter() {
        return codeInterpreter;
    }

    public ExecutionWorker getExecutionWorker() {
        return executionWorker;
    }

    public EditorController getEditorController() {
        return editorController;
    }

    public SimulationMenuController getSimulationMenuController() {
        return simulationMenuController;
    }

    public JArmEmuDialogs getDialogs() {
        return dialogs;
    }

    private void onClosingRequest(WindowEvent event) {
        event.consume();
        getMainMenuController().onExit();
    }

    public void newSourceParser() {
        if (getSettingsController().getSourceParserSetting() == 1) {
            //sourceParser = new LegacySourceParser(new SourceScanner("", null, 0));
        } else {
            sourceParser = new RegexSourceParser();
        }
    }

    /**
     * @param data les données
     * @param format le format (0, 1, 2)
     * @return une version formatée du nombre en données
     */
    public String getFormattedData(int data, int format) {
        if (format == 2) {
            return String.format(SettingsController.DATA_FORMAT_DICT[format], (long) data & 0xFFFFFFFFL).toUpperCase();
        } else {
            return String.format(SettingsController.DATA_FORMAT_DICT[format], data).toUpperCase();

        }
    }

    /**
     * @param data les données
     * @return une version formatée du nombre en données
     */
    public String getFormattedData(int data) {
        return getFormattedData(data, getSettingsController().getDataFormat());
    }

    /**
     * @return le chemin vers le fichier passé en paramètre (ou null si rien n'est passé en paramètre)
     */
    public String getArgSave() {
        return argSave;
    }

    public static @NotNull URL getResource(String name) {
        return Objects.requireNonNull(JArmEmuApplication.class.getResource("/fr/dwightstudio/jarmemu/" + name));
    }

    public static @NotNull InputStream getResourceAsStream(String name) {
        return Objects.requireNonNull(JArmEmuApplication.class.getResourceAsStream("/fr/dwightstudio/jarmemu/" + name));
    }

    public static String formatMessage(String message, Object ... args) {
        AtomicReference<String> string = new AtomicReference<>(message);
        BUNDLE.keySet().stream().sorted((s1, s2) -> -Integer.compare(s1.length(), s2.length())).forEach(key -> {
            string.set(string.get().replaceAll("%" + key, Matcher.quoteReplacement(BUNDLE.getString(key))));
        });
        message = string.get();
        try {
            return message.formatted(args);
        } catch (Exception e) {
            logger.severe(message);
            throw e;
        }
    }
}
