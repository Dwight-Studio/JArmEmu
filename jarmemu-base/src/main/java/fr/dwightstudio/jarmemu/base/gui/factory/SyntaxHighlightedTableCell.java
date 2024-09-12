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

import fr.dwightstudio.jarmemu.base.util.InstructionSyntaxUtils;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Region;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

public class SyntaxHighlightedTableCell<T> extends TableCell<T, String> {

    private final TextFlow textFlow;

    public SyntaxHighlightedTableCell() {
        textFlow = new TextFlow();textFlow.setMaxHeight(20);
        textFlow.setMinWidth(Region.USE_PREF_SIZE);
    }

    @Override
    protected void updateItem(String string, boolean empty) {
        super.updateItem(string, empty);


        if (!empty && string != null) {
            textFlow.getChildren().clear();
            textFlow.getChildren().addAll(InstructionSyntaxUtils.replacePlaceholder(string));

            setPrefHeight(20);
            setMinWidth(Region.USE_PREF_SIZE);

            setText("");
            setGraphic(textFlow);
        } else {
            setGraphic(null);
        }
    }

    public static <T> Callback<TableColumn<T, String>, TableCell<T, String>> factory() {
        return (val) -> new SyntaxHighlightedTableCell<>();
    }
}
