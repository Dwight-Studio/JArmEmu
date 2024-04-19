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

package fr.dwightstudio.jarmemu.gui.factory;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.view.MemoryChunkView;
import fr.dwightstudio.jarmemu.util.converters.BinStringConverter;
import fr.dwightstudio.jarmemu.util.converters.HexStringConverter;
import fr.dwightstudio.jarmemu.util.converters.ValueStringConverter;
import fr.dwightstudio.jarmemu.util.converters.WordASCIIStringConverter;
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

    // RIP l'animation
    /*
    private final Animation UPDATE_ANIMATION = new Transition() {

        {
            setCycleDuration(Duration.seconds(10));
            setInterpolator(Interpolator.EASE_OUT);
        }

        @Override
        protected void interpolate(double frac) {
            int percentage = (int) Math.floor(frac * 100);
            setStyle("-fx-background-color: ladder(derive(black, " + percentage + "%), -color-warning-muted, transparent 50%);");
        }
    };
    */

    private final ChangeListener<Number> CHANGE_LISTENER;

    private ObservableValue<Number> obs;

    private ValueTableCell(StringConverter<Number> converter) {
        super(converter);
        this.getStyleClass().add("data-value");
        this.setAlignment(Pos.CENTER);

        CHANGE_LISTENER = (obs, oldVal, newVal) -> {
        /*
        Platform.runLater(UPDATE_ANIMATION::stop);
        Platform.runLater(UPDATE_ANIMATION::play);
         */
            if (JArmEmuApplication.getSettingsController().getHighlightUpdates())
                Platform.runLater(() -> setStyle("-fx-background-color: -color-warning-muted"));
        };

        JArmEmuApplication.getExecutionWorker().addStepListener((pos) -> setStyle("-fx-background-color: transparent"));
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
