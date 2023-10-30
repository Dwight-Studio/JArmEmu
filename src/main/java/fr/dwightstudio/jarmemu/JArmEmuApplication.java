package fr.dwightstudio.jarmemu;

import fr.dwightstudio.jarmemu.gui.EditorManager;
import fr.dwightstudio.jarmemu.gui.JAREmuController;
import fr.dwightstudio.jarmemu.sim.*;
import fr.dwightstudio.jarmemu.sim.parse.RegexSourceParser;
import fr.dwightstudio.jarmemu.sim.parse.SourceParser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class JArmEmuApplication extends Application {

    public static final String VERSION = JArmEmuApplication.class.getPackage().getImplementationVersion();

    public final Logger logger = Logger.getLogger(getClass().getName());

    public JAREmuController controller;
    public EditorManager editorManager;
    public SourceParser sourceParser;
    public CodeInterpreter codeInterpreter;
    public ExecutionWorker executionWorker;

    public Stage stage;
    private boolean unsaved = true;

    @Override
    public void start(Stage stage) throws IOException {
        LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("logging.properties"));

        logger.info("Starting JARMEmu");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gui/main-view.fxml"));

        fxmlLoader.setController(new JAREmuController());
        controller = fxmlLoader.getController();
        controller.init(this);
        editorManager = new EditorManager();
        controller.editorManager = editorManager;
        codeInterpreter = new CodeInterpreter(this);
        executionWorker = new ExecutionWorker(this);
        this.stage = stage;
        sourceParser = new RegexSourceParser(new SourceScanner(""));

        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        setTitle("New File");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        editorManager.clean();
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
}