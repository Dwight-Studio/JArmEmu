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
import fr.dwightstudio.jarmemu.base.gui.factory.AddressTableCell;
import fr.dwightstudio.jarmemu.base.gui.factory.CursorTableCell;
import fr.dwightstudio.jarmemu.base.gui.factory.ValueTableCell;
import fr.dwightstudio.jarmemu.base.gui.view.MemoryWordView;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.base.util.TableViewUtils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class StackController implements Initializable {

    private static final int MAX_NUMBER = 500;

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    private TableColumn<MemoryWordView, Boolean> col0;
    private TableColumn<MemoryWordView, Number> col1;
    private TableColumn<MemoryWordView, Number> col2;
    private ObservableList<MemoryWordView> views;
    private TableView<MemoryWordView> stackTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        col0 = new TableColumn<>();
        TableViewUtils.setupColumn(col0, Material2OutlinedAL.LOCATION_SEARCHING, 35, false, false, false);
        col0.setCellValueFactory(c -> c.getValue().getCursorProperty());
        col0.setCellFactory(CursorTableCell.factory());

        col1 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.stack.address"));
        TableViewUtils.setupColumn(col1, Material2OutlinedAL.ALTERNATE_EMAIL, 80, false, true, false);
        col1.setGraphic(new FontIcon(Material2OutlinedAL.ALTERNATE_EMAIL));
        col1.setCellValueFactory(c -> c.getValue().getAddressProperty());
        col1.setCellFactory(AddressTableCell.factory());
        col1.setSortType(TableColumn.SortType.ASCENDING);

        col2 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.stack.value"));
        TableViewUtils.setupColumn(col2, Material2OutlinedMZ.MONEY, 80, true, true, false);
        col2.setCellValueFactory(c -> c.getValue().getValueProperty());
        col2.setCellFactory(ValueTableCell.factoryDynamicFormat());

        stackTable = new TableView<>();
        views = stackTable.getItems();

        FontIcon icon = new FontIcon(Material2OutlinedAL.AUTORENEW);
        HBox placeHolder = new HBox(5, icon);

        icon.getStyleClass().add("medium-icon");
        placeHolder.setAlignment(Pos.CENTER);
        stackTable.setPlaceholder(placeHolder);

        stackTable.getColumns().setAll(col0, col1, col2);
        stackTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        stackTable.getStyleClass().addAll(Styles.STRIPED, Styles.DENSE, Tweaks.ALIGN_CENTER, Tweaks.EDGE_TO_EDGE);
        stackTable.getSelectionModel().selectFirst();
        stackTable.setEditable(true);
        stackTable.getSortOrder().clear();
        stackTable.getSortOrder().add(col1);

        AnchorPane.setTopAnchor(stackTable, 0d);
        AnchorPane.setRightAnchor(stackTable, 0d);
        AnchorPane.setBottomAnchor(stackTable, 0d);
        AnchorPane.setLeftAnchor(stackTable, 0d);

        JArmEmuApplication.getController().stackPane.getChildren().add(stackTable);
    }

    private ArrayList<Integer> getLowerValues(StateContainer container) {
        ArrayList<Integer> rtn = new ArrayList<>();
        int address = container.getStackAddress() - 4;
        int sp = container.getSP().getData();

        int number = 0;
        while (container.getMemory().isWordInitiated(address) || (sp < container.getStackAddress() && address >= sp)) {
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
        int sp = container.getSP().getData();

        int number = 0;
        while (container.getMemory().isWordInitiated(address) || (sp > container.getStackAddress() && address <= sp)) {
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
     * Update the values using the state container.
     *
     * @apiNote Do not execute it on the application thread (to save performance)
     * @param stateContainer the current state container
     */
    public void updateGUI(StateContainer stateContainer) {
        if (stateContainer == null) {
            views.clear();
        } else {
            Register sp = stateContainer.getSP();

            ArrayList<Integer> stackValues = getLowerValues(stateContainer);
            stackValues.addAll(getHigherValues(stateContainer));
            stackValues.add(stateContainer.getStackAddress());

            Platform.runLater(() -> {
                views.removeIf(view -> !stackValues.contains(view.getAddressProperty().get()) || view.getSP() != stateContainer.getSP());
                views.forEach(view -> stackValues.remove((Integer) view.getAddressProperty().get()));

                for (int address : stackValues) {
                    views.add(new MemoryWordView(stateContainer.getMemory(), address, sp));
                }

                col1.setSortable(true);
                stackTable.sort();
                col1.setSortable(false);

                if (JArmEmuApplication.getSettingsController().getFollowSPSetting()) {
                    for (int i = 0; i < views.size(); i++) {
                        if (views.get(i).getCursorProperty().get()) {
                            stackTable.scrollTo(i);
                            stackTable.getFocusModel().focus(i);
                            break;
                        }
                    }
                } else if (!stackTable.getSelectionModel().isEmpty()) {
                    int i = stackTable.getSelectionModel().getSelectedIndex();
                    stackTable.scrollTo(i);
                    stackTable.getFocusModel().focus(i);
                }
            });
        }
    }

    public void refresh() {
        stackTable.refresh();
    }
}
