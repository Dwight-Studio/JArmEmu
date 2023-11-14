package fr.dwightstudio.jarmemu.gui.controllers;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.factory.AddressTableCell;
import fr.dwightstudio.jarmemu.gui.factory.CursorTableCell;
import fr.dwightstudio.jarmemu.gui.factory.ValueTableCell;
import fr.dwightstudio.jarmemu.gui.view.MemoryWordView;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.util.RegisterUtils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class StackController extends AbstractJArmEmuModule {

    private static final int MAX_NUMBER = 500;

    private final Logger logger = Logger.getLogger(getClass().getName());
    private TableColumn<MemoryWordView, Boolean> col0;
    private TableColumn<MemoryWordView, Number> col1;
    private TableColumn<MemoryWordView, Number> col2;
    private ObservableList<MemoryWordView> views;
    private TableView<MemoryWordView> stackTable;


    public StackController(JArmEmuApplication application) {
        super(application);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        col0 = new TableColumn<>();
        col0.setGraphic(new FontIcon(Material2OutlinedAL.LOCATION_SEARCHING));
        col0.setSortable(false);
        col0.setEditable(false);
        col0.setReorderable(false);
        col0.setMaxWidth(35);
        col0.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col0.setCellValueFactory(c -> c.getValue().getCursorProperty());
        col0.setCellFactory(CursorTableCell.factory());

        col1 = new TableColumn<>("Address");
        col1.setGraphic(new FontIcon(Material2OutlinedAL.ALTERNATE_EMAIL));
        col1.setSortable(false);
        col0.setEditable(false);
        col1.setReorderable(false);
        col1.setMinWidth(80);
        col1.setPrefWidth(80);
        col1.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col1.setCellValueFactory(c -> c.getValue().getAddressProperty());
        col1.setCellFactory(AddressTableCell.factory());

        col2 = new TableColumn<>("Value");
        col2.setGraphic(new FontIcon(Material2OutlinedMZ.MONEY));
        col2.setSortable(false);
        col2.setReorderable(false);
        col2.setMinWidth(80);
        col2.setPrefWidth(80);
        col2.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col2.setCellValueFactory(c -> c.getValue().getValueProperty());
        col2.setCellFactory(ValueTableCell.factoryDynamicFormat(application));

        stackTable = new TableView<>();
        views = stackTable.getItems();

        FontIcon icon = new FontIcon(Material2OutlinedAL.AUTORENEW);
        HBox placeHolder = new HBox(5, icon);

        icon.getStyleClass().add("medium-icon");
        placeHolder.setAlignment(Pos.CENTER);
        stackTable.setPlaceholder(placeHolder);

        // TODO: Corriger le problème du trie du stack

        stackTable.getColumns().setAll(col0, col1, col2);
        stackTable.getSortOrder().clear();
        stackTable.getSortOrder().add(col1);
        stackTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        stackTable.getStyleClass().addAll(Styles.STRIPED, Styles.DENSE, Tweaks.ALIGN_CENTER, Tweaks.EDGE_TO_EDGE);
        stackTable.getSelectionModel().selectFirst();
        stackTable.setEditable(true);

        getController().stackTab.setContent(stackTable);
    }

    private ArrayList<Integer> getLowerValues(StateContainer container) {
        ArrayList<Integer> rtn = new ArrayList<>();
        int address = container.getStackAddress() - 4;
        int sp = container.registers[RegisterUtils.SP.getN()].getData();

        int number = 0;
        while (container.memory.isWordInitiated(address) || (sp < container.getStackAddress() && address >= sp)) {
            if (number > MAX_NUMBER) {
                break;
            }
            rtn.add(address);
            address -= 4;
            number++;
        }

        return rtn;
    }

    private ArrayList<Integer> getHigherValues(StateContainer container) {
        ArrayList<Integer> rtn = new ArrayList<>();
        int address = container.getStackAddress();
        int sp = container.registers[RegisterUtils.SP.getN()].getData();

        int number = 0;
        while (container.memory.isWordInitiated(address) || (sp >= container.getStackAddress() && address <= sp)) {
            if (number > MAX_NUMBER) {
                break;
            }
            rtn.add(address);
            address += 4;
            number++;
        }

        return rtn;
    }

    /**
     * Met à jour les registres sur le GUI avec les informations du conteneur d'état.
     *
     * @apiNote Attention, ne pas exécuter sur l'Application Thread (pour des raisons de performances)
     * @param stateContainer le conteneur d'état
     */
    public void updateGUI(StateContainer stateContainer) {
        if (stateContainer == null) {
            views.clear();
        } else {
            Register sp = stateContainer.registers[RegisterUtils.SP.getN()];

            ArrayList<Integer> stackValues = getLowerValues(stateContainer);
            stackValues.addAll(getHigherValues(stateContainer));

            views.removeIf(view -> !stackValues.contains(view.getAddressProperty().get()));
            views.forEach(view -> stackValues.remove((Integer) view.getAddressProperty().get()));

            for (int address : stackValues) {
                views.add(new MemoryWordView(stateContainer.memory, address, sp));
            }

            AtomicInteger i = new AtomicInteger(0);

            views.forEach(views -> {
                if (views.getCursorProperty().get()) {
                    int finalI = i.get();
                    Platform.runLater(() -> {
                        stackTable.scrollTo(finalI);
                        stackTable.getFocusModel().focus(finalI);
                    });
                }
                i.getAndIncrement();
            });
        }
    }
}
