package fr.dwightstudio.jarmemu.gui.factory;

import fr.dwightstudio.jarmemu.gui.view.MemoryWordView;
import fr.dwightstudio.jarmemu.util.converters.CursorStringConverter;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

public class CursorTableCell extends TextFieldTableCell<MemoryWordView, Boolean> {

    private CursorTableCell() {
        super(new CursorStringConverter());
    }

    public static Callback<TableColumn<MemoryWordView, Boolean>, TableCell<MemoryWordView, Boolean>> factory() {
        return (val) -> new CursorTableCell();
    }
}
