package fr.dwightstudio.jarmemu.gui.factory;

import fr.dwightstudio.jarmemu.util.converters.HexStringConverter;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

public class AddressTableCell<S> extends TextFieldTableCell<S, Number> {

    private AddressTableCell() {
        super(new HexStringConverter());
        this.getStyleClass().add("data-address");
    }

    public static <S> Callback<TableColumn<S, Number>, TableCell<S, Number>> factory() {
        return (val) -> new AddressTableCell<>();
    }
}
