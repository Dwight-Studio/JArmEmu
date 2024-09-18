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

import atlantafx.base.theme.Styles;
import fr.dwightstudio.jarmemu.base.asm.Instruction;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2RoundAL;

public class InstructionDetailTableCell extends TableCell<Instruction, Instruction> {
    Button button;

    public InstructionDetailTableCell() {
        button = new Button();
        button.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT);
        button.setGraphic(new FontIcon(Material2RoundAL.INFO));
    }

    @Override
    protected void updateItem(Instruction instruction, boolean empty) {
        super.updateItem(instruction, empty);

        if (!empty && instruction != null) {
            button.setOnAction(event -> JArmEmuApplication.getDialogs().instructionDetail(instruction));

            setGraphic(button);
            setText("");
        } else {
            setGraphic(null);
        }
    }

    public static Callback<TableColumn<Instruction, Instruction>, TableCell<Instruction, Instruction>> factory() {
        return (val) -> new InstructionDetailTableCell();
    }
}
