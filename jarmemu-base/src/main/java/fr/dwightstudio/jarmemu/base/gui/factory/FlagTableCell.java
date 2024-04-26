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
import fr.dwightstudio.jarmemu.base.gui.view.RegisterView;
import fr.dwightstudio.jarmemu.base.sim.entity.ProgramStatusRegister;
import fr.dwightstudio.jarmemu.base.sim.entity.Register;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

import java.util.Arrays;

public class FlagTableCell extends TableCell<RegisterView, Register> {

    public static Callback<TableColumn<RegisterView, Register>, TableCell<RegisterView, Register>> factory() {
        return (val) -> new FlagTableCell();
    }

    HBox box;
    TextFlow text;

    Text n;
    Text z;
    Text c;
    Text v;
    Text i;
    Text f;
    Text t;

    Text[] texts;

    public FlagTableCell() {
        text = new TextFlow();
        n = new Text("N");
        z = new Text("Z");
        c = new Text("C");
        v = new Text("V");
        i = new Text("I");
        f = new Text("F");
        t = new Text("T");

        texts = new Text[]{n, z, c, v, i, f, t};

        Arrays.stream(texts).forEach(t -> t.setTextAlignment(TextAlignment.CENTER));

        text.getChildren().addAll(texts);
        text.setTextAlignment(TextAlignment.CENTER);

        box = new HBox(text);
        box.setAlignment(Pos.CENTER);

        setPrefHeight(20);
    }

    @Override
    protected boolean isItemChanged(Register register, Register t1) {
        return true;
    }

    @Override
    protected void updateItem(Register register, boolean empty) {
        super.updateItem(register, empty);
        
        if (register instanceof ProgramStatusRegister programStatusRegister) {
            n.getStyleClass().clear();
            z.getStyleClass().clear();
            c.getStyleClass().clear();
            v.getStyleClass().clear();
            i.getStyleClass().clear();
            f.getStyleClass().clear();
            t.getStyleClass().clear();

            if (programStatusRegister.getN()) {
                n.getStyleClass().addAll(Styles.TEXT, Styles.ACCENT, Styles.TEXT_BOLD);
            } else {
                n.getStyleClass().add(Styles.TEXT_SUBTLE);
            }

            if (programStatusRegister.getZ()) {
                z.getStyleClass().addAll(Styles.TEXT, Styles.ACCENT, Styles.TEXT_BOLD);
            } else {
                z.getStyleClass().add(Styles.TEXT_SUBTLE);
            }

            if (programStatusRegister.getC()) {
                c.getStyleClass().addAll(Styles.TEXT, Styles.ACCENT, Styles.TEXT_BOLD);
            } else {
                c.getStyleClass().add(Styles.TEXT_SUBTLE);
            }

            if (programStatusRegister.getV()) {
                v.getStyleClass().addAll(Styles.TEXT, Styles.ACCENT, Styles.TEXT_BOLD);
            } else {
                v.getStyleClass().add(Styles.TEXT_SUBTLE);
            }

            if (programStatusRegister.getI()) {
                i.getStyleClass().addAll(Styles.TEXT, Styles.ACCENT, Styles.TEXT_BOLD);
            } else {
                i.getStyleClass().add(Styles.TEXT_SUBTLE);
            }

            if (programStatusRegister.getF()) {
                f.getStyleClass().addAll(Styles.TEXT, Styles.ACCENT, Styles.TEXT_BOLD);
            } else {
                f.getStyleClass().add(Styles.TEXT_SUBTLE);
            }

            if (programStatusRegister.getT()) {
                t.getStyleClass().addAll(Styles.TEXT, Styles.ACCENT, Styles.TEXT_BOLD);
            } else {
                t.getStyleClass().add(Styles.TEXT_SUBTLE);
            }

            setGraphic(box);
            setText("");
        } else {
            setGraphic(null);
        }
    }
}
