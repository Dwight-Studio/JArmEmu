package fr.dwightstudio.jarmemu.gui;

import fr.dwightstudio.jarmemu.sim.SourceInterpreter;
import fr.dwightstudio.jarmemu.sim.StateContainer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.controlsfx.dialog.ExceptionDialog;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class JAREmuController implements Initializable {

    public JArmEmuApplication application;
    public EditorManager editorManager;
    public File savePath = null;

    int tmp = 0;

    public static final String DATA_FORMAT = "%08x";

    @FXML
    protected CodeArea codeArea;
    @FXML protected Button simulate;
    @FXML protected Button stepInto;
    @FXML protected Button stepOver;
    @FXML protected Button conti;
    @FXML protected Button pause;
    @FXML protected Button stop;
    @FXML protected Button restart;
    @FXML protected Button reset;

    @FXML protected Label R0;
    @FXML protected Label R1;
    @FXML protected Label R2;
    @FXML protected Label R3;
    @FXML protected Label R4;
    @FXML protected Label R5;
    @FXML protected Label R6;
    @FXML protected Label R7;
    @FXML protected Label R8;
    @FXML protected Label R9;
    @FXML protected Label R10;
    @FXML protected Label R11;
    @FXML protected Label R12;
    @FXML protected Label R13;
    @FXML protected Label R14;
    @FXML protected Label R15;
    @FXML protected Label CPSR;
    @FXML protected Label CPSRT;
    @FXML protected Label SPSR;
    @FXML protected Label SPSRT;

    protected Label[] registers = new Label[]{R0, R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15};

    public void init(JArmEmuApplication application) {
        this.application = application;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        editorManager.init(application);
        application.sourceInterpreter = new SourceInterpreter(codeArea);
    }

    @FXML
    public void onNewFile() {
        editorManager.newFile();
        application.sourceInterpreter.updateFromEditor(codeArea);
        Platform.runLater(() -> {
            application.setTitle("New File");
            application.setUnsaved();
        });
    }

    @FXML
    public void onOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Source File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Assembly Source File", "*.s"));
        if (savePath != null) fileChooser.setInitialDirectory(savePath.isDirectory() ? savePath : savePath.getParentFile());
        File file = fileChooser.showOpenDialog(application.stage);
        if (file != null) {
            savePath = file;
            onReload();
        }
    }

    @FXML
    public void onSave() {
        if (savePath == null) {
            onSaveAs();
        } else {
            try {
                application.sourceInterpreter.updateFromEditor(codeArea);
                application.sourceInterpreter.exportToFile(savePath);
                Platform.runLater(() -> {
                    application.setTitle(savePath.getName());
                    application.setSaved();
                });
            } catch (IOException exception) {
                new ExceptionDialog(exception).show();
            }
        }
    }

    @FXML
    public void onSaveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Source File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Assembly Source File", "*.s"));
        if (savePath != null) fileChooser.setInitialDirectory(savePath.isDirectory() ? savePath : savePath.getParentFile());
        File file = fileChooser.showSaveDialog(application.stage);
        if (file != null) {
            savePath = file;
            onSave();
        }
    }

    @FXML
    public void onReload() {
        if (savePath != null) {
            try {
                application.sourceInterpreter.updateFromFile(savePath);
                application.sourceInterpreter.exportToEditor(codeArea);
                Platform.runLater(() -> {
                    application.setTitle(savePath.getName());
                    application.setSaved();
                });
            } catch (FileNotFoundException exception) {
                new ExceptionDialog(exception).show();
            }
        }
    }

    @FXML
    public void onExit() {
        Platform.exit();
    }

    @FXML
    public void onSimulate() {
        editorManager.registerLines();
        application.sourceInterpreter.updateFromEditor(codeArea);
        application.sourceInterpreter.resetState();
        codeArea.setDisable(true);
        simulate.setDisable(true);
        stepInto.setDisable(false);
        stepOver.setDisable(false);
        conti.setDisable(false);
        pause.setDisable(true);
        stop.setDisable(false);
        restart.setDisable(false);
        reset.setDisable(false);
    }

    @FXML
    public void onStepInto() {
    }

    @FXML
    public void onStepOver() {
    }

    @FXML
    public void onContinue() {
        stepInto.setDisable(true);
        stepOver.setDisable(true);
        conti.setDisable(true);
        pause.setDisable(false);
        restart.setDisable(true);
        reset.setDisable(true);
    }

    @FXML
    public void onPause() {
        stepInto.setDisable(false);
        stepOver.setDisable(false);
        conti.setDisable(false);
        pause.setDisable(true);
        restart.setDisable(false);
        reset.setDisable(false);
    }

    @FXML
    public void onStop() {
        codeArea.deselect();
        codeArea.setDisable(false);
        simulate.setDisable(false);
        stepInto.setDisable(true);
        stepOver.setDisable(true);
        conti.setDisable(true);
        pause.setDisable(true);
        stop.setDisable(true);
        restart.setDisable(true);
        reset.setDisable(true);
    }

    @FXML
    public void onRestart() {
        application.sourceInterpreter.restart();
    }

    @FXML
    public void onReset() {
        application.sourceInterpreter.resetState();
    }

    public void updateRegisters(StateContainer stateContainer) {
        for (int i = 0; i < 16; i++) {
            registers[i].setText(String.format(DATA_FORMAT, stateContainer.registers[i].getData()));
        }

        CPSR.setText(String.format(DATA_FORMAT, stateContainer.cpsr.getData()));
        SPSR.setText(String.format(DATA_FORMAT, stateContainer.spsr.getData()));
    }
}