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
import fr.dwightstudio.jarmemu.base.gui.factory.ValueTableCell;
import fr.dwightstudio.jarmemu.base.gui.view.SymbolView;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.base.util.TableViewUtils;
import fr.dwightstudio.jarmemu.base.util.converters.FileNameStringConverter;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class SymbolsController implements Initializable {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());

    private TableColumn<SymbolView, Number> col0;
    private TableColumn<SymbolView, String> col1;
    private TableColumn<SymbolView, Number> col2;
    private ObservableList<SymbolView> views;
    private TableView<SymbolView> symbolTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        col0 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.symbols.file"));
        TableViewUtils.setupColumn(col0, Material2OutlinedAL.INSERT_DRIVE_FILE, 80, false, true, true);
        col0.setCellValueFactory(c -> c.getValue().getFileIndexProperty());
        col0.setCellFactory(TextFieldTableCell.forTableColumn(new FileNameStringConverter()));

        col1 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.symbols.name"));
        TableViewUtils.setupColumn(col1, Material2OutlinedAL.LABEL, 80, false, true, true);
        col1.setCellValueFactory(c -> c.getValue().getNameProperty());
        col1.setCellFactory(TextFieldTableCell.forTableColumn());
        col1.setSortType(TableColumn.SortType.ASCENDING);

        col2 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.symbols.value"));
        TableViewUtils.setupColumn(col2, Material2OutlinedMZ.MONEY, 80, false, true, true);
        col2.setCellValueFactory(c -> c.getValue().getValueProperty());
        col2.setCellFactory(ValueTableCell.factoryDynamicFormat());

        symbolTable = new TableView<>();
        views = symbolTable.getItems();

        FontIcon icon = new FontIcon(Material2OutlinedAL.AUTORENEW);
        HBox placeHolder = new HBox(5, icon);

        icon.getStyleClass().add("medium-icon");
        placeHolder.setAlignment(Pos.CENTER);
        symbolTable.setPlaceholder(placeHolder);

        symbolTable.getColumns().setAll(col0, col1, col2);
        symbolTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        symbolTable.getStyleClass().addAll(Styles.STRIPED, Styles.DENSE, Tweaks.ALIGN_CENTER, Tweaks.EDGE_TO_EDGE);
        symbolTable.getSelectionModel().selectFirst();
        symbolTable.setEditable(true);
        symbolTable.getSortOrder().clear();
        symbolTable.getSortOrder().add(col0);

        symbolTable.setRowFactory(table -> {
            TableRow<SymbolView> row = new TableRow<>();

            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2 && !row.isEmpty()) {
                    JArmEmuApplication.getController().getOpenedMemoryViewController().goTo(row.getItem().getValueProperty().get());
                }
            });

            return row;
        });

        AnchorPane.setTopAnchor(symbolTable, 0d);
        AnchorPane.setRightAnchor(symbolTable, 0d);
        AnchorPane.setBottomAnchor(symbolTable, 0d);
        AnchorPane.setLeftAnchor(symbolTable, 0d);

        JArmEmuApplication.getController().symbolsPane.getChildren().add(symbolTable);
    }

    /**
     * Update the values using the state container.
     *
     * @apiNote Do not execute it on the application thread (to save performance)
     * @param stateContainer the current state container
     */
    public void attach(StateContainer stateContainer) {
        views.clear();
        if (stateContainer != null) {

            for (int i = 0 ; i < stateContainer.getLabelsInFiles().size() ; i++) {
                stateContainer.getCurrentMemoryPos().setFileIndex(i);
                for (Map.Entry<String, Integer> entry : stateContainer.getAccessibleConsts().entrySet()) {
                    views.add(new SymbolView(entry,  stateContainer.getCurrentMemoryPos().getFileIndex()));
                }

                for (Map.Entry<String, Integer> entry : stateContainer.getAccessibleData().entrySet()) {
                    views.add(new SymbolView(entry,  stateContainer.getCurrentMemoryPos().getFileIndex()));
                }
            }

            Platform.runLater(() -> {
                col0.setSortable(true);
                symbolTable.sort();
                col0.setSortable(false);
            });
        }
    }

    public void refresh() {
        symbolTable.refresh();
    }
}
