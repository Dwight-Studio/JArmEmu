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
import fr.dwightstudio.jarmemu.gui.factory.ValueTableCell;
import fr.dwightstudio.jarmemu.gui.view.SymbolView;
import fr.dwightstudio.jarmemu.sim.obj.FilePos;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.util.converters.FileNameStringConverter;
import javafx.application.Platform;
import javafx.collections.ObservableList;
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

public class LabelsController extends AbstractJArmEmuModule {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private TableColumn<SymbolView, Number> col0;
    private TableColumn<SymbolView, String> col1;
    private TableColumn<SymbolView, Number> col2;
    private ObservableList<SymbolView> views;
    private TableView<SymbolView> labelTable;


    public LabelsController(JArmEmuApplication application) {
        super(application);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        col0 = new TableColumn<>("File");
        col0.setGraphic(new FontIcon(Material2OutlinedAL.INSERT_DRIVE_FILE));
        col0.setSortable(false);
        col0.setEditable(false);
        col0.setReorderable(false);
        col0.setMinWidth(80);
        col0.setPrefWidth(80);
        col0.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col0.setCellValueFactory(c -> c.getValue().getFileIndexProperty());
        col0.setCellFactory(TextFieldTableCell.forTableColumn(new FileNameStringConverter(getApplication())));

        col1 = new TableColumn<>("Name");
        col1.setGraphic(new FontIcon(Material2OutlinedAL.LABEL));
        col1.setEditable(false);
        col1.setReorderable(false);
        col1.setSortable(false);
        col1.setMinWidth(80);
        col1.setPrefWidth(80);
        col1.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col1.setCellValueFactory(c -> c.getValue().getNameProperty());
        col1.setCellFactory(TextFieldTableCell.forTableColumn());
        col1.setSortType(TableColumn.SortType.ASCENDING);

        col2 = new TableColumn<>("Address");
        col2.setGraphic(new FontIcon(Material2OutlinedAL.ALTERNATE_EMAIL));
        col2.setEditable(false);
        col2.setReorderable(false);
        col2.setSortable(false);
        col2.setMinWidth(80);
        col2.setPrefWidth(80);
        col2.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col2.setCellValueFactory(c -> c.getValue().getValueProperty());
        col2.setCellFactory(ValueTableCell.factoryDynamicFormat(application));

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
                    FilePos pos = getCodeInterpreter().getLineNumber(address);
                    getEditorController().goTo(pos);
                }
            });

            return row;
        });

        AnchorPane.setTopAnchor(labelTable, 0d);
        AnchorPane.setRightAnchor(labelTable, 0d);
        AnchorPane.setBottomAnchor(labelTable, 0d);
        AnchorPane.setLeftAnchor(labelTable, 0d);

        getController().labelsPane.getChildren().add(labelTable);
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
