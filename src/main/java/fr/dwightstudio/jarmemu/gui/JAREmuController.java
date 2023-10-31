package fr.dwightstudio.jarmemu.gui;

import fr.dwightstudio.jarmemu.JArmEmuApplication;
import fr.dwightstudio.jarmemu.Status;
import fr.dwightstudio.jarmemu.sim.SourceScanner;
import fr.dwightstudio.jarmemu.sim.obj.AssemblyError;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.util.MathUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
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
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JAREmuController implements Initializable {

    public static final String HEX_FORMAT = "%08x";
    public static final String DEC_FORMAT = "%08d";
    public static final int LINES_PER_PAGE = 512;
    public static final int ADDRESS_PER_LINE = 4;
    public static final int ADDRESS_PER_PAGE = LINES_PER_PAGE * ADDRESS_PER_LINE;
    public static final int PAGE_NUMBER = (int) (((long) Math.pow(2L, 32L)) / ADDRESS_PER_PAGE);
    public static final int LINE_HEIGHT = 20;

    public JArmEmuApplication application;
    public EditorManager editorManager;
    public File savePath = null;

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
    @FXML protected GridPane memoryGrid;
    @FXML protected ScrollPane memoryScroll;
    @FXML protected ScrollBar memoryScrollBar;
    @FXML protected AnchorPane memoryPane;
    @FXML protected Pagination memoryPage;

    protected Text[] registers;
    protected Text[][] memory;
    protected int memoryIndex;
    protected ArrayList<Text> stack;

    public void attach(JArmEmuApplication application) {
        this.application = application;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        registers = new Text[]{R0, R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12, R13, R14, R15};

        memory = new Text[LINES_PER_PAGE][7];
        memoryIndex = 0;

        for (int i = 0; i < LINES_PER_PAGE; i++) {

            for (int j = 0; j < 7; j++) {
                Text node = new Text("00000000");
                node.getStyleClass().add(j == 0 ? "reg-address" : "reg-data");
                memoryGrid.add(node, j, i);
                memory[i][j] = node;
            }

            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(LINE_HEIGHT);
            rowConstraints.setMaxHeight(LINE_HEIGHT);
            memoryGrid.getRowConstraints().add(i, rowConstraints);
        }

        memoryPage.setPageCount(PAGE_NUMBER);
        memoryPage.currentPageIndexProperty().addListener(((obs, oldVal, newVal) -> {
            memoryIndex = newVal.intValue();
            if (newVal.intValue() != oldVal.intValue()) application.executionWorker.updateGUI();
        }));

        memoryScrollBar.minProperty().bind(memoryScroll.vminProperty());
        memoryScrollBar.maxProperty().bind(memoryScroll.vmaxProperty());
        memoryScrollBar.visibleAmountProperty().bind(memoryScroll.heightProperty().divide(memoryPane.heightProperty()));
        memoryScroll.vvalueProperty().bindBidirectional(memoryScrollBar.valueProperty());
        memoryScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        stack = new ArrayList<>();

        editorManager.init(application);
        onNewFile();
    }

    @FXML
    public void onNewFile() {
        editorManager.newFile();
        application.sourceParser.setSourceScanner(new SourceScanner(codeArea.getText()));
        Platform.runLater(() -> {
            application.setTitle("New File");
            application.setUnsaved();
        });
        onStop();
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
        onStop();
    }

    @FXML
    public void onExit() {
        Platform.exit();
    }

    @FXML
    public void onSimulate() {
        simulate.setDisable(true);
        clearNotifs();
        application.executionWorker.revive();
        application.executionWorker.prepare();
    }

    public void launchSimulation(AssemblyError[] errors) {
        if (errors.length == 0 && application.codeInterpreter.getInstructionCount() != 0) {
            application.controller.editorManager.clearExecutedLines();
            codeArea.setDisable(true);
            stepInto.setDisable(false);
            stepOver.setDisable(false);
            conti.setDisable(false);
            pause.setDisable(true);
            stop.setDisable(false);
            restart.setDisable(false);
            reset.setDisable(false);
            application.status = Status.SIMULATING;
        } else {
            simulate.setDisable(false);
            if (application.codeInterpreter.getInstructionCount() == 0) {
                addNotif("Parsing error: ", "No instructions detected", "danger");
            }
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
        application.status = Status.EDITING;
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

    /**
     * Met à jour le GUI avec les informations du conteneur d'état.
     *
     * @apiNote Attention, ne pas exécuter sur l'Application Thread (pour des raisons de performances)
     * @param stateContainer le conteneur d'état
     */
    public void updateGUI(StateContainer stateContainer) {
        memoryPage.setDisable(true);

        for (int i = 0; i < 16; i++) {
            registers[i].setText(String.format(HEX_FORMAT, stateContainer.registers[i].getData()).toUpperCase());
        }

        CPSR.setText(String.format(HEX_FORMAT, stateContainer.cpsr.getData()).toUpperCase());
        CPSRT.setText(stateContainer.cpsr.toString());
        SPSR.setText(String.format(HEX_FORMAT, stateContainer.spsr.getData()).toUpperCase());
        SPSRT.setText(stateContainer.spsr.toString());

        for (int i = 0; i < LINES_PER_PAGE; i++) {
            int add = (memoryIndex * LINES_PER_PAGE + i) * ADDRESS_PER_LINE;

            byte byte0 = stateContainer.memory.get(add);
            byte byte1 = stateContainer.memory.get(add + 1);
            byte byte2 = stateContainer.memory.get(add + 2);
            byte byte3 = stateContainer.memory.get(add + 3);

            memory[i][0].setText(String.format(HEX_FORMAT, add).toUpperCase());
            memory[i][1].setText(String.format(HEX_FORMAT, MathUtils.toInt(byte0, byte1, byte2, byte3)).toUpperCase());
            memory[i][2].setText(String.format(DEC_FORMAT, MathUtils.toInt(byte0, byte1, byte2, byte3)));
            memory[i][3].setText(MathUtils.toBinString(byte0));
            memory[i][4].setText(MathUtils.toBinString(byte1));
            memory[i][5].setText(MathUtils.toBinString(byte2));
            memory[i][6].setText(MathUtils.toBinString(byte3));
        }

        memoryPage.setDisable(false);
    }

    /**
     * Ajoute une notification sur l'éditeur (5 maximums).
     *
     * @param titleString le titre (en gras)
     * @param contentString le corps du message
     * @param classString la classe à utiliser (Classes de BootstrapFX)
     */
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

    /**
     * Affiche une notification relative à une AssemblyError.
     *
     * @param error l'erreur en question
     */
    public void addError(AssemblyError error) {
        addNotif(error.getException().getTitle(), " " + error.getException().getMessage() + " at line " + error.getLine(), "danger");
        logger.log(Level.INFO, ExceptionUtils.getStackTrace(error.getException()));
    }

    /**
     * Supprime les notifications
     */
    @FXML
    public void clearNotifs() {
        notif.getChildren().clear();
    }
}