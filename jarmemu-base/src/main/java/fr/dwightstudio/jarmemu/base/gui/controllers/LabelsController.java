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
import fr.dwightstudio.jarmemu.base.sim.entity.FilePos;
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

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class LabelsController implements Initializable {

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    private TableColumn<SymbolView, Number> col0;
    private TableColumn<SymbolView, String> col1;
    private TableColumn<SymbolView, Number> col2;
    private ObservableList<SymbolView> views;
    private TableView<SymbolView> labelTable;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        col0 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.labels.file"));
        TableViewUtils.setupColumn(col0, Material2OutlinedAL.INSERT_DRIVE_FILE, 80, false, true, true);
        col0.setCellValueFactory(c -> c.getValue().getFileIndexProperty());
        col0.setCellFactory(TextFieldTableCell.forTableColumn(new FileNameStringConverter()));

        col1 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.labels.name"));
        TableViewUtils.setupColumn(col1, Material2OutlinedAL.LABEL, 80, false, true, true);
        col1.setCellValueFactory(c -> c.getValue().getNameProperty());
        col1.setCellFactory(TextFieldTableCell.forTableColumn());
        col1.setSortType(TableColumn.SortType.ASCENDING);

        col2 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.labels.address"));
        TableViewUtils.setupColumn(col2, Material2OutlinedAL.ALTERNATE_EMAIL, 80, false, true, true);
        col2.setCellValueFactory(c -> c.getValue().getValueProperty());
        col2.setCellFactory(ValueTableCell.factoryDynamicFormat());

        labelTable = new TableView<>();
        views = labelTable.getItems();

        FontIcon icon = new FontIcon(Material2OutlinedAL.AUTORENEW);
        HBox placeHolder = new HBox(5, icon);

        icon.getStyleClass().add("medium-icon");
        placeHolder.setAlignment(Pos.CENTER);
        labelTable.setPlaceholder(placeHolder);

        labelTable.getColumns().setAll(col0, col1, col2);
        labelTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        labelTable.getStyleClass().addAll(Styles.STRIPED, Styles.DENSE, Tweaks.ALIGN_CENTER, Tweaks.EDGE_TO_EDGE);
        labelTable.getSelectionModel().selectFirst();
        labelTable.setEditable(true);
        labelTable.getSortOrder().clear();
        labelTable.getSortOrder().add(col0);

        labelTable.setRowFactory(table -> {
            TableRow<SymbolView> row = new TableRow<>();

            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2 && !row.isEmpty()) {
                    int address = row.getItem().getValueProperty().get();
                    FilePos pos = JArmEmuApplication.getCodeInterpreter().getLineNumber(address);
                    JArmEmuApplication.getEditorController().goTo(pos);
                }
            });

            return row;
        });

        AnchorPane.setTopAnchor(labelTable, 0d);
        AnchorPane.setRightAnchor(labelTable, 0d);
        AnchorPane.setBottomAnchor(labelTable, 0d);
        AnchorPane.setLeftAnchor(labelTable, 0d);

        JArmEmuApplication.getController().labelsPane.getChildren().add(labelTable);
    }

    /**
     * Met à jour les registres sur le GUI avec les informations du conteneur d'état.
     *
     * @apiNote Attention, ne pas exécuter sur l'Application Thread (pour des raisons de performances)
     * @param stateContainer le conteneur d'état
     */
    public void attach(StateContainer stateContainer) {
        views.clear();
        if (stateContainer != null) {
            for (Map.Entry<String, FilePos> entry : stateContainer.getAllLabels().entries()) {
                views.add(new SymbolView(entry));
            }

            Platform.runLater(() -> {
                col0.setSortable(true);
                labelTable.sort();
                col0.setSortable(false);
            });
        }
    }

    public void refresh() {
        labelTable.refresh();
    }
}
