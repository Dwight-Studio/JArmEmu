package fr.dwightstudio.jarmemu.gui.factory;

import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.util.converters.BinStringConverter;
import fr.dwightstudio.jarmemu.util.converters.HexStringConverter;
import fr.dwightstudio.jarmemu.util.converters.ValueStringConverter;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class ValueTableCell<S> extends TextFieldTableCell<S, Number> {

    private ValueTableCell(JArmEmuApplication application) {
        super(new ValueStringConverter(application));
        this.getStyleClass().add("data-value");
    }

    private ValueTableCell(StringConverter<Number> converter) {
        super(converter);
        this.getStyleClass().add("data-value");
    }


    public static <S> Callback<TableColumn<S, Number>, TableCell<S, Number>> factoryDynamicFormat(JArmEmuApplication application) {
        return (val) -> new ValueTableCell<>(application);
    }

    public static <S> Callback<TableColumn<S, Number>, TableCell<S, Number>> factoryStaticHex() {
        return (val) -> new ValueTableCell<>(new HexStringConverter());
    }

    public static <S> Callback<TableColumn<S, Number>, TableCell<S, Number>> factoryStaticBin() {
        return (val) -> new ValueTableCell<>(new BinStringConverter());
    }
}
