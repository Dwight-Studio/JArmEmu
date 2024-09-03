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

import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.gui.view.MemoryChunkView;
import fr.dwightstudio.jarmemu.base.util.converters.BinStringConverter;
import fr.dwightstudio.jarmemu.base.util.converters.HexStringConverter;
import fr.dwightstudio.jarmemu.base.util.converters.ValueStringConverter;
import fr.dwightstudio.jarmemu.base.util.converters.WordASCIIStringConverter;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class ValueTableCell<S> extends TextFieldTableCell<S, Number> {

    private final ChangeListener<Number> CHANGE_LISTENER;

    private ObservableValue<Number> obs;
    private boolean changed;

    private ValueTableCell(StringConverter<Number> converter) {
        super(converter);
        this.getStyleClass().add("data-value");
        this.setAlignment(Pos.CENTER);

        CHANGE_LISTENER = (obs, oldVal, newVal) -> {
            if (JArmEmuApplication.getSettingsController().getHighlightUpdates())
                Platform.runLater(() -> {
                    setStyle("-fx-background-color: -color-warning-muted");
                    changed = true;
                });
        };

        JArmEmuApplication.getExecutionWorker().addStepListener((pos) -> {
            if (changed) setStyle("-fx-background-color: transparent");
            changed = false;
        });
    }

    private ValueTableCell() {
        this(new ValueStringConverter());
    }

    @Override
    public void updateItem(Number number, boolean empty) {
        super.updateItem(number, empty);

        if (getTableColumn() != null && getTableColumn().getCellObservableValue(getIndex()) != null) {
            if (obs != getTableColumn().getCellObservableValue(getIndex())) {
                if (obs != null) obs.removeListener(CHANGE_LISTENER);
                setStyle("-fx-background-color: transparent");
                obs = getTableColumn().getCellObservableValue(getIndex());
                obs.addListener(CHANGE_LISTENER);
            }
        }
    }



    public static <S> Callback<TableColumn<S, Number>, TableCell<S, Number>> factoryDynamicFormat() {
        return (val) -> new ValueTableCell<>();
    }

    public static <S> Callback<TableColumn<S, Number>, TableCell<S, Number>> factoryStaticHex() {
        return (val) -> new ValueTableCell<>(new HexStringConverter());
    }

    public static <S> Callback<TableColumn<S, Number>, TableCell<S, Number>> factoryStaticBin() {
        return (val) -> new ValueTableCell<>(new BinStringConverter());
    }

    public static <S> Callback<TableColumn<S, Number>, TableCell<S, Number>> factoryStaticWordASCII(JArmEmuApplication application) {
        return (val) -> new ValueTableCell<>(new WordASCIIStringConverter());
    }

    public static Callback<TableColumn<MemoryChunkView, String>, TableCell<MemoryChunkView, String>> factoryStaticString() {
        return (val) -> {
            TextFieldTableCell<MemoryChunkView, String> rtn = new TextFieldTableCell<>();
            rtn.getStyleClass().add("data-value");
            rtn.setAlignment(Pos.CENTER);
            return rtn;
        };
    }
}
