package fr.dwightstudio.jarmemu;

import fr.dwightstudio.jarmemu.gui.controllers.*;
import fr.dwightstudio.jarmemu.sim.*;
import fr.dwightstudio.jarmemu.sim.parse.RegexSourceParser;
import fr.dwightstudio.jarmemu.sim.parse.SourceParser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
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
    private SourceParser sourceParser;
    private CodeInterpreter codeInterpreter;
    private ExecutionWorker executionWorker;
    
    
    public Status status;
    public Stage stage;
    private boolean unsaved = true;

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
        sourceParser = new RegexSourceParser(new SourceScanner(""));
        codeInterpreter = new CodeInterpreter();
        executionWorker = new ExecutionWorker(this);

        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

        Font.loadFont(getClass().getResourceAsStream("gui/roboto/static/RobotoMono-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("gui/roboto/static/RobotoMono-Thin.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("gui/roboto/static/RobotoMono-ThinItalic.ttf"), 14);

        scene.getStylesheets().add(getClass().getResource("gui/registers-style.css").toExternalForm());
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        status = Status.EDITING;

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

    public void setTitle(String title) {
        this.stage.setTitle("JArmEmu - " + title + (unsaved && !title.endsWith("*") ? "*" : ""));
    }

    // TODO: Améliorer la gestion de l'enregistrement (avec comparaison)
    public void setUnsaved() {
        String old = this.stage.getTitle();
        if (!unsaved && !old.endsWith("*")) {
            Platform.runLater(() -> this.stage.setTitle(old + "*"));
        }
        unsaved = true;
    }

    public void setSaved() {
        if (unsaved) {
            Platform.runLater(() -> this.stage.setTitle(stage.getTitle().substring(0, stage.getTitle().length() - 1)));
        }
        unsaved = false;
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
}