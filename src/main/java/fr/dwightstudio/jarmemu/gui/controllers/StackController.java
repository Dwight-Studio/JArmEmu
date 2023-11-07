package fr.dwightstudio.jarmemu.gui.controllers;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import javafx.application.Platform;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public class StackController extends AbstractJArmEmuModule {

    protected static final String HEX_FORMAT = "%08x";

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
        for (Map.Entry<Integer, Integer> entry : stack.entrySet()) {
            if (stackTexts.size() > i) {
                update(entry, i);
            } else {
                stackTexts.add(create(entry, i));
            }
            i++;
        }

        for (int j = i; j < stackTexts.size(); j++) {
            Text[] texts = stackTexts.remove(i);

            Platform.runLater(() -> getController().stackGrid.getChildren().remove(texts[0]));
            Platform.runLater(() -> getController().stackGrid.getChildren().remove(texts[1]));
        }
    }

    private HashMap<Integer, Integer> getLowerValues(StateContainer container) {
        HashMap<Integer, Integer> rtn = new HashMap<>();
        int address = container.getStackAddress() - 4;

        while (container.memory.isWordInitiated(address)) {
            rtn.put(address, container.memory.getWord(address));
            address -= 4;
        }

        return rtn;
    }

    private HashMap<Integer, Integer> getHigherValues(StateContainer container) {
        HashMap<Integer, Integer> rtn = new HashMap<>();
        int address = container.getStackAddress();

        while (container.memory.isWordInitiated(address)) {
            rtn.put(address, container.memory.getWord(address));
            address += 4;
        }

        return rtn;
    }

    private Text[] create(Map.Entry<Integer, Integer> entry, int line) {
        Text[] texts = new Text[2];

        Text address = new Text(String.format(HEX_FORMAT, entry.getKey()).toUpperCase());
        address.getStyleClass().add("reg-address");
        Platform.runLater(() -> getController().stackGrid.add(address, 0, line));
        texts[0] = address;

        Text value = new Text(String.format(HEX_FORMAT, entry.getValue()).toUpperCase());
        value.getStyleClass().add("reg-data");
        Platform.runLater(() -> getController().stackGrid.add(value, 1, line));
        texts[1] = value;

        return texts;
    }

    private void update(Map.Entry<Integer, Integer> entry, int line) {
        Text[] texts = stackTexts.get(line);

        texts[0].setText(String.format(HEX_FORMAT, entry.getKey()).toUpperCase());
        texts[1].setText(String.format(HEX_FORMAT, entry.getValue()).toUpperCase());
    }
}
