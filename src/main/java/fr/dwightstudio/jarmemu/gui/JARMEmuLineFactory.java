package fr.dwightstudio.jarmemu.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.reactfx.collection.LiveList;
import org.reactfx.value.Val;

public class JARMEmuLineFactory implements IntFunction<Node> {

    private static final Insets DEFAULT_INSETS = new Insets(0.0, 5.0, 0.0, 5.0);
    private static final Paint DEFAULT_TEXT_FILL = Color.web("#858585");
    private static final Font DEFAULT_FONT = Font.font("monospace", FontPosture.REGULAR, 12.0);
    private static final Background DEFAULT_BACKGROUND =  new Background(new BackgroundFill(Color.web("#FFFFFF"), null, null));
    private static final Background EXECUTED_BACKGROUND =  new Background(new BackgroundFill(Color.web("#a7ff8a"), null, null));

    public ArrayList<Integer> breakpoints = new ArrayList<>();
    public HashMap<Integer, Consumer<Boolean>> nums = new HashMap<>();

    @Override
    public Node apply(int idx) {
        HBox rtn = new HBox();

        Label lineNo = new Label();
        lineNo.setFont(DEFAULT_FONT);
        lineNo.setBackground(DEFAULT_BACKGROUND);
        lineNo.setTextFill(DEFAULT_TEXT_FILL);
        lineNo.setPadding(DEFAULT_INSETS);
        lineNo.setAlignment(Pos.CENTER_RIGHT);
        lineNo.setText(String.format("%d", idx));
        lineNo.setMinWidth(32);
        lineNo.setMaxWidth(32);
        nums.put(idx, (b) -> {
            if (b) lineNo.setBackground(EXECUTED_BACKGROUND); else lineNo.setBackground(DEFAULT_BACKGROUND);
        });

        Label breakpoint = new Label();
        breakpoint.setFont(DEFAULT_FONT);
        breakpoint.setBackground(DEFAULT_BACKGROUND);
        breakpoint.setTextFill(DEFAULT_TEXT_FILL);
        breakpoint.setPadding(DEFAULT_INSETS);
        breakpoint.setAlignment(Pos.CENTER_LEFT);
        if (breakpoints.contains(idx)) breakpoint.setText("⬤"); else breakpoint.setText("");
        breakpoint.setMinWidth(32);
        breakpoint.setMaxWidth(32);
        breakpoint.setTooltip(new Tooltip("Toggle breakpoint"));
        breakpoint.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) toggle(idx, breakpoint);
        });

        rtn.getChildren().add(lineNo);
        rtn.getChildren().add(breakpoint);

        rtn.setAlignment(Pos.TOP_RIGHT);

        return rtn;
    }

    private void toggle(int id, Label label) {
        if (breakpoints.contains(id)) breakpoints.remove((Integer) id); else breakpoints.add(id);
        if (breakpoints.contains(id)) label.setText("⬤"); else label.setText("");
    }
}