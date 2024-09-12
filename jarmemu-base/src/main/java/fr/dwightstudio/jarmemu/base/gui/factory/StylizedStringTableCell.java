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

package fr.dwightstudio.jarmemu.base.gui.factory;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

public class StylizedStringTableCell<T> extends TableCell<T, String> {

    Label label;

    public StylizedStringTableCell(String clazz) {
        label = new Label();
        label.getStyleClass().addAll("text", "usage", clazz);
        setTextAlignment(TextAlignment.CENTER);
        setAlignment(Pos.CENTER);
    }

    @Override
    protected void updateItem(String string, boolean empty) {
        super.updateItem(string, empty);

        setText("");

        if (empty) {
            setGraphic(null);
        } else {
            label.setText(string);
            setGraphic(label);
        }
    }

    public static <T> Callback<TableColumn<T, String>, TableCell<T, String>> factory(String clazz) {
        return (val) -> new StylizedStringTableCell<>(clazz);
    }
}
