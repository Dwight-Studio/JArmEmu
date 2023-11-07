package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.util.RegisterUtils;
import javafx.application.Platform;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.lang.reflect.Executable;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public class StackController extends AbstractJArmEmuModule {

    protected static final String HEX_FORMAT = "%08x";
    private static final Background DEFAULT_BACKGROUND =  new Background(new BackgroundFill(Color.TRANSPARENT, null, null));
    private static final Background POINTED_BACKGROUND =  new Background(new BackgroundFill(Color.web("#8aeaff"), null, null));
    private static final int MAX_NUMBER = 100;

    private final Logger logger = Logger.getLogger(getClass().getName());
    protected ArrayList<Text[]> stackTexts;
    public StackController(JArmEmuApplication application) {
        super(application);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stackTexts = new ArrayList<>();
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

        stack.putAll(getLowerValues(stateContainer));
        stack.putAll(getHigherValues(stateContainer));

        int i = 0;
        int sp = stateContainer.registers[RegisterUtils.SP.getN()].getData();
        for (Map.Entry<Integer, Integer> entry : stack.entrySet()) {
            if (stackTexts.size() > i) {
                update(entry, i, sp);
            } else {
                create(entry, i, sp);
            }
            i++;
        }

        int s = stackTexts.size();
        for (int j = i; j < s; j++) {
            Text[] texts = stackTexts.remove(i);

            Platform.runLater(() -> {
                getController().stackGrid.getChildren().remove(texts[0]);
                getController().stackGrid.getChildren().remove(texts[1]);
                getController().stackGrid.getChildren().remove(texts[2]);
            });
        }
    }

    private HashMap<Integer, Integer> getLowerValues(StateContainer container) {
        HashMap<Integer, Integer> rtn = new HashMap<>();
        int address = container.getStackAddress() - 4;
        int sp = container.registers[RegisterUtils.SP.getN()].getData();

        int number = 0;
        while (container.memory.isWordInitiated(address) || (sp < container.getStackAddress() && address >= sp)) {
            if (number > MAX_NUMBER) break;
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
            if (number > MAX_NUMBER) break;
            rtn.put(address, container.memory.getWord(address));
            address += 4;
            number++;
        }

        return rtn;
    }

    private void create(Map.Entry<Integer, Integer> entry, int line, int sp) {
        Text[] texts = new Text[3];

        Text indicator = new Text(entry.getKey().equals(sp) ? "➤" : "");
        indicator.getStyleClass().add("reg-data");
        indicator.setTextAlignment(TextAlignment.CENTER);
        Platform.runLater(() -> getController().stackGrid.add(indicator, 0, line));
        texts[0] = indicator;

        Text address = new Text(String.format(HEX_FORMAT, entry.getKey()).toUpperCase());
        address.getStyleClass().add("reg-address");
        address.setTextAlignment(TextAlignment.CENTER);
        Platform.runLater(() -> getController().stackGrid.add(address, 1, line));
        texts[1] = address;

        Text value = new Text(String.format(HEX_FORMAT, entry.getValue()).toUpperCase());
        value.getStyleClass().add("reg-data");
        value.setTextAlignment(TextAlignment.CENTER);
        Platform.runLater(() -> getController().stackGrid.add(value, 2, line));
        texts[2] = value;

        stackTexts.add(texts);
    }

    private void update(Map.Entry<Integer, Integer> entry, int line, int sp) {
        Text[] texts = stackTexts.get(line);

        texts[0].setText(entry.getKey().equals(sp) ? "➤" : "");
        texts[1].setText(String.format(HEX_FORMAT, entry.getKey()).toUpperCase());
        texts[2].setText(String.format(HEX_FORMAT, entry.getValue()).toUpperCase());
    }

    public void clear() {
        int s = stackTexts.size();
        for (int j = 0; j < s; j++) {
            Text[] texts = stackTexts.remove(0);

            Platform.runLater(() -> {
                getController().stackGrid.getChildren().remove(texts[0]);
                getController().stackGrid.getChildren().remove(texts[1]);
                getController().stackGrid.getChildren().remove(texts[2]);
            });
        }
    }
}
