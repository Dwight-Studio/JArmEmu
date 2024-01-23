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

package fr.dwightstudio.jarmemu.gui.controllers;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import fr.dwightstudio.jarmemu.gui.AbstractJArmEmuModule;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.factory.AddressTableCell;
import fr.dwightstudio.jarmemu.gui.factory.CursorTableCell;
import fr.dwightstudio.jarmemu.gui.factory.ValueTableCell;
import fr.dwightstudio.jarmemu.gui.view.MemoryWordView;
import fr.dwightstudio.jarmemu.sim.obj.Register;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
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

        col1 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.stack.address"));
        col1.setGraphic(new FontIcon(Material2OutlinedAL.ALTERNATE_EMAIL));
        col0.setEditable(false);
        col1.setReorderable(false);
        col1.setMinWidth(80);
        col1.setPrefWidth(80);
        col1.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col1.setCellValueFactory(c -> c.getValue().getAddressProperty());
        col1.setCellFactory(AddressTableCell.factory());
        col1.setSortType(TableColumn.SortType.ASCENDING);

        col2 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.stack.value"));
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

        getController().stackPane.getChildren().add(stackTable);
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
     * Met à jour les registres sur le GUI avec les informations du conteneur d'état.
     *
     * @apiNote Attention, ne pas exécuter sur l'Application Thread (pour des raisons de performances)
     * @param stateContainer le conteneur d'état
     */
    public void updateGUI(StateContainer stateContainer) {
        if (stateContainer == null) {
            views.clear();
        } else {
            Register sp = stateContainer.getSP();

            ArrayList<Integer> stackValues = getLowerValues(stateContainer);
            stackValues.addAll(getHigherValues(stateContainer));
            stackValues.add(stateContainer.getStackAddress());

            views.removeIf(view -> !stackValues.contains(view.getAddressProperty().get()) || view.getSP() != stateContainer.getSP());
            views.forEach(view -> stackValues.remove((Integer) view.getAddressProperty().get()));

            for (int address : stackValues) {
                views.add(new MemoryWordView(stateContainer.getMemory(), address, sp));
            }

            Platform.runLater(() -> {
                col1.setSortable(true);
                stackTable.sort();
                col1.setSortable(false);

                if (getSettingsController().getFollowSPSetting()) {
                    for (int i = 0; i < views.size(); i++) {
                        if (views.get(i).getCursorProperty().get()) {
                            stackTable.scrollTo(i);
                            stackTable.getFocusModel().focus(i);
                            break;
                        }
                    }
                }
            });
        }
    }

    public void refresh() {
        stackTable.refresh();
    }
}
