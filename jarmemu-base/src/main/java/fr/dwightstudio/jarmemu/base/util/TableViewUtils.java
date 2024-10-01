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

package fr.dwightstudio.jarmemu.base.util;

import atlantafx.base.theme.Tweaks;
import javafx.scene.control.TableColumn;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

public class TableViewUtils {
    public static void setupColumn(TableColumn<?,?> col, Ikon icon, double width, boolean editable, boolean resizable, boolean sortable) {
        if (icon != null) col.setGraphic(new FontIcon(icon));

        col.setMinWidth(width);
        if (!resizable) col.setMaxWidth(width);
        col.setPrefWidth(width);

        col.setReorderable(false);
        col.setSortable(sortable);
        col.setEditable(editable);
        col.setResizable(resizable);

        col.getStyleClass().add(Tweaks.ALIGN_CENTER);
    }
}
