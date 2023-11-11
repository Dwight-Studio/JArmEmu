package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.util.MathUtils;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MemoryController extends AbstractJArmEmuModule {

    protected static final String HEX_FORMAT = "%08x";
    protected int DATA_FORMAT;

    protected static final int LINES_PER_PAGE = 512;
    protected static final int ADDRESS_PER_LINE = 4;
    protected static final int ADDRESS_PER_PAGE = LINES_PER_PAGE * ADDRESS_PER_LINE;
    protected static final int PAGE_NUMBER = (int) (((long) Math.pow(2L, 32L)) / ADDRESS_PER_PAGE);
    protected static final int PAGE_OFFSET = PAGE_NUMBER/2;
    protected static final int LINE_HEIGHT = 20;

    private final Logger logger = Logger.getLogger(getClass().getName());
    private StringProperty[][] memoryStrings;

    public MemoryController(JArmEmuApplication application) {
        super(application);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        memoryStrings = new StringProperty[LINES_PER_PAGE][6];

        for (int i = 0; i < LINES_PER_PAGE; i++) {

            for (int j = 0; j < 6; j++) {
                Text node = new Text();
                StringProperty stringProperty = new SimpleStringProperty();

                node.getStyleClass().add(j == 0 ? "reg-address" : "reg-data");
                node.textProperty().bind(stringProperty);
                node.setTextAlignment(TextAlignment.CENTER);
                getController().memoryGrid.add(node, j, i);

                stringProperty.set("-");

                memoryStrings[i][j] = stringProperty;
            }

            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(LINE_HEIGHT);
            rowConstraints.setMaxHeight(LINE_HEIGHT);
            getController().memoryGrid.getRowConstraints().add(i, rowConstraints);
        }

        // Configuration du sélecteur de pages
        getController().memoryPage.setPageCount(PAGE_NUMBER);
        getController().memoryPage.setCurrentPageIndex(PAGE_OFFSET);
        getController().memoryPage.currentPageIndexProperty().addListener((observableValue, number, t1) -> {
            if (number.intValue() != t1.intValue()) {
                getExecutionWorker().updateGUI();
            }
        });

        // Relier la barre virtuelle à la barre du ScrollPane
        getController().memoryScrollBar.minProperty().bind(getController().memoryScroll.vminProperty());
        getController().memoryScrollBar.maxProperty().bind(getController().memoryScroll.vmaxProperty());
        getController().memoryScrollBar.visibleAmountProperty().bind(getController().memoryScroll.heightProperty().divide(getController().memoryPane.heightProperty()));
        getController().memoryScroll.vvalueProperty().bindBidirectional(getController().memoryScrollBar.valueProperty());
        getController().memoryScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        final StateContainer container = new StateContainer();

        getController().addressField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                try {
                    int add = container.evalWithConsts(getController().addressField.getText().toUpperCase());
                    int page = Math.floorDiv(add, ADDRESS_PER_PAGE) + PAGE_OFFSET;
                    getController().memoryPage.setCurrentPageIndex(page);
                    // TODO: Ajouter le scroll automatique vers la bonne adresse (s'inspirer de ce qui est fait avec le stack)
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
        DATA_FORMAT = getSettingsController().getDataFormat();

        for (int i = 0; i < LINES_PER_PAGE; i++) {
            int add = ((getController().memoryPage.getCurrentPageIndex() - PAGE_OFFSET) * LINES_PER_PAGE + i) * ADDRESS_PER_LINE;

            memoryStrings[i][0].set(String.format(HEX_FORMAT, add).toUpperCase());

            if (stateContainer != null) {
                byte byte3 = stateContainer.memory.getByte(add);
                byte byte2 = stateContainer.memory.getByte(add + 1);
                byte byte1 = stateContainer.memory.getByte(add + 2);
                byte byte0 = stateContainer.memory.getByte(add + 3);

                memoryStrings[i][1].set(getApplication().getFormattedData(MathUtils.toWord(byte3, byte2, byte1, byte0), DATA_FORMAT).toUpperCase());
                memoryStrings[i][2].set(MathUtils.toBinString(byte3));
                memoryStrings[i][3].set(MathUtils.toBinString(byte2));
                memoryStrings[i][4].set(MathUtils.toBinString(byte1));
                memoryStrings[i][5].set(MathUtils.toBinString(byte0));
            }
        }
    }
}
