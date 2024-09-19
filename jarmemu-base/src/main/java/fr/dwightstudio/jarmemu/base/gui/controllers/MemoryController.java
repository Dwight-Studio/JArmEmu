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

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Popover;
import atlantafx.base.theme.Styles;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.view.MemoryView;
import fr.dwightstudio.jarmemu.base.sim.entity.StateContainer;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public abstract class MemoryController<T extends MemoryView> implements Initializable {

    private static Logger logger = Logger.getLogger(MemoryController.class.getSimpleName());

    private Popover hintPop;
    private boolean doSearchQuery;
    private int searchQuery;

    public final void initialize(URL url, ResourceBundle resourceBundle) {
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
        hintPop.setArrowLocation(Popover.ArrowLocation.BOTTOM_CENTER);

        setupTableView();

        getPagination().currentPageIndexProperty().addListener((observableValue, number, t1) -> {
            if (number.intValue() != t1.intValue()) {
                JArmEmuApplication.getExecutionWorker().updateGUI();
            }
        });

        getTextField().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (doSearchQuery) return;
                try {
                    StateContainer container = JArmEmuApplication.getCodeInterpreter().getStateContainer();

                    searchQuery = container.evalWithAll(getTextField().getText().strip().toUpperCase());
                    doSearchQuery = true;
                    getTextField().pseudoClassStateChanged(Styles.STATE_DANGER, false);

                    int page = getPageIndex(searchQuery);

                    if (page == getPagination().getCurrentPageIndex()) {
                        JArmEmuApplication.getExecutionWorker().updateGUI();
                    } else {
                        getPagination().setCurrentPageIndex(page);
                    }
                } catch (Exception e) {
                    logger.severe(e.getMessage());
                    getTextField().pseudoClassStateChanged(Styles.STATE_DANGER, true);
                    showHint();
                }
            }
        });

        getTextField().textProperty().addListener(((observableValue, oldVal, newVal) -> {
            if (getTextField().focusedProperty().get() && newVal.equalsIgnoreCase("")) {
                showHint();
            } else {
                hintPop.hide();
            }
        } ));

        getTextField().focusedProperty().addListener(((observableValue, oldVal, newVal) -> {
            if (newVal && getTextField().getText().equalsIgnoreCase("")) {
                showHint();
            }
        }));

        doSearchQuery = false;
    }

    /**
     * Open address search hint
     */
    private void showHint() {
        hintPop.show(getTextField());
    }

    /**
     * Update the current displayed page in the view and attach the state container to the GUI
     * @param stateContainer the state container to tie to the GUI
     */
    public void updatePage(StateContainer stateContainer) {
        if (getPagination().getCurrentPageIndex() != getLastPageIndex()) {
            attach(stateContainer);
        }

        if (doSearchQuery) {
            Platform.runLater(() -> {
                int relativePos = getIndexInPage(getPagination().getCurrentPageIndex(), searchQuery);

                getMemoryTable().scrollTo(relativePos);
                getMemoryTable().getFocusModel().focus(relativePos);
                getMemoryTable().getSelectionModel().select(relativePos);
                doSearchQuery = false;
            });
        }
    }

    /**
     * Compute page index with address
     * @param address the address to search
     */
    public abstract int getPageIndex(int address);

    /**
     * @param pageIndex the page index
     * @return the page first address
     */
    public abstract int getPageFirstAddress(int pageIndex);

    /**
     * @param page the index of the page to search in
     * @param address the address
     * @return the index of the view in the table corresponding to the address
     */
    public abstract int getIndexInPage(int page, int address);

    /**
     * Set up the Table View
     */
    public abstract void setupTableView();

    /**
     * Tie the stateContainer to the GUI (any modification will appear in real time)
     *
     * @apiNote Do not execute it on application thread (to avoid poor performance)
     * @param stateContainer the state container to tie to the GUI
     */
    public abstract void attach(StateContainer stateContainer);

    /**
     * Refresh the view
     */
    public abstract void refresh();

    public abstract CustomTextField getTextField();

    public abstract Pagination getPagination();

    public abstract TableView<T> getMemoryTable();

    public abstract int getLastPageIndex();
}
