package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.util.RegisterUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public class StackController extends AbstractJArmEmuModule {

    protected static final String HEX_FORMAT = "%08x";
    private static final int MAX_NUMBER = 500;
    private static final int ROW_HEIGHT = 20;

    protected int dataFormat;
    private int spDisplayer;

    private final Logger logger = Logger.getLogger(getClass().getName());
    protected ArrayList<Text[]> stackTexts;
    protected ArrayList<StringProperty[]> stackTextProperties;
    public StackController(JArmEmuApplication application) {
        super(application);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stackTexts = new ArrayList<>();
        stackTextProperties = new ArrayList<>();
    }

    /**
     * Met à jour les registres sur le GUI avec les informations du conteneur d'état.
     *
     * @apiNote Attention, ne pas exécuter sur l'Application Thread (pour des raisons de performances)
     * @param stateContainer le conteneur d'état
     */
    public void updateGUI(StateContainer stateContainer) {
        if (stateContainer == null) return;
        TreeMap<Integer, Integer> stack = new TreeMap<>();

        dataFormat = getSettingsController().getDataFormat();

        stack.putAll(getLowerValues(stateContainer));
        stack.putAll(getHigherValues(stateContainer));

        int i = 0;
        int sp = stateContainer.registers[RegisterUtils.SP.getN()].getData();
        spDisplayer = -1;
        for (Map.Entry<Integer, Integer> entry : stack.entrySet()) {
            boolean hasSp = entry.getKey().equals(sp);
            if (stackTexts.size() <= i) {
                create(entry, i, hasSp);
            }
            update(entry, i, hasSp);
            if (hasSp) spDisplayer = i;
            i++;
        }

        int s = stackTexts.size();
        for (int j = i; j < s; j++) {
            Text[] texts = stackTexts.remove(i);
            stackTextProperties.remove(i);

            Platform.runLater(() -> {
                getController().stackGrid.getChildren().remove(texts[0]);
                getController().stackGrid.getChildren().remove(texts[1]);
                getController().stackGrid.getChildren().remove(texts[2]);
            });
        }

        if (spDisplayer != -1) {
            Platform.runLater(() -> {
                try {
                    final double current = getController().stackScroll.getVvalue();

                    final double totalSize = getController().stackGrid.getLayoutBounds().getHeight();
                    final double viewSize = getController().stackScroll.getViewportBounds().getHeight();
                    //final double lineSize = getController().stackGrid.getChildren().getLast().getLayoutBounds().getHeight() + getController().stackGrid.getVgap();
                    final double lineSize = ROW_HEIGHT;
                    final double linePos = spDisplayer * lineSize;

                    final double currentViewTop = (totalSize - viewSize) * current;
                    final double currentViewBottom = currentViewTop + viewSize;

                    if (linePos < currentViewTop) {
                        getController().stackScroll.setVvalue(linePos / (totalSize - viewSize));
                    } else if ((linePos + lineSize) > currentViewBottom) {
                        getController().stackScroll.setVvalue((linePos - viewSize + lineSize) / (totalSize - viewSize));
                    }
                } catch (Exception e) {
                    logger.warning("Failed to calculate scroll value for StackScroll");
                }
            });
        }
    }

    private HashMap<Integer, Integer> getLowerValues(StateContainer container) {
        HashMap<Integer, Integer> rtn = new HashMap<>();
        int address = container.getStackAddress() - 4;
        int sp = container.registers[RegisterUtils.SP.getN()].getData();

        int number = 0;
        while (container.memory.isWordInitiated(address) || (sp < container.getStackAddress() && address >= sp)) {
            if (number > MAX_NUMBER) {
                rtn.put(address, null);
                break;
            }
            rtn.put(address, container.memory.getWord(address));
            address -= 4;
            number++;
        }

        return rtn;
    }

    private HashMap<Integer, Integer> getHigherValues(StateContainer container) {
        HashMap<Integer, Integer> rtn = new HashMap<>();
        int address = container.getStackAddress();
        int sp = container.registers[RegisterUtils.SP.getN()].getData();

        int number = 0;
        while (container.memory.isWordInitiated(address) || (sp >= container.getStackAddress() && address <= sp)) {
            if (number > MAX_NUMBER) {
                rtn.put(address, null);
                break;
            }
            rtn.put(address, container.memory.getWord(address));
            address += 4;
            number++;
        }

        return rtn;
    }

    private void create(Map.Entry<Integer, Integer> entry, int line, boolean sp) {
        Text[] texts = new Text[3];
        StringProperty[] stringProperties = new StringProperty[3];

        for (int i = 0 ; i < 3 ; i++) {
            Text text = new Text();
            StringProperty textProperty = new SimpleStringProperty();

            if (i == 1) {
                text.getStyleClass().add("reg-address");
            } else {
                text.getStyleClass().add("reg-data");
            }

            text.setTextAlignment(TextAlignment.CENTER);
            text.textProperty().bind(textProperty);

            texts[i] = text;
            stringProperties[i] = textProperty;
        }

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setMinHeight(ROW_HEIGHT);
        rowConstraints.setPrefHeight(ROW_HEIGHT);
        rowConstraints.setMaxHeight(ROW_HEIGHT);

        Platform.runLater(() -> {
            if (getController().stackGrid.getRowConstraints().size() < line) getController().stackGrid.getRowConstraints().add(rowConstraints);

            getController().stackGrid.add(texts[0], 0, line);
            getController().stackGrid.add(texts[1], 1, line);
            getController().stackGrid.add(texts[2], 2, line);
        });

        stackTexts.add(texts);
        stackTextProperties.add(stringProperties);
    }

    private void update(Map.Entry<Integer, Integer> entry, int line, boolean sp) {
        StringProperty[] property = stackTextProperties.get(line);

        property[0].set(sp ? "➤" : "");
        property[1].set(String.format(HEX_FORMAT, entry.getKey()).toUpperCase());

        if (entry.getValue() == null) {
            property[2].set("...");
        } else {
            property[2].set(getApplication().getFormattedData(entry.getValue(), dataFormat));
        }
    }

    public void clear() {
        int s = stackTexts.size();
        for (int j = 0; j < s; j++) {
            Text[] texts = stackTexts.remove(0);
            stackTextProperties.remove(0);

            Platform.runLater(() -> {
                getController().stackGrid.getChildren().remove(texts[0]);
                getController().stackGrid.getChildren().remove(texts[1]);
                getController().stackGrid.getChildren().remove(texts[2]);
            });
        }
    }
}
