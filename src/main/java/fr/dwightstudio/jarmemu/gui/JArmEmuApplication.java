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

package fr.dwightstudio.jarmemu.gui;

import atlantafx.base.theme.*;
import fr.dwightstudio.jarmemu.Status;
import fr.dwightstudio.jarmemu.gui.controllers.*;
import fr.dwightstudio.jarmemu.sim.CodeInterpreter;
import fr.dwightstudio.jarmemu.sim.ExecutionWorker;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.sim.parse.LegacySourceParser;
import fr.dwightstudio.jarmemu.sim.parse.RegexSourceParser;
import fr.dwightstudio.jarmemu.sim.parse.SourceParser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class JArmEmuApplication extends Application {

    public static final String VERSION = JArmEmuApplication.class.getPackage().getImplementationVersion() != null ? JArmEmuApplication.class.getPackage().getImplementationVersion() : "NotFound" ;
    public static final Logger logger = Logger.getLogger(JArmEmuApplication.class.getName());

    // Controllers
    private JArmEmuController controller;

    private EditorController editorController;
    private MainMenuController mainMenuController;
    private MemoryController memoryController;
    private RegistersController registersController;
    private SettingsController settingsController;
    private SimulationMenuController simulationMenuController;
    private StackController stackController;

    // Others
    private ShortcutHandler shortcutHandler;
    private SourceParser sourceParser;
    private CodeInterpreter codeInterpreter;
    private ExecutionWorker executionWorker;
    private JArmEmuDialogs dialogs;


    public Theme theme;
    public Status status;
    public Stage stage;
    public Scene scene;
    private String lastSave;
    private File lastSavePath;
    private String argSave;

    // TODO: Enregistrer la disposition du GUI (splitpane, tableview...)
    // TODO: Refaire les tests pour les initializers de données (pour un argument vide, plusieurs arguments, avec une section incorrecte etc)

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;

        logger.info("Starting up JArmEmu v" + VERSION);

        FXMLLoader fxmlLoader = new FXMLLoader(getResource("main-view.fxml"));

        editorController = new EditorController(this);
        mainMenuController = new MainMenuController(this);
        memoryController = new MemoryController(this);
        registersController = new RegistersController(this);
        settingsController = new SettingsController(this);
        simulationMenuController = new SimulationMenuController(this);
        stackController = new StackController(this);
        dialogs = new JArmEmuDialogs(this);

        fxmlLoader.setController(new JArmEmuController(this));
        controller = fxmlLoader.getController();

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

        status = Status.EDITING;

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
        stage.setScene(scene);
        stage.show();

        SplashScreen splashScreen = SplashScreen.getSplashScreen();

        int scale = (int) (stage.getOutputScaleY() * 100);
        Preferences.userRoot().node(getClass().getPackage().getName()).putInt("scale", scale);

        if (splashScreen != null) {
            splashScreen.close();
        }

        logger.info("Startup finished");
    }

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

    private static void adaptSplashScreen(SplashScreen splashScreen) {
        try {
            int scale = Preferences.userRoot().node(JArmEmuApplication.class.getPackage().getName()).getInt("scale", 100);

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
        editorController.clean();

    }

    public static void main(String[] args) {
        System.setProperty("prism.dirtyopts", "false");
        SplashScreen splashScreen = SplashScreen.getSplashScreen();

        if (splashScreen != null) {
            adaptSplashScreen(splashScreen);
        }

        JArmEmuApplication.launch(args);
    }

    private void setTitle(String title) {
        Platform.runLater(() -> this.stage.setTitle("JArmEmu v" + VERSION +" - " + title));
    }

    /**
     * Défini le contenu de la dernière sauvegarde
     *
     * @apiNote Sert à déterminer l'état actuel de la sauvegarde ('*' dans le titre)
     */
    public void setSaved() {
        lastSave = String.valueOf(getEditorController().getText());
        lastSavePath = getMainMenuController().getSavePath();
        setTitle(getMainMenuController().getSavePath().getName());
    }

    /**
     * Défini le contenu de la dernière sauvegarde à rien
     *
     * @apiNote Sert à déterminer l'état actuel de la sauvegarde ('*' dans le titre)
     */
    public void setNew() {
        lastSave = EditorController.SAMPLE_CODE;
        lastSavePath = null;
        setTitle("New File");
    }

    /**
     * Met à jour l'état de sauvegarde
     *
     * @return vrai si le fichier est sauvegardé
     */
    public boolean updateSaveState() {
        boolean saved = true;

        if (getEditorController() != null) {
            saved = getEditorController().getText().equals(lastSave);
        }

        String fileName = lastSavePath == null ? "New File" : lastSavePath.getName();

        if (saved) {
            setTitle(fileName);
        } else {
            setTitle(fileName + "*");
        }

        return saved;
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

    public MemoryController getMemoryController() {
        return memoryController;
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
        if (!updateSaveState()) {
            getDialogs().unsavedAlert().thenAccept(rtn -> {
                switch (rtn) {
                    case SAVE_AND_CONTINUE -> {
                        getMainMenuController().onSave();
                        Platform.exit();
                    }
                    case DISCARD_AND_CONTINUE -> Platform.exit();
                }
            });

            event.consume();
        }
    }

    public void newSourceParser() {
        if (getSettingsController().getSourceParserSetting() == 1) {
            sourceParser = new LegacySourceParser(new SourceScanner(""));
        } else {
            sourceParser = new RegexSourceParser(new SourceScanner(""));
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
}
