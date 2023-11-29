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

import atlantafx.base.controls.Popover;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import fr.dwightstudio.jarmemu.gui.AbstractJArmEmuModule;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.factory.AddressTableCell;
import fr.dwightstudio.jarmemu.gui.factory.ValueTableCell;
import fr.dwightstudio.jarmemu.gui.view.MemoryWordView;
import fr.dwightstudio.jarmemu.sim.obj.StateContainer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MemoryController extends AbstractJArmEmuModule {

    protected static final int LINES_PER_PAGE = 512;
    protected static final int ADDRESS_PER_LINE = 4;
    protected static final int ADDRESS_PER_PAGE = LINES_PER_PAGE * ADDRESS_PER_LINE;
    protected static final int PAGE_NUMBER = (int) (((long) Math.pow(2L, 32L)) / ADDRESS_PER_PAGE);
    protected static final int PAGE_OFFSET = PAGE_NUMBER/2;

    private final Logger logger = Logger.getLogger(getClass().getName());
    private Popover hintPop;
    private TableColumn<MemoryWordView, Number> col0;
    private TableColumn<MemoryWordView, Number> col1;
    private TableColumn<MemoryWordView, Number> col2;
    private TableColumn<MemoryWordView, Number> col3;
    private TableColumn<MemoryWordView, Number> col4;
    private TableColumn<MemoryWordView, Number> col5;
    private TableColumn<MemoryWordView, Number> col6;
    private ObservableList<MemoryWordView> views;
    private TableView<MemoryWordView> memoryTable;
    private int lastPageIndex;
    private boolean doSearchQuery;
    private int searchQuery;

    public MemoryController(JArmEmuApplication application) {
        super(application);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        TextFlow textFlow = new TextFlow(
                new Text("You can search in memory using multiple address formats:\n"),
                new Text("\t→ Signed decimal number (e.g. -1842)\n"),
                new Text("\t→ Hexadecimal number (e.g. 0x01FF)\n"),
                new Text("\t→ Binary number (e.g. 0b1001)\n"),
                new Text("\t→ Defined constant (e.g N)\n"),
                new Text("\t→ Defined data (e.g. data1)\n"),
                new Text("\t→ Mathematical expression (e.g (N*4)+0xFF)")
        );
        textFlow.setLineSpacing(5);
        textFlow.setPrefWidth(400);
        textFlow.setPadding(new Insets(10, 0, 10, 0));

        hintPop = new Popover(textFlow);
        hintPop.setTitle("Memory searching");
        hintPop.setHeaderAlwaysVisible(true);
        hintPop.setDetachable(false);
        hintPop.setAnimated(true);
        hintPop.setCloseButtonEnabled(true);
        hintPop.setArrowLocation(Popover.ArrowLocation.RIGHT_CENTER);

        col0 = new TableColumn<>("Address");
        col0.setGraphic(new FontIcon(Material2OutlinedAL.ALTERNATE_EMAIL));
        col0.setSortable(false);
        col0.setEditable(false);
        col0.setReorderable(false);
        col0.setMinWidth(80);
        col0.setPrefWidth(80);
        col0.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col0.setCellValueFactory(c -> c.getValue().getAddressProperty());
        col0.setCellFactory(AddressTableCell.factory());

        col1 = new TableColumn<>("Value");
        col1.setGraphic(new FontIcon(Material2OutlinedMZ.MONEY));
        col1.setSortable(false);
        col1.setReorderable(false);
        col1.setMinWidth(80);
        col1.setPrefWidth(80);
        col1.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col1.setCellValueFactory(c -> c.getValue().getValueProperty());
        col1.setCellFactory(ValueTableCell.factoryDynamicFormat(application));

        col2 = new TableColumn<>("ASCII");
        col2.setGraphic(new FontIcon(Material2OutlinedMZ.MONEY));
        col2.setSortable(false);
        col2.setReorderable(false);
        col2.setMinWidth(80);
        col2.setPrefWidth(80);
        col2.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col2.setCellValueFactory(c -> c.getValue().getValueProperty());
        col2.setCellFactory(ValueTableCell.factoryStaticASCII());
        col2.setVisible(false);

        col3 = new TableColumn<>("Byte 3");
        col3.setGraphic(new FontIcon(Material2OutlinedAL.LOOKS_ONE));
        col3.setSortable(false);
        col3.setEditable(false);
        col3.setReorderable(false);
        col3.setMinWidth(80);
        col3.setPrefWidth(80);
        col3.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col3.setCellValueFactory(c -> c.getValue().getByte0Property());
        col3.setCellFactory(ValueTableCell.factoryStaticBin());

        col4 = new TableColumn<>("Byte 2");
        col4.setGraphic(new FontIcon(Material2OutlinedAL.LOOKS_ONE));
        col4.setSortable(false);
        col4.setEditable(false);
        col4.setReorderable(false);
        col4.setMinWidth(80);
        col4.setPrefWidth(80);
        col4.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col4.setCellValueFactory(c -> c.getValue().getByte1Property());
        col4.setCellFactory(ValueTableCell.factoryStaticBin());

        col5 = new TableColumn<>("Byte 1");
        col5.setGraphic(new FontIcon(Material2OutlinedAL.LOOKS_ONE));
        col5.setSortable(false);
        col5.setEditable(false);
        col5.setReorderable(false);
        col5.setMinWidth(80);
        col5.setPrefWidth(80);
        col5.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col5.setCellValueFactory(c -> c.getValue().getByte2Property());
        col5.setCellFactory(ValueTableCell.factoryStaticBin());

        col6 = new TableColumn<>("Byte 0");
        col6.setGraphic(new FontIcon(Material2OutlinedAL.LOOKS_ONE));
        col6.setSortable(false);
        col6.setEditable(false);
        col6.setReorderable(false);
        col6.setMinWidth(80);
        col6.setPrefWidth(80);
        col6.getStyleClass().add(Tweaks.ALIGN_CENTER);
        col6.setCellValueFactory(c -> c.getValue().getByte3Property());
        col6.setCellFactory(ValueTableCell.factoryStaticBin());

        memoryTable = new TableView<>();
        views = memoryTable.getItems();

        memoryTable.getColumns().setAll(col0, col1, col2, col3, col4, col5, col6);
        memoryTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        memoryTable.getStyleClass().addAll(Styles.STRIPED, Styles.DENSE, Tweaks.ALIGN_CENTER, Tweaks.EDGE_TO_EDGE);
        memoryTable.getSelectionModel().selectFirst();
        memoryTable.setTableMenuButtonVisible(true);
        memoryTable.setEditable(true);
        memoryTable.setMaxWidth(Double.POSITIVE_INFINITY);

        getMainMenuController().registerMemoryColumns(memoryTable);

        FontIcon icon = new FontIcon(Material2OutlinedAL.AUTORENEW);
        HBox placeHolder = new HBox(5, icon);

        icon.getStyleClass().add("medium-icon");
        placeHolder.setAlignment(Pos.CENTER);
        memoryTable.setPlaceholder(placeHolder);

        AnchorPane.setRightAnchor(memoryTable, 0d);
        AnchorPane.setBottomAnchor(memoryTable, 0d);
        AnchorPane.setLeftAnchor(memoryTable, 0d);
        AnchorPane.setTopAnchor(memoryTable, 0d);

        getController().memoryAnchorPane.getChildren().add(memoryTable);

        // Configuration du sélecteur de pages
        getController().memoryPage.setPageCount(PAGE_NUMBER);
        getController().memoryPage.setCurrentPageIndex(PAGE_OFFSET);
        getController().memoryPage.currentPageIndexProperty().addListener((observableValue, number, t1) -> {
            if (number.intValue() != t1.intValue()) {
                getExecutionWorker().updateGUI();
            }
        });
        
        lastPageIndex = PAGE_OFFSET;
        doSearchQuery = false;

        getController().addressField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                try {
                    StateContainer container = getCodeInterpreter().getStateContainer();
                    searchQuery = container.evalWithAll(getController().addressField.getText().strip().toUpperCase());
                    doSearchQuery = true;
                    int page = Math.floorDiv(searchQuery, ADDRESS_PER_PAGE) + PAGE_OFFSET;

                    if (page == getController().memoryPage.getCurrentPageIndex()) {
                        getExecutionWorker().updateGUI();
                    } else {
                        getController().memoryPage.setCurrentPageIndex(page);
                    }

                    getController().addressField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                } catch (Exception e) {
                    logger.info(ExceptionUtils.getStackTrace(e));
                    getController().addressField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                }
            }
        });

        getController().addressField.textProperty().addListener(((observableValue, oldVal, newVal) -> {
            if (getController().addressField.focusedProperty().get() && newVal.equalsIgnoreCase("")) {
                Bounds bounds = getController().addressField.localToScreen(getController().addressField.getBoundsInLocal());
                hintPop.show(getController().addressField, bounds.getMinX() - 10, bounds.getCenterY() - 30);
            } else {
                hintPop.hide();
            }
        } ));

        getController().addressField.focusedProperty().addListener(((observableValue, oldVal, newVal) -> {
            if (newVal && getController().addressField.getText().equalsIgnoreCase("")) {
                Bounds bounds = getController().addressField.localToScreen(getController().addressField.getBoundsInLocal());
                hintPop.show(getController().addressField, bounds.getMinX() - 10, bounds.getCenterY() - 30);
            }
        }));
    }

    /**
     * Met à jour la mémoire sur le GUI avec les informations du conteneur d'état.
     *
     * @apiNote Attention, ne pas exécuter sur l'Application Thread (pour des raisons de performances)
     * @param stateContainer le conteneur d'état
     */
    public void attach(StateContainer stateContainer) {
        if (stateContainer == null) {
            views.clear();
        } else {
            views.clear();
            lastPageIndex = (getController().memoryPage.getCurrentPageIndex());

            for (int i = 0; i < LINES_PER_PAGE; i++) {
                int add = ((getController().memoryPage.getCurrentPageIndex() - PAGE_OFFSET) * LINES_PER_PAGE + i) * ADDRESS_PER_LINE;

                views.add(new MemoryWordView(stateContainer.memory, add));
            }
        }
    }

    public void updatePage(StateContainer stateContainer) {
        if (getController().memoryPage.getCurrentPageIndex() != lastPageIndex) {
            attach(stateContainer);
        }

        if (doSearchQuery) {
            Platform.runLater(() -> {
                int firstAdd = ((getController().memoryPage.getCurrentPageIndex() - PAGE_OFFSET) * LINES_PER_PAGE) * ADDRESS_PER_LINE;
                int relativePos = Math.floorDiv(searchQuery - firstAdd, 4);

                memoryTable.scrollTo(relativePos);
                memoryTable.getFocusModel().focus(relativePos);
                memoryTable.getSelectionModel().select(relativePos);
                doSearchQuery = false;
            });
        }
    }

    public void refresh() {
        memoryTable.refresh();
    }
}
