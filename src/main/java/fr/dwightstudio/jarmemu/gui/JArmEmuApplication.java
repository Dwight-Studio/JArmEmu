package fr.dwightstudio.jarmemu.gui;

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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class JArmEmuApplication extends Application {

    public static final String VERSION = JArmEmuApplication.class.getPackage().getImplementationVersion() != null ? JArmEmuApplication.class.getPackage().getImplementationVersion() : "NotFound" ;
    public static final Logger logger = Logger.getLogger(JArmEmuApplication.class.getName());

    // Format Settings
    public static final int DEFAULT_DATA_FORMAT = 0;
    public static final String[] DATA_FORMAT_DICT = new String[]{"%08x", "%d", "%d"};

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


    public Status status;
    public Stage stage;
    private String lastSave;
    private String argSave;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;

        logger.info("Starting up JArmEmu v" + VERSION);
        // TODO: Ajouter les automatic breakpoints (lecture en dehors de la grille, stack bizarre etc...)
        // TODO: Ajouter un desktop pour les .s et surtout ajouter un argument pour l'ouverture de fichiers
        // TODO: Gérer les saves inexistantes
        // TODO: Ajouter le about dans help

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));

        editorController = new EditorController(this);
        mainMenuController = new MainMenuController(this);
        memoryController = new MemoryController(this);
        registersController = new RegistersController(this);
        settingsController = new SettingsController(this);
        simulationMenuController = new SimulationMenuController(this);
        stackController = new StackController(this);

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

        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

        Font.loadFont(getClass().getResourceAsStream("fonts/roboto-mono/RobotoMono-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("fonts/roboto-mono/RobotoMono-Thin.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("fonts/roboto-mono/RobotoMono-ThinItalic.ttf"), 14);

        Font.loadFont(getClass().getResourceAsStream("fonts/roboto/Roboto-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("fonts/roboto/Roboto-Medium.ttf"), 14);

        scene.getStylesheets().add(getClass().getResource("jarmemu-style.css").toExternalForm());
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        scene.setOnKeyPressed(shortcutHandler::handle);

        status = Status.EDITING;

        stage.setOnCloseRequest(this::onClosingRequest);
        stage.getIcons().addAll(
                new Image(getClass().getResourceAsStream("medias/favicon@16.png")),
                new Image(getClass().getResourceAsStream("medias/favicon@32.png")),
                new Image(getClass().getResourceAsStream("medias/favicon@64.png")),
                new Image(getClass().getResourceAsStream("medias/favicon@128.png")),
                new Image(getClass().getResourceAsStream("medias/favicon@256.png")),
                new Image(getClass().getResourceAsStream("medias/favicon@512.png")),
                new Image(getClass().getResourceAsStream("medias/logo.png"))
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

    private static void adaptSplashScreen(SplashScreen splashScreen) {
        try {
            int scale = Preferences.userRoot().node(JArmEmuApplication.class.getPackage().getName()).getInt("scale", 100);

            logger.info("Adapting SplashScreen to current screen scale (" + scale + "%)");

            URL url;
            
            if (scale >= 125 && scale < 150) {
                url = JArmEmuApplication.class.getResource("medias/splash@125pct.png");
            } else if (scale >= 150 && scale < 200) {
                url = JArmEmuApplication.class.getResource("medias/splash@150pct.png");
            } else if (scale >= 200 && scale < 250) {
                url = JArmEmuApplication.class.getResource("medias/splash@200pct.png");
            } else if (scale >= 250 && scale < 300) {
                url = JArmEmuApplication.class.getResource("medias/splash@250pct.png");
            } else if (scale >= 300) {
                url = JArmEmuApplication.class.getResource("medias/splash@300pct.png");
            } else {
                url = JArmEmuApplication.class.getResource("medias/splash.png");
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
        setTitle(getMainMenuController().getSavePath().getName());
    }

    /**
     * Défini le contenu de la dernière sauvegarde à rien
     *
     * @apiNote Sert à déterminer l'état actuel de la sauvegarde ('*' dans le titre)
     */
    public void setNew() {
        lastSave = null;
        setTitle("New File");
    }

    /**
     * Met à jour l'état de sauvegarde
     */
    public boolean updateSaveState() {
        boolean saved = true;

        if (getEditorController() != null && lastSave != null) {
            saved = getEditorController().getText().equals(lastSave);
        }

        String fileName = getMainMenuController().getSavePath() == null || lastSave == null ? "New File" : getMainMenuController().getSavePath().getName();

        if (saved) {
            setTitle(fileName);
        } else {
            setTitle(fileName + "*");
        }

        return saved;
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

    private void onClosingRequest(WindowEvent event) {
        if (!updateSaveState()) {
            ButtonType saveAndQuit = new ButtonType("Save and Close");
            ButtonType discardAndQuit = new ButtonType("Discard and Close");
            ButtonType cancel = new ButtonType("Cancel");

            Alert alert = new Alert(Alert.AlertType.WARNING, "The open file has unsaved changes. Changes will be permanently lost. Do you want to save the file?", saveAndQuit, discardAndQuit, cancel);

            Optional<ButtonType> option = alert.showAndWait();

            if (option.isPresent()) {
                if (option.get() == saveAndQuit) {
                    getMainMenuController().onSave();
                } else if (option.get() == cancel) {
                    event.consume();
                }
            } else {
                event.consume();
            }
        }
    }

    /**
     * Affiche un avertissement lors de la fermeture d'un fichier
     *
     * @return vrai si on continue la fermeture, faux sinon
     */
    public boolean warnUnsaved() {
        WindowEvent event = new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST);

        this.onClosingRequest(event);

        return !event.isConsumed();
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
            return String.format(DATA_FORMAT_DICT[format], (long) data & 0xFFFFFFFFL).toUpperCase();
        } else {
            return String.format(DATA_FORMAT_DICT[format], data).toUpperCase();

        }
    }

    /**
     * @return le chemin vers le fichier passé en paramètre (ou null si rien n'est passé en paramètre)
     */
    public String getArgSave() {
        return argSave;
    }
}