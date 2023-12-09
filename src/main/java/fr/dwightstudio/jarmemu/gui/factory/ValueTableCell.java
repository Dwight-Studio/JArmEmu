/*
 *            ____           _       __    __     _____ __            ___
 *           / __ \_      __(_)___ _/ /_  / /_   / ___// /___  ______/ (_)___
 *          / / / / | /| / / / __ `/ __ \/ __/   \__ \/ __/ / / / __  / / __ \
 *         / /_/ /| |/ |/ / / /_/ / / / / /_    ___/ / /_/ /_/ / /_/ / / /_/ /
 *        /_____/ |__/|__/_/\__, /_/ /_/\__/   /____/\__/\__,_/\__,_/_/\____/
 *                         /____/
 *     Copyright (C) 2023 Dwight Studio
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

import fr.dwightstudio.jarmemu.Status;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.view.MemoryChunkView;
import fr.dwightstudio.jarmemu.util.converters.BinStringConverter;
import fr.dwightstudio.jarmemu.util.converters.HexStringConverter;
import fr.dwightstudio.jarmemu.util.converters.ValueStringConverter;
import fr.dwightstudio.jarmemu.util.converters.WordASCIIStringConverter;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.util.Objects;

public class ValueTableCell<S> extends TextFieldTableCell<S, Number> {
    private final Animation UPDATE_ANIMATION = new Transition() {

        {
            setCycleDuration(Duration.millis(1000));
            setInterpolator(Interpolator.EASE_OUT);
        }

        @Override
        protected void interpolate(double frac) {
            int percentage = (int) Math.floor(frac * 100);
            setStyle("-fx-background-color: ladder(derive(black, " + percentage + "%), -color-warning-muted, transparent 50%);");
        }
    };

    private final JArmEmuApplication application;

    private Number last;

    private ValueTableCell(JArmEmuApplication application) {
        super(new ValueStringConverter(application));
        this.application = application;
        this.getStyleClass().add("data-value");
        this.setAlignment(Pos.CENTER);
        last = 0;
    }

    private ValueTableCell(JArmEmuApplication application, StringConverter<Number> converter) {
        super(converter);
        this.application = application;
        this.getStyleClass().add("data-value");
        this.setAlignment(Pos.CENTER);
        last = 0;
    }

    @Override
    public void updateItem(Number number, boolean empty) {
        super.updateItem(number, empty);

        if (application.status.get() == Status.SIMULATING && !empty && !Objects.equals(number, last)) {
            Platform.runLater(UPDATE_ANIMATION::stop);
            Platform.runLater(UPDATE_ANIMATION::play);
            last = number;
        }
    }

    public static <S> Callback<TableColumn<S, Number>, TableCell<S, Number>> factoryDynamicFormat(JArmEmuApplication application) {
        return (val) -> new ValueTableCell<>(application);
    }

    public static <S> Callback<TableColumn<S, Number>, TableCell<S, Number>> factoryStaticHex(JArmEmuApplication application) {
        return (val) -> new ValueTableCell<>(application, new HexStringConverter());
    }

    public static <S> Callback<TableColumn<S, Number>, TableCell<S, Number>> factoryStaticBin(JArmEmuApplication application) {
        return (val) -> new ValueTableCell<>(application, new BinStringConverter());
    }

    public static <S> Callback<TableColumn<S, Number>, TableCell<S, Number>> factoryStaticWordASCII(JArmEmuApplication application) {
        return (val) -> new ValueTableCell<>(application, new WordASCIIStringConverter());
    }

    public static Callback<TableColumn<MemoryChunkView, String>, TableCell<MemoryChunkView, String>> factoryStaticString(JArmEmuApplication application) {
        return (val) -> {
            TextFieldTableCell<MemoryChunkView, String> rtn = new TextFieldTableCell<>();
            rtn.getStyleClass().add("data-value");
            rtn.setAlignment(Pos.CENTER);
            return rtn;
        };
    }
}
