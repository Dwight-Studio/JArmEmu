package fr.dwightstudio.jarmemu.gui;

import fr.dwightstudio.jarmemu.JArmEmuApplication;
import fr.dwightstudio.jarmemu.asm.AssemblySyntaxException;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;
import fr.dwightstudio.jarmemu.sim.LegacySourceParser;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.controlsfx.dialog.ExceptionDialog;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JAREmuController implements Initializable {

    public JArmEmuApplication application;
    public EditorManager editorManager;
    public File savePath = null;
    public static final String DATA_FORMAT = "%08x";

    private final Logger logger = Logger.getLogger(getClass().getName());

    @FXML
    protected CodeArea codeArea;
    @FXML
    protected VBox notif;
    @FXML protected Button simulate;
    @FXML protected Button stepInto;
    @FXML protected Button stepOver;
    @FXML protected Button conti;
    @FXML protected Button pause;
    @FXML protected Button stop;
    @FXML protected Button restart;
    @FXML protected Button reset;

    @FXML protected Text R0;
    @FXML protected Text R1;
    @FXML protected Text R2;
    @FXML protected Text R3;
    @FXML protected Text R4;
    @FXML protected Text R5;
    @FXML protected Text R6;
    @FXML protected Text R7;
    @FXML protected Text R8;
    @FXML protected Text R9;
    @FXML protected Text R10;
    @FXML protected Text R11;
    @FXML protected Text R12;
    @FXML protected Text R13;
    @FXML protected Text R14;
    @FXML protected Text R15;
    @FXML protected Text CPSR;
    @FXML protected Text CPSRT;
    @FXML protected Text SPSR;
    @FXML protected Text SPSRT;

    protected Text[] registers;

    public void init(JArmEmuApplication application) {
        this.application = application;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        registers = new Text[]{R0, R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15};
        editorManager.init(application);
        application.sourceParser = new LegacySourceParser(codeArea);
    }

    @FXML
    public void onNewFile() {
        editorManager.newFile();
        codeArea.clear();
        codeArea.insertText(0, application.sourceParser.getSourceScanner().exportCode());
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
                application.sourceParser.setSourceScanner(new SourceScanner(codeArea.getText()));
                application.sourceParser.getSourceScanner().exportCodeToFile(savePath);
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
                application.sourceParser.setSourceScanner(new SourceScanner(savePath));
                codeArea.clear();
                codeArea.insertText(0, application.sourceParser.getSourceScanner().exportCode());
                Platform.runLater(() -> {
                    application.setTitle(savePath.getName());
                    application.setSaved();
                });
                onStop();
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
        application.sourceParser.setSourceScanner(new SourceScanner(codeArea.getText()));
        application.codeInterpreter.load(application.sourceParser);
        application.codeInterpreter.resetState();
        application.codeInterpreter.restart();

        clearNotifs();
        AssemblyError[] errors = new AssemblyError[0];
        try {
            errors = application.codeInterpreter.verifyAll();
        } catch (Exception e) {
            new ExceptionDialog(e).show();
        }

        if (errors.length == 0 && application.codeInterpreter.getLineCount() != 0) {
            application.executionWorker.revive();
            application.codeInterpreter.resetState();
            application.controller.editorManager.clearExecutedLines();
            codeArea.setDisable(true);
            simulate.setDisable(true);
            stepInto.setDisable(false);
            stepOver.setDisable(false);
            conti.setDisable(false);
            pause.setDisable(true);
            stop.setDisable(false);
            restart.setDisable(false);
            reset.setDisable(false);
        } else {
            if (application.codeInterpreter.getLineCount() == 0) addError(new AssemblyError(0, new AssemblySyntaxException("No instructions detected")));
            for (AssemblyError error : errors) {
                addError(error);
            }
        }
    }

    @FXML
    public void onStepInto() {
        application.executionWorker.stepInto();
    }

    @FXML
    public void onStepOver() {
        application.executionWorker.stepOver();
    }

    @FXML
    public void onContinue() {
        application.executionWorker.conti();
        stepInto.setDisable(true);
        stepOver.setDisable(true);
        conti.setDisable(true);
        pause.setDisable(false);
        restart.setDisable(true);
        reset.setDisable(true);
    }

    @FXML
    public void onPause() {
        application.executionWorker.pause();
        stepInto.setDisable(false);
        stepOver.setDisable(false);
        conti.setDisable(false);
        pause.setDisable(true);
        restart.setDisable(false);
        reset.setDisable(false);
    }

    @FXML
    public void onStop() {
        application.controller.clearNotifs();
        application.executionWorker.pause();
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
        application.controller.clearNotifs();
        application.controller.editorManager.clearExecutedLines();
        application.codeInterpreter.restart();
    }

    @FXML
    public void onReset() {
        application.controller.clearNotifs();
        application.codeInterpreter.resetState();
    }

    public void updateRegisters(StateContainer stateContainer) {
        for (int i = 0; i < 16; i++) {
            registers[i].setText(String.format(DATA_FORMAT, stateContainer.registers[i].getData()).toUpperCase());
        }

        CPSR.setText(String.format(DATA_FORMAT, stateContainer.cpsr.getData()).toUpperCase());
        CPSRT.setText(stateContainer.cpsr.toString());
        SPSR.setText(String.format(DATA_FORMAT, stateContainer.spsr.getData()).toUpperCase());
        SPSRT.setText(stateContainer.cpsr.toString());
    }

    public void addNotif(String titleString, String contentString, String classString) {

        if (notif.getChildren().size() > 5) return;

        TextFlow textFlow = new TextFlow();
        textFlow.setMinHeight(32);
        textFlow.getStyleClass().add("alert");
        textFlow.getStyleClass().add("alert-" + classString);

        Text title = new Text(titleString);
        title.getStyleClass().add("strong");
        textFlow.getChildren().add(title);

        Text label = new Text(" " + contentString);
        textFlow.getChildren().add(label);

        notif.getChildren().add(textFlow);
    }

    public void addError(AssemblyError error) {
        addNotif("[ln " + error.getLine() + "] Syntax Error:", error.getException().getMessage(), "danger");
        logger.log(Level.INFO, ExceptionUtils.getStackTrace(error.getException()));
    }

    public void clearNotifs() {
        notif.getChildren().clear();
    }
}