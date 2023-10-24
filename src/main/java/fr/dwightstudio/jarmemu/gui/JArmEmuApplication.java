package fr.dwightstudio.jarmemu.gui;

import fr.dwightstudio.jarmemu.asm.Instruction;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

public class JArmEmuApplication extends Application {

    public static final String VERSION = JArmEmuApplication.class.getPackage().getImplementationVersion();

    public JAREmuController controller;
    public EditorManager editorManager;

    @Override
    public void start(Stage stage) throws IOException {
        controller = new JAREmuController();
        editorManager = new EditorManager();
        controller.editorManager = editorManager;

        FXMLLoader fxmlLoader = new FXMLLoader(JArmEmuApplication.class.getResource("main-view.fxml"));

        fxmlLoader.setController(controller);

        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        stage.setTitle("JArmEmu");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        editorManager.clean();
    }

    public static void main(String[] args) {
        JArmEmuApplication.launch();
    }
}