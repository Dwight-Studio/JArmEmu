package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.JArmEmuApplication;
import fr.dwightstudio.jarmemu.asm.args.ArgumentParsers;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.util.MathUtils;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MemoryController extends AbstractJArmEmuModule {

    protected static final String DATA_FORMAT = "%08x";

    protected static final int LINES_PER_PAGE = 512;
    protected static final int ADDRESS_PER_LINE = 4;
    protected static final int ADDRESS_PER_PAGE = LINES_PER_PAGE * ADDRESS_PER_LINE;
    protected static final int PAGE_NUMBER = (int) (((long) Math.pow(2L, 32L)) / ADDRESS_PER_PAGE);
    protected static final int PAGE_OFFSET = PAGE_NUMBER/2;
    protected static final int LINE_HEIGHT = 20;

    private final Logger logger = Logger.getLogger(getClass().getName());
    private Text[][] memory;

    public MemoryController(JArmEmuApplication application) {
        super(application);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        memory = new Text[LINES_PER_PAGE][7];

        for (int i = 0; i < LINES_PER_PAGE; i++) {

            for (int j = 0; j < 7; j++) {
                Text node = new Text("00000000");
                node.getStyleClass().add(j == 0 ? "reg-address" : "reg-data");
                getController().memoryGrid.add(node, j, i);
                memory[i][j] = node;
            }

            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(LINE_HEIGHT);
            rowConstraints.setMaxHeight(LINE_HEIGHT);
            getController().memoryGrid.getRowConstraints().add(i, rowConstraints);
        }

        getController().memoryPage.setPageCount(PAGE_NUMBER);
        getController().memoryPage.setCurrentPageIndex(PAGE_OFFSET);
        getController().memoryPage.currentPageIndexProperty().addListener((observableValue, number, t1) -> {
            if (number.intValue() != t1.intValue()) {
                getExecutionWorker().updateGUI();
            }
        });

        getController().memoryScrollBar.minProperty().bind(getController().memoryScroll.vminProperty());
        getController().memoryScrollBar.maxProperty().bind(getController().memoryScroll.vmaxProperty());
        getController().memoryScrollBar.visibleAmountProperty().bind(getController().memoryScroll.heightProperty().divide(getController().memoryPane.heightProperty()));
        getController().memoryScroll.vvalueProperty().bindBidirectional(getController().memoryScrollBar.valueProperty());
        getController().memoryScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        getController().addressField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                try {
                    int add = ArgumentParsers.VALUE_12.generalParse(null, getController().addressField.getText().toUpperCase());
                    int page = Math.floorDiv(add, ADDRESS_PER_PAGE) + PAGE_OFFSET;
                    getController().memoryPage.setCurrentPageIndex(page);
                } catch (Exception ignored) {}
            }
        });
    }

    /**
     * Met à jour la mémoire sur le GUI avec les informations du conteneur d'état.
     *
     * @apiNote Attention, ne pas exécuter sur l'Application Thread (pour des raisons de performances)
     * @param stateContainer le conteneur d'état
     */
    public void updateGUI(StateContainer stateContainer) {
        for (int i = 0; i < LINES_PER_PAGE; i++) {
            int add = ((getController().memoryPage.getCurrentPageIndex() - PAGE_OFFSET) * LINES_PER_PAGE + i) * ADDRESS_PER_LINE;

            memory[i][0].setText(String.format(DATA_FORMAT, add).toUpperCase());

            if (stateContainer != null) {
                byte byte3 = stateContainer.memory.get(add);
                byte byte2 = stateContainer.memory.get(add + 1);
                byte byte1 = stateContainer.memory.get(add + 2);
                byte byte0 = stateContainer.memory.get(add + 3);

                memory[i][1].setText(String.format(DATA_FORMAT, MathUtils.toInt(byte3, byte2, byte1, byte0)).toUpperCase());
                memory[i][2].setText(String.format(DATA_FORMAT, MathUtils.toInt(byte3, byte2, byte1, byte0)));
                memory[i][3].setText(MathUtils.toBinString(byte3));
                memory[i][4].setText(MathUtils.toBinString(byte2));
                memory[i][5].setText(MathUtils.toBinString(byte1));
                memory[i][6].setText(MathUtils.toBinString(byte0));
            }
        }
    }
}
