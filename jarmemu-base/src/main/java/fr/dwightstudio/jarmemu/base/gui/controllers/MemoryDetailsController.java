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

import atlantafx.base.controls.Popover;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.factory.AddressTableCell;
import fr.dwightstudio.jarmemu.base.gui.factory.ValueTableCell;
import fr.dwightstudio.jarmemu.base.gui.view.MemoryWordView;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import fr.dwightstudio.jarmemu.base.util.TableViewUtils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
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

public class MemoryDetailsController implements Initializable {

    protected static final int LINES_PER_PAGE = 512;
    protected static final int ADDRESS_PER_LINE = 4;
    protected static final int ADDRESS_PER_PAGE = LINES_PER_PAGE * ADDRESS_PER_LINE;
    protected static final int PAGE_NUMBER = (int) (((long) Math.pow(2L, 32L)) / ADDRESS_PER_PAGE);
    protected static final int PAGE_OFFSET = PAGE_NUMBER/2;

    private final Logger logger = Logger.getLogger(getClass().getSimpleName());
    private Popover hintPop;
    private TableColumn<MemoryWordView, Number> col0;
    private TableColumn<MemoryWordView, Number> col1;
    private TableColumn<MemoryWordView, Number> col2;
    private TableColumn<MemoryWordView, Number> col3;
    private TableColumn<MemoryWordView, Number> col4;
    private TableColumn<MemoryWordView, Number> col5;
    private TableColumn<MemoryWordView, Number> col6;
    private ObservableList<MemoryWordView> views;
    protected TableView<MemoryWordView> memoryTable;
    private int lastPageIndex;
    private boolean doSearchQuery;
    private int searchQuery;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        TextFlow textFlow = new TextFlow(new Text(JArmEmuApplication.formatMessage("%pop.memoryHint.message", "→")));
        textFlow.setLineSpacing(5);
        textFlow.setPrefWidth(400);
        textFlow.setPadding(new Insets(10, 0, 10, 0));

        hintPop = new Popover(textFlow);
        hintPop.setTitle(JArmEmuApplication.formatMessage("%pop.memoryHint.title"));
        hintPop.setHeaderAlwaysVisible(true);
        hintPop.setDetachable(false);
        hintPop.setAnimated(true);
        hintPop.setCloseButtonEnabled(true);
        hintPop.setArrowLocation(Popover.ArrowLocation.RIGHT_CENTER);

        col0 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.memoryDetails.address"));
        TableViewUtils.setupColumn(col0, Material2OutlinedAL.ALTERNATE_EMAIL, 80, false, true, false);
        col0.setCellValueFactory(c -> c.getValue().getAddressProperty());
        col0.setCellFactory(AddressTableCell.factory());

        col1 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.memoryDetails.value"));
        TableViewUtils.setupColumn(col1, Material2OutlinedMZ.MONEY, 80, true, true, false);
        col1.setCellValueFactory(c -> c.getValue().getValueProperty());
        col1.setCellFactory(ValueTableCell.factoryDynamicFormat());

        col2 = new TableColumn<>("ASCII");
        TableViewUtils.setupColumn(col2, Material2OutlinedMZ.SHORT_TEXT, 80, false, true, false);
        col2.setCellValueFactory(c -> c.getValue().getValueProperty());
        col2.setCellFactory(ValueTableCell.factoryStaticWordASCII(JArmEmuApplication.getInstance()));
        col2.setVisible(false);

        col3 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.memoryDetails.byte", 3));
        TableViewUtils.setupColumn(col3, Material2OutlinedAL.LOOKS_ONE, 80, false, true, false);
        col3.setCellValueFactory(c -> c.getValue().getByte0Property());
        col3.setCellFactory(ValueTableCell.factoryStaticBin());

        col4 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.memoryDetails.byte", 2));
        TableViewUtils.setupColumn(col4, Material2OutlinedAL.LOOKS_ONE, 80, false, true, false);
        col4.setCellValueFactory(c -> c.getValue().getByte1Property());
        col4.setCellFactory(ValueTableCell.factoryStaticBin());

        col5 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.memoryDetails.byte", 1));
        TableViewUtils.setupColumn(col5, Material2OutlinedAL.LOOKS_ONE, 80, false, true, false);
        col5.setCellValueFactory(c -> c.getValue().getByte2Property());
        col5.setCellFactory(ValueTableCell.factoryStaticBin());

        col6 = new TableColumn<>(JArmEmuApplication.formatMessage("%tab.memoryDetails.byte", 0));
        TableViewUtils.setupColumn(col6, Material2OutlinedAL.LOOKS_ONE, 80, false, true, false);
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

        JArmEmuApplication.getMainMenuController().registerMemoryDetailsColumns();

        FontIcon icon = new FontIcon(Material2OutlinedAL.AUTORENEW);
        HBox placeHolder = new HBox(5, icon);

        icon.getStyleClass().add("medium-icon");
        placeHolder.setAlignment(Pos.CENTER);
        memoryTable.setPlaceholder(placeHolder);

        AnchorPane.setRightAnchor(memoryTable, 0d);
        AnchorPane.setBottomAnchor(memoryTable, 0d);
        AnchorPane.setLeftAnchor(memoryTable, 0d);
        AnchorPane.setTopAnchor(memoryTable, 0d);

        JArmEmuApplication.getController().memoryDetailsAnchorPane.getChildren().add(memoryTable);

        // Configuration du sélecteur de pages
        JArmEmuApplication.getController().memoryDetailsPage.setPageCount(PAGE_NUMBER);
        JArmEmuApplication.getController().memoryDetailsPage.setCurrentPageIndex(PAGE_OFFSET);
        JArmEmuApplication.getController().memoryDetailsPage.currentPageIndexProperty().addListener((observableValue, number, t1) -> {
            if (number.intValue() != t1.intValue()) {
                JArmEmuApplication.getExecutionWorker().updateGUI();
            }
        });
        
        lastPageIndex = PAGE_OFFSET;
        doSearchQuery = false;

        JArmEmuApplication.getController().memoryDetailsAddressField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                try {
                    StateContainer container = JArmEmuApplication.getCodeInterpreter().getStateContainer();
                    searchQuery = container.evalWithAll(JArmEmuApplication.getController().memoryDetailsAddressField.getText().strip().toUpperCase());
                    doSearchQuery = true;
                    int page = Math.floorDiv(searchQuery, ADDRESS_PER_PAGE) + PAGE_OFFSET;

                    if (page == JArmEmuApplication.getController().memoryDetailsPage.getCurrentPageIndex()) {
                        JArmEmuApplication.getExecutionWorker().updateGUI();
                    } else {
                        JArmEmuApplication.getController().memoryDetailsPage.setCurrentPageIndex(page);
                    }

                    JArmEmuApplication.getController().memoryDetailsAddressField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                } catch (Exception e) {
                    logger.info(ExceptionUtils.getStackTrace(e));
                    JArmEmuApplication.getController().memoryDetailsAddressField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                }
            }
        });

        JArmEmuApplication.getController().memoryDetailsAddressField.textProperty().addListener(((observableValue, oldVal, newVal) -> {
            if (JArmEmuApplication.getController().memoryDetailsAddressField.focusedProperty().get() && newVal.equalsIgnoreCase("")) {
                Bounds bounds = JArmEmuApplication.getController().memoryDetailsAddressField.localToScreen(JArmEmuApplication.getController().memoryDetailsAddressField.getBoundsInLocal());
                hintPop.show(JArmEmuApplication.getController().memoryDetailsAddressField, bounds.getMinX() - 10, bounds.getCenterY() - 30);
            } else {
                hintPop.hide();
            }
        } ));

        JArmEmuApplication.getController().memoryDetailsAddressField.focusedProperty().addListener(((observableValue, oldVal, newVal) -> {
            if (newVal && JArmEmuApplication.getController().memoryDetailsAddressField.getText().equalsIgnoreCase("")) {
                Bounds bounds = JArmEmuApplication.getController().memoryDetailsAddressField.localToScreen(JArmEmuApplication.getController().memoryDetailsAddressField.getBoundsInLocal());
                hintPop.show(JArmEmuApplication.getController().memoryDetailsAddressField, bounds.getMinX() - 10, bounds.getCenterY() - 30);
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
            lastPageIndex = (JArmEmuApplication.getController().memoryDetailsPage.getCurrentPageIndex());

            for (int i = 0; i < LINES_PER_PAGE; i++) {
                int add = ((JArmEmuApplication.getController().memoryDetailsPage.getCurrentPageIndex() - PAGE_OFFSET) * LINES_PER_PAGE + i) * ADDRESS_PER_LINE;

                views.add(new MemoryWordView(stateContainer.getMemory(), add));
            }
        }
    }

    public void updatePage(StateContainer stateContainer) {
        if (JArmEmuApplication.getController().memoryDetailsPage.getCurrentPageIndex() != lastPageIndex) {
            attach(stateContainer);
        }

        if (doSearchQuery) {
            Platform.runLater(() -> {
                int firstAdd = ((JArmEmuApplication.getController().memoryDetailsPage.getCurrentPageIndex() - PAGE_OFFSET) * LINES_PER_PAGE) * ADDRESS_PER_LINE;
                int relativePos = Math.floorDiv(searchQuery - firstAdd, ADDRESS_PER_LINE);

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
