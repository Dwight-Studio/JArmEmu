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

    public static class ResizableTableViewSkin<T> extends TableViewSkin<T> {

        private Method method;

        public ResizableTableViewSkin(TableView<T> tableView) {
            super(tableView);

            try {
                method = VirtualFlow.class.getDeclaredMethod("setSuppressBreadthBar", boolean.class);
                method.setAccessible(true);

            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        private void update() {
            try {
                method.invoke(getVirtualFlow(), true);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
            update();

            ObservableList<TableColumn<T, ?>> cols = getSkinnable().getColumns();

            if (cols == null || cols.isEmpty()) {
                return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
            }

            double pw = leftInset + rightInset;
            for (TableColumn<T, ?> tc : cols) {
                pw += tc.widthProperty().get();
            }

            return pw + 8;
        }


    }
}
