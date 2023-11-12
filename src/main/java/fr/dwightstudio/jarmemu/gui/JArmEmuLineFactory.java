package fr.dwightstudio.jarmemu.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public class JArmEmuLineFactory implements IntFunction<Node> {

    public ArrayList<Integer> breakpoints = new ArrayList<>();
    private final HashMap<Integer, Node> nodes = new HashMap<>();

    @Override
    public Node apply(int line) {
        Node rtn = nodes.get(line);

        if (rtn == null) {
            rtn = generate(line);
        }

        return rtn;
    }

    public Node generate(int line) {
        HBox rtn = new HBox();

        rtn.setMaxWidth(70);
        rtn.setMinWidth(70);
        rtn.setPrefWidth(70);

        Text lineNo = new Text();
        Text breakpoint = new Text();

        lineNo.getStyleClass().add("lineno");
        breakpoint.getStyleClass().add("breakpoint");

        lineNo.setText(String.format("%4d", line));
        if (breakpoints.contains(line)) breakpoint.setText(" ⬤ "); else breakpoint.setText("   ");

        breakpoint.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) toggle(line, breakpoint);
        });

        rtn.getChildren().add(lineNo);
        rtn.getChildren().add(breakpoint);

        return rtn;
    }

    private void toggle(int id, Text label) {
        if (breakpoints.contains(id)) breakpoints.remove((Integer) id); else breakpoints.add(id);
        if (breakpoints.contains(id)) label.setText(" ⬤ "); else label.setText("   ");
    }

    public void pregenAll(int lineNum) {
        for (int i = 0; i < lineNum; i++) {
            apply(i);
        }
    }
}