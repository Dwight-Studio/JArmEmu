package fr.dwightstudio.jarmemu.base.util;

import atlantafx.base.theme.Tweaks;
import javafx.scene.control.TableColumn;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

public class TableViewUtils {
    public static void setupColumn(TableColumn<?,?> col, Ikon icon, int width, boolean editable, boolean resizable, boolean sortable) {
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
