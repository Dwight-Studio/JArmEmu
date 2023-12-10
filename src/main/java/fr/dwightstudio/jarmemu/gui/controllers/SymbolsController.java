/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2023 Dwight Studio
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
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import fr.dwightstudio.jarmemu.util.converters.FileNameStringConverter;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.converter.NumberStringConverter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class SymbolsController extends AbstractJArmEmuModule {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private TableColumn<SymbolView, Number> col0;
    private TableColumn<SymbolView, String> col1;
    private TableColumn<SymbolView, Number> col2;
    private ObservableList<SymbolView> views;
    private TableView<SymbolView> symbolTable;


    public SymbolsController(JArmEmuApplication application) {
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

        col2 = new TableColumn<>("Value");
        col2.setGraphic(new FontIcon(Material2OutlinedMZ.MONEY));
        col2.setEditable(false);
        col2.setReorderable(false);
        col2.setSortable(false);
        col2.setMinWidth(80);
        col2.setPrefWidth(80);
        col2.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col2.setCellValueFactory(c -> c.getValue().getValueProperty());
        col2.setCellFactory(ValueTableCell.factoryDynamicFormat(application));

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

        AnchorPane.setTopAnchor(symbolTable, 0d);
        AnchorPane.setRightAnchor(symbolTable, 0d);
        AnchorPane.setBottomAnchor(symbolTable, 0d);
        AnchorPane.setLeftAnchor(symbolTable, 0d);

        getController().symbolsPane.getChildren().add(symbolTable);
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

            for (int i = 0 ; i < stateContainer.getLabelsInFiles().size() ; i++) {
                stateContainer.setFileIndex(i);
                for (Map.Entry<String, Integer> entry : stateContainer.getAccessibleConsts().entrySet()) {
                    views.add(new SymbolView(entry,  stateContainer.getCurrentFileIndex()));
                }

                for (Map.Entry<String, Integer> entry : stateContainer.getAccessibleData().entrySet()) {
                    views.add(new SymbolView(entry,  stateContainer.getCurrentFileIndex()));
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
