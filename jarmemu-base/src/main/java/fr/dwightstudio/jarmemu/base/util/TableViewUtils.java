package fr.dwightstudio.jarmemu.base.util;

import atlantafx.base.theme.Tweaks;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
