package fr.dwightstudio.jarmemu;

import fr.dwightstudio.jarmemu.gui.ShortcutHandler;
import fr.dwightstudio.jarmemu.gui.controllers.*;
import fr.dwightstudio.jarmemu.sim.*;
import fr.dwightstudio.jarmemu.sim.parse.RegexSourceParser;
import fr.dwightstudio.jarmemu.sim.parse.SourceParser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class JArmEmuApplication extends Application {

    public static final String VERSION = JArmEmuApplication.class.getPackage().getImplementationVersion();

    public final Logger logger = Logger.getLogger(getClass().getName());

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

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;

        LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("logging.properties"));
        logger.info("Starting JArmEmu");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gui/main-view.fxml"));

        editorController = new EditorController(this);
        mainMenuController = new MainMenuController(this);
        memoryController = new MemoryController(this);
        registersController = new RegistersController(this);
        settingsController = new SettingsController(this);
        simulationMenuController = new SimulationMenuController(this);
        stackController = new StackController(this);

        fxmlLoader.setController(new JArmEmuController(this));
        controller = fxmlLoader.getController();


        // Others
        shortcutHandler = new ShortcutHandler(this);
        sourceParser = new RegexSourceParser(new SourceScanner(""));
        codeInterpreter = new CodeInterpreter();
        executionWorker = new ExecutionWorker(this);

        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

        Font.loadFont(getClass().getResourceAsStream("gui/roboto/static/RobotoMono-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("gui/roboto/static/RobotoMono-Thin.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("gui/roboto/static/RobotoMono-ThinItalic.ttf"), 14);

        scene.getStylesheets().add(getClass().getResource("gui/registers-style.css").toExternalForm());
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        scene.setOnKeyPressed(shortcutHandler::handle);

        status = Status.EDITING;
        lastSave = null;

        stage.setOnCloseRequest(this::onClosingRequest);

        setTitle("New File");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        editorController.clean();

    }

    public static void main(String[] args) {
        JArmEmuApplication.launch();
    }

    private void setTitle(String title) {
        Platform.runLater(() -> this.stage.setTitle("JArmEmu - " + title));
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
}