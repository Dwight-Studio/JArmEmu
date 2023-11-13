package fr.dwightstudio.jarmemu.gui.controllers;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.view.RegisterView;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.util.ValueHexStringConverter;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class RegistersController extends AbstractJArmEmuModule {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private int dataFormat;
    private TableColumn<RegisterView, String> col0;
    private TableColumn<RegisterView, Number> col1;
    private TableColumn<RegisterView, String> col2;
    private ObservableList<RegisterView> views;

    public RegistersController(JArmEmuApplication application) {
        super(application);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        col0 = new TableColumn<>("Register");
        col0.setSortable(false);
        col0.setEditable(false);
        col0.setReorderable(false);
        col0.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col0.setCellValueFactory(c -> c.getValue().getNameProperty());
        col0.setCellFactory(TextFieldTableCell.forTableColumn());

        col1 = new TableColumn<>("Value");
        col1.setSortable(false);
        col1.setReorderable(false);
        col1.setMinWidth(100);
        col1.getStyleClass().addAll(Tweaks.ALIGN_CENTER, "reg-data");
        col1.setCellValueFactory(c -> c.getValue().getValueProperty());
        col1.setCellFactory(TextFieldTableCell.forTableColumn(new ValueHexStringConverter(this::format)));

        col2 = new TableColumn<>("Flags");
        col2.setSortable(false);
        col2.setEditable(false);
        col2.setReorderable(false);
        col2.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col2.setCellValueFactory(c -> c.getValue().getFlagsProperty());
        col2.setCellFactory(TextFieldTableCell.forTableColumn());

        TableView<RegisterView> registersTable = new TableView<>();
        views = registersTable.getItems();
        registersTable.getColumns().setAll(col0, col1, col2);
        registersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        registersTable.getSelectionModel().selectFirst();

        registersTable.getStyleClass().addAll(Styles.STRIPED, Styles.DENSE);
        registersTable.setEditable(true);

        getController().registersTab.setContent(registersTable);
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
            views.clear();
            views.add(new RegisterView(stateContainer.registers[0], "R0"));
            views.add(new RegisterView(stateContainer.registers[1], "R1"));
            views.add(new RegisterView(stateContainer.registers[2], "R2"));
            views.add(new RegisterView(stateContainer.registers[3], "R3"));
            views.add(new RegisterView(stateContainer.registers[4], "R4"));
            views.add(new RegisterView(stateContainer.registers[5], "R5"));
            views.add(new RegisterView(stateContainer.registers[6], "R6"));
            views.add(new RegisterView(stateContainer.registers[7], "R7"));
            views.add(new RegisterView(stateContainer.registers[8], "R8"));
            views.add(new RegisterView(stateContainer.registers[9], "R9"));
            views.add(new RegisterView(stateContainer.registers[10], "R10"));
            views.add(new RegisterView(stateContainer.registers[11], "R11 (FP)"));
            views.add(new RegisterView(stateContainer.registers[12], "R12 (IP)"));
            views.add(new RegisterView(stateContainer.registers[13], "R13 (SP)"));
            views.add(new RegisterView(stateContainer.registers[14], "R14 (LR)"));
            views.add(new RegisterView(stateContainer.registers[15], "R15 (PC)"));
            views.add(new RegisterView(stateContainer.cpsr, "CPSR"));
            views.add(new RegisterView(stateContainer.spsr, "SPSR"));
        }
    }

    private String format(int i) {
        return getApplication().getFormattedData(i, dataFormat);
    }
}
