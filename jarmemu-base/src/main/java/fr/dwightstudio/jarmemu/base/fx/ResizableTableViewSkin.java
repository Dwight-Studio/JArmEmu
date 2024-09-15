package fr.dwightstudio.jarmemu.base.fx;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ResizableTableViewSkin<T> extends TableViewSkin<T> {

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

    private List<TableColumn<T, ?>> getLeafColumns(TableView<T> tableView) {
        List<TableColumn<T, ?>> rtn = new ArrayList<>();

        for (TableColumn<T, ?> child : tableView.getColumns()) {
            rtn.addAll(getLeafColumnsRecursive(child));
        }

        return rtn;
    }

    private List<TableColumn<T, ?>> getLeafColumnsRecursive(TableColumn<T, ?> column) {
        if (column.getColumns() == null || column.getColumns().isEmpty()) {
            return List.of(column);
        } else {
            List<TableColumn<T, ?>> rtn = new ArrayList<>();

            for (TableColumn<T, ?> child : column.getColumns()) {
                rtn.addAll(getLeafColumnsRecursive(child));
            }

            return rtn;
        }
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        update();

        List<TableColumn<T, ?>> cols = getLeafColumns(getSkinnable());

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
