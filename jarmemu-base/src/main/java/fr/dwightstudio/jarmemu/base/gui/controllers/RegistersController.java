/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2024 Dwight Studio
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.dwightstudio.jarmemu.base.gui.controllers;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.factory.FlagTableCell;
import fr.dwightstudio.jarmemu.base.gui.factory.ValueTableCell;
import fr.dwightstudio.jarmemu.base.gui.view.RegisterView;
import fr.dwightstudio.jarmemu.base.gui.view.UpdatableWrapper;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.base.util.TableViewUtils;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class RegistersController implements Initializable {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    private TableColumn<RegisterView, String> col0;
    private TableColumn<RegisterView, Number> col1;
    private TableColumn<RegisterView, Register> col2;
    private ObservableList<RegisterView> views;
    private TableView<RegisterView> registersTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        col0 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.registers.register"));
        TableViewUtils.setupColumn(col0, Material2OutlinedMZ.MEMORY, 80, false, true, false);
        col0.setCellValueFactory(c -> c.getValue().getNameProperty());
        col0.setCellFactory(TextFieldTableCell.forTableColumn());

        col1 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.registers.value"));
        TableViewUtils.setupColumn(col1, Material2OutlinedMZ.MONEY, 80, true, true, false);
        col1.setCellValueFactory(c -> c.getValue().getValueProperty());
        col1.setCellFactory(ValueTableCell.factoryDynamicFormat());

        col2 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.registers.flags"));
        TableViewUtils.setupColumn(col2, Material2OutlinedAL.FLAG, 80, false, true, false);
        col2.setCellValueFactory(c -> c.getValue().getRegisterObservable());
        col2.setCellFactory(FlagTableCell.factory());

        registersTable = new TableView<>();
        views = registersTable.getItems();

        FontIcon icon = new FontIcon(Material2OutlinedAL.AUTORENEW);
        HBox placeHolder = new HBox(5, icon);

        icon.getStyleClass().add("medium-icon");
        placeHolder.setAlignment(Pos.CENTER);
        registersTable.setPlaceholder(placeHolder);

        registersTable.getColumns().setAll(col0, col1, col2);
        registersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        registersTable.getStyleClass().addAll(Styles.STRIPED, Styles.DENSE, Tweaks.ALIGN_CENTER, Tweaks.EDGE_TO_EDGE);
        registersTable.getSelectionModel().selectFirst();
        registersTable.setEditable(true);

        AnchorPane.setTopAnchor(registersTable, 0d);
        AnchorPane.setRightAnchor(registersTable, 0d);
        AnchorPane.setBottomAnchor(registersTable, 0d);
        AnchorPane.setLeftAnchor(registersTable, 0d);
        JArmEmuApplication.getController().registersPane.getChildren().add(registersTable);
    }

    /**
     * Met à jour les registres sur le GUI avec les informations du conteneur d'état.
     *
     * @apiNote Attention, ne pas exécuter sur l'Application Thread (pour des raisons de performances)
     * @param stateContainer le conteneur d'état
     */
    public void attach(StateContainer stateContainer) {
        if (stateContainer == null) {
            views.clear();
        } else {
            views.clear();
            views.add(new RegisterView(stateContainer.getRegister(0), "R0"));
            views.add(new RegisterView(stateContainer.getRegister(1), "R1"));
            views.add(new RegisterView(stateContainer.getRegister(2), "R2"));
            views.add(new RegisterView(stateContainer.getRegister(3), "R3"));
            views.add(new RegisterView(stateContainer.getRegister(4), "R4"));
            views.add(new RegisterView(stateContainer.getRegister(5), "R5"));
            views.add(new RegisterView(stateContainer.getRegister(6), "R6"));
            views.add(new RegisterView(stateContainer.getRegister(7), "R7"));
            views.add(new RegisterView(stateContainer.getRegister(8), "R8"));
            views.add(new RegisterView(stateContainer.getRegister(9), "R9"));
            views.add(new RegisterView(stateContainer.getRegister(10), "R10"));
            views.add(new RegisterView(stateContainer.getRegister(11), "R11 (FP)"));
            views.add(new RegisterView(stateContainer.getRegister(12), "R12 (IP)"));
            views.add(new RegisterView(stateContainer.getRegister(13), "R13 (SP)"));
            views.add(new RegisterView(stateContainer.getLR(), "R14 (LR)"));
            views.add(new RegisterView(stateContainer.getPC(), "R15 (PC)"));
            views.add(new RegisterView(stateContainer.getCPSR(), "CPSR"));
            views.add(new RegisterView(stateContainer.getSPSR(), "SPSR"));
        }
    }

    public void refresh() {
        registersTable.refresh();
    }
}
