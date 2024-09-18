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

package fr.dwightstudio.jarmemu.base.fx;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ResizableTableViewSkin<T> extends TableViewSkin<T> {

    private Method method;

    public ResizableTableViewSkin(TableView<T> tableView) {
        super(tableView);

        try {
            method = VirtualFlow.class.getDeclaredMethod("setSuppressBreadthBar", boolean.class);
            method.setAccessible(true);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void update() {
        try {
            method.invoke(getVirtualFlow(), true);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private List<TableColumn<T, ?>> getLeafColumns(TableView<T> tableView) {
        List<TableColumn<T, ?>> rtn = new ArrayList<>();

        for (TableColumn<T, ?> child : tableView.getColumns()) {
            rtn.addAll(getLeafColumnsRecursive(child));
        }

        return rtn;
    }

    private List<TableColumn<T, ?>> getLeafColumnsRecursive(TableColumn<T, ?> column) {
        if (column.getColumns() == null || column.getColumns().isEmpty()) {
            return List.of(column);
        } else {
            List<TableColumn<T, ?>> rtn = new ArrayList<>();

            for (TableColumn<T, ?> child : column.getColumns()) {
                rtn.addAll(getLeafColumnsRecursive(child));
            }

            return rtn;
        }
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        update();

        List<TableColumn<T, ?>> cols = getLeafColumns(getSkinnable());

        if (cols == null || cols.isEmpty()) {
            return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
        }

        double pw = leftInset + rightInset;
        for (TableColumn<T, ?> tc : cols) {
            pw += tc.widthProperty().get();
        }

        return pw + 8;
    }
}
