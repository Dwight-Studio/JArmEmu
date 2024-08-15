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

package fr.dwightstudio.jarmemu.base.gui;

import atlantafx.base.theme.*;
import fr.dwightstudio.jarmemu.base.Status;
import fr.dwightstudio.jarmemu.base.asm.parser.SourceParser;
import fr.dwightstudio.jarmemu.base.asm.parser.legacy.LegacySourceParser;
import fr.dwightstudio.jarmemu.base.asm.parser.regex.RegexSourceParser;
import fr.dwightstudio.jarmemu.base.gui.controllers.*;
import fr.dwightstudio.jarmemu.base.sim.CodeInterpreter;
import fr.dwightstudio.jarmemu.base.sim.ExecutionWorker;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GitHub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JArmEmuApplication extends Application {

    public static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    public static final String OS_ARCH = System.getProperty("os.arch").toLowerCase();
    public static final String OS_VERSION = System.getProperty("os.version").toLowerCase();
    public static final String VERSION = JArmEmuApplication.class.getPackage().getImplementationVersion() != null ? JArmEmuApplication.class.getPackage().getImplementationVersion() : "0.0.0";
    public static final ResourceBundle BUNDLE = ResourceBundle.getBundle("fr.dwightstudio.jarmemu.base.bundles.Locale");
    public static String LICENCE;
    public static final String[] FONTS_URL = new String[]{
            "Cantarell/Cantarell-Regular.ttf",
            "Cantarell/Cantarell-Italic.ttf",
            "Cantarell/Cantarell-Bold.ttf",
            "Cantarell/Cantarell-BoldItalic.ttf",
            "SourceCodePro/SourceCodePro-Regular.ttf",
            "SourceCodePro/SourceCodePro-Italic.ttf",
            "SourceCodePro/SourceCodePro-ExtraLight.ttf",
            "SourceCodePro/SourceCodePro-ExtraLightItalic.ttf",
            "SourceCodePro/SourceCodePro-Light.ttf",
            "SourceCodePro/SourceCodePro-LightItalic.ttf",
            "SourceCodePro/SourceCodePro-Medium.ttf",
            "SourceCodePro/SourceCodePro-MediumItalic.ttf",
            "SourceCodePro/SourceCodePro-SemiBold.ttf",
            "SourceCodePro/SourceCodePro-SemiBoldItalic.ttf",
            "SourceCodePro/SourceCodePro-Bold.ttf",
            "SourceCodePro/SourceCodePro-BoldItalic.ttf",
            "SourceCodePro/SourceCodePro-ExtraBold.ttf",
            "SourceCodePro/SourceCodePro-ExtraBoldItalic.ttf",
            "SourceCodePro/SourceCodePro-Black.ttf",
            "SourceCodePro/SourceCodePro-BlackItalic.ttf",
    };

    public static final Logger logger = Logger.getLogger(JArmEmuApplication.class.getSimpleName());

    private static final Pattern TRANSLATION_KEY_PATTERN = Pattern.compile("%(?<KEY>[a-zA-Z0-9.]+)");

    // Controllers
    private static JArmEmuApplication instance;
    private static JArmEmuController controller;

    private static EditorController editorController;
    private static MainMenuController mainMenuController;
    private static MemoryDetailsController memoryDetailsController;
    private static MemoryOverviewController memoryOverviewController;
    private static RegistersController registersController;
    private static SettingsController settingsController;
    private static SimulationMenuController simulationMenuController;
    private static StackController stackController;
    private static SymbolsController symbolsController;
    private static LabelsController labelsController;
    private static AutocompletionController autocompletionController;

    // Loading objects
    public Scene scene;
    public List<Image> icons;

    // Others
    private static SourceParser sourceParser;
    private static CodeInterpreter codeInterpreter;
    private static ExecutionWorker executionWorker;
    private static JArmEmuDialogs dialogs;

    public Theme theme;
    public SimpleObjectProperty<Status> status;
    public Stage stage;
    private ArrayList<String> argSave;

    private URL downloadURL;
    private String newVersion;

    public static void main(String[] args) {
        launch(args);
    }

    // TODO: Ajouter le support de plus de directives pour l'autocomplétion
    // TODO: Ajouter une detection des boucles infinies
    // TODO: Ajouter des hints pour les nouveaux utilisateurs (par exemple pour les breakpoints, double cliques sur symbols...)
    // TODO: Ajouter un enregistrement du layout des tableaux
    // TODO: Detect and warn from program overwriting

    @Override
    public void init() {
        instance = this;
        this.status = new SimpleObjectProperty<>(Status.INITIALIZING);

        logger.info("Starting up JArmEmu v" + VERSION + " on " + OS_NAME + " v" + OS_VERSION + " (" + OS_ARCH + ")");
        logger.info("Initializing application");
        JArmEmuApplication.notifyPreloader("Starting up");

        FXMLLoader fxmlLoader = new FXMLLoader(getResource("main-view.fxml"));

        editorController = new EditorController();
        mainMenuController = new MainMenuController();
        memoryDetailsController = new MemoryDetailsController();
        memoryOverviewController = new MemoryOverviewController();
        registersController = new RegistersController();
        settingsController = new SettingsController();
        simulationMenuController = new SimulationMenuController();
        stackController = new StackController();
        symbolsController = new SymbolsController();
        labelsController = new LabelsController();
        autocompletionController = new AutocompletionController();
        dialogs = new JArmEmuDialogs();

        fxmlLoader.setController(new JArmEmuController());
        controller = fxmlLoader.getController();

        fxmlLoader.setResources(BUNDLE);
        try {
            LICENCE = new BufferedReader(new InputStreamReader(getResourceAsStream("Licence.txt"))).lines().collect(Collectors.joining("\n"));
        } catch (Exception ignored) {
        }

        // Essayer d'ouvrir le fichier passé en paramètre
        argSave = new ArrayList<String>();
        if (!getParameters().getUnnamed().isEmpty()) {
            for (String arg : getParameters().getUnnamed()) {
                if (arg.startsWith("--")) continue;
                logger.info("Detecting file argument: " + getParameters().getUnnamed().getFirst());
                argSave.add(arg);
            }
        }

        // Autres
        codeInterpreter = new CodeInterpreter();
        executionWorker = new ExecutionWorker();

        for (String fontURL : FONTS_URL) {
            Font font = Font.loadFont(getMediaAsStream("fonts/" + fontURL), 14);
            logger.info("Font " + font.getFamily() + " " + font.getStyle() + " loaded");
        }

        try {
            scene = new Scene(fxmlLoader.load(), 1280, 720);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        updateUserAgentStyle(getSettingsController().getThemeVariation(), getSettingsController().getThemeFamily());
        scene.getStylesheets().add(getResource("jarmemu-style.css").toExternalForm());
        scene.setCursor(Cursor.WAIT);

        icons = List.of(
                new Image(getMediaAsStream("images/favicon@16.png")),
                new Image(getMediaAsStream("images/favicon@32.png")),
                new Image(getMediaAsStream("images/favicon@64.png")),
                new Image(getMediaAsStream("images/favicon@128.png")),
                new Image(getMediaAsStream("images/favicon@256.png")),
                new Image(getMediaAsStream("images/favicon@512.png")),
                new Image(getMediaAsStream("images/logo.png"))
        );

        // Check for update
        if (!getParameters().getRaw().contains("--offline")) {
            if (!VERSION.equals("NotFound")) {
                try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {
                    Future<?> future = executorService.submit(() -> {
                        logger.info("Checking for update...");
                        JArmEmuApplication.notifyPreloader("Checking for Update");
                        try {
                            GitHub gitHub = GitHub.connectAnonymously();

                            GHRelease release = gitHub.getRepository("Dwight-Studio/JArmEmu").getLatestRelease();
                            String latestVersion = release.getTagName().substring(1);

                            if (latestVersion.compareTo(VERSION) > 0) {
                                logger.info("Running outdated version of JArmEmu (v" + VERSION + " -> v" + latestVersion + ")");

                                downloadURL = release.getHtmlUrl();
                                newVersion = latestVersion;
                            } else {
                                logger.info("JArmEmu is up to date");
                            }
                        } catch (IOException exception) {
                            logger.warning("Can't verify version information (" + exception.getMessage() + ")");
                        }
                    });

                    try {
                        future.get(5, TimeUnit.SECONDS);
                    } catch (TimeoutException e) {
                        logger.warning("Timed out waiting for version information");
                    } catch (ExecutionException | InterruptedException exception) {
                        logger.warning("Can't verify version information (" + exception.getMessage() + ")");
                        logger.warning(ExceptionUtils.getStackTrace(exception));
                    }

                    executorService.shutdownNow();
                }
            } else {
                logger.warning("Can't verify version information (unknown version)");
            }
        } else {
            logger.info("Skipping checking for update (offline mode)");
        }
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        logger.info("Opening application");

        stage.setOnCloseRequest(this::onClosingRequest);
        stage.getIcons().addAll(icons);

        status.addListener((observable -> updateTitle()));

        stage.setScene(scene);
        stage.setMinHeight(360);
        stage.setMinWidth(640);
        stage.centerOnScreen();
        if (settingsController.getMaximized() != isMaximized()) {
            setMaximized(settingsController.getMaximized());
        }
        controller.applyLayout(JArmEmuApplication.getSettingsController().getLayout());
        stage.show();

        status.set(Status.EDITING);
        logger.info("Startup finished");

        JArmEmuApplication.notifyPreloader("Applying GUI layout");

        new Timeline(
                new KeyFrame(Duration.seconds(1), actionEvent -> {
                    JArmEmuApplication.notifyPreloader("Finishing up");
                    controller.applyLayout(JArmEmuApplication.getSettingsController().getLayout());
                    JArmEmuApplication.getExecutionWorker().updateGUI();
                }),

                new KeyFrame(Duration.seconds(2), actionEvent -> {
                    JArmEmuApplication.closePreloader();
                    controller.registerLayoutChangeListener();

                    if (downloadURL != null) {
                        if (newVersion.equals(getSettingsController().getIgnoreVersion())) {
                            logger.info("Ignoring version update v" + newVersion);
                            return;
                        }

                        Button download = new Button(JArmEmuApplication.formatMessage("%notification.outdated.download"));
                        download.setOnAction(event -> {
                            JArmEmuApplication.getEditorController().clearNotifications();
                            JArmEmuApplication.openURL(downloadURL.toExternalForm());
                        });
                        download.getStyleClass().add(Styles.ACCENT);

                        Button dontShowAgain = new Button(JArmEmuApplication.formatMessage("%notification.outdated.dontShowAgain"));
                        dontShowAgain.setOnAction(event -> {
                            getSettingsController().setIgnoreVersion(newVersion);
                            JArmEmuApplication.getEditorController().clearNotifications();
                        });
                        dontShowAgain.getStyleClass().add(Styles.DANGER);

                        getEditorController().addNotification(
                                JArmEmuApplication.formatMessage("%notification.outdated.title", VERSION, newVersion),
                                JArmEmuApplication.formatMessage("%notification.outdated.message"),
                                Styles.ACCENT,
                                download,
                                dontShowAgain
                        );
                    }
                })
        ).play();
    }

    public void updateTitle() {
        stage.setTitle("JArmEmu v" + VERSION + " - " + status.get());
    }

    /**
     * Mise à jour de l'UserAgentStyle pour la modification du thème.
     *
     * @param variation l'indice de la variation
     * @param family    l'indice' de la famille
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

    @Override
    public void stop() {

    }

    public static void openURL(String url) {
        instance.getHostServices().showDocument(url);
    }

    public static JArmEmuApplication getInstance() {
        return instance;
    }

    public static Stage getStage() {
        return instance.stage;
    }

    public static Status getStatus() {
        return instance.status.get();
    }

    public static void setStatus(Status status) {
        instance.status.set(status);
    }

    public static JArmEmuController getController() {
        return controller;
    }

    public static MainMenuController getMainMenuController() {
        return mainMenuController;
    }

    public static MemoryDetailsController getMemoryDetailsController() {
        return memoryDetailsController;
    }

    public static MemoryOverviewController getMemoryOverviewController() {
        return memoryOverviewController;
    }

    public static RegistersController getRegistersController() {
        return registersController;
    }

    public static SettingsController getSettingsController() {
        return settingsController;
    }

    public static StackController getStackController() {
        return stackController;
    }

    public static SymbolsController getSymbolsController() {
        return symbolsController;
    }

    public static LabelsController getLabelsController() {
        return labelsController;
    }

    public static SourceParser getSourceParser() {
        return sourceParser;
    }

    public static CodeInterpreter getCodeInterpreter() {
        return codeInterpreter;
    }

    public static ExecutionWorker getExecutionWorker() {
        return executionWorker;
    }

    public static EditorController getEditorController() {
        return editorController;
    }

    public static SimulationMenuController getSimulationMenuController() {
        return simulationMenuController;
    }

    public static AutocompletionController getAutocompletionController() {
        return autocompletionController;
    }

    public static JArmEmuDialogs getDialogs() {
        return dialogs;
    }

    private void onClosingRequest(WindowEvent event) {
        event.consume();
        getMainMenuController().onExit();
    }

    public void newSourceParser() {
        if (getSettingsController().getSourceParser() == 1) {
            sourceParser = new LegacySourceParser();
        } else {
            sourceParser = new RegexSourceParser();
        }
    }

    /**
     * @param data   les données
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
    public String[] getArgSave() {
        return argSave.toArray(String[]::new);
    }

    public static @NotNull URL getResource(String name) {
        return Objects.requireNonNull(JArmEmuApplication.class.getResource("/fr/dwightstudio/jarmemu/base/" + name));
    }

    public static @NotNull InputStream getResourceAsStream(String name) {
        return Objects.requireNonNull(JArmEmuApplication.class.getResourceAsStream("/fr/dwightstudio/jarmemu/base/" + name));
    }

    public static @NotNull URL getMedia(String name) {
        return Objects.requireNonNull(JArmEmuApplication.class.getResource("/fr/dwightstudio/jarmemu/medias/" + name));
    }

    public static @NotNull InputStream getMediaAsStream(String name) {
        return Objects.requireNonNull(JArmEmuApplication.class.getResourceAsStream("/fr/dwightstudio/jarmemu/medias/" + name));
    }

    public static String formatMessage(String message, Object... args) {
        StringBuilder builder = new StringBuilder();
        Matcher matcher = TRANSLATION_KEY_PATTERN.matcher(message);

        while (matcher.find()) {
            String key = matcher.group("KEY");
            String replacement = key;
            try {
                replacement = BUNDLE.getString(key);
            } catch (MissingResourceException exception) {
                logger.warning("Can't find translation key '" + key + "'");
            } finally {
                matcher.appendReplacement(builder, Matcher.quoteReplacement(replacement));
            }
        }

        try {
            return builder.toString().formatted(args);
        } catch (IllegalFormatException e) {
            logger.severe(ExceptionUtils.getStackTrace(e));
            return "#Invalid translation string#";
        }
    }

    /**
     * Updates preloader with loading informations
     *
     * @param message the message to display
     */
    public static void notifyPreloader(String message) {
        instance.notifyPreloader(new LoadingNotification(message));
    }

    /**
     * Closes preloader
     */
    public static void closePreloader() {
        instance.notifyPreloader(new CloseNotification());
        instance.scene.setCursor(Cursor.DEFAULT);
    }
}
