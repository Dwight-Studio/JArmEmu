package fr.dwightstudio.jarmemu.gui;

import fr.dwightstudio.jarmemu.sim.SourceInterpreter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class JArmEmuApplication extends Application {

    public static final String VERSION = JArmEmuApplication.class.getPackage().getImplementationVersion();

    public JAREmuController controller;
    public EditorManager editorManager;
    public SourceInterpreter sourceInterpreter;

    public Stage stage;
    private boolean unsaved = true;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(JArmEmuApplication.class.getResource("main-view.fxml"));

        fxmlLoader.setController(new JAREmuController());
        controller = fxmlLoader.getController();
        controller.init(this);
        editorManager = new EditorManager();
        controller.editorManager = editorManager;

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