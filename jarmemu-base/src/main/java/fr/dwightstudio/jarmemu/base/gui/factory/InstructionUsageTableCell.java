package fr.dwightstudio.jarmemu.base.gui.factory;

import fr.dwightstudio.jarmemu.base.asm.Instruction;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.base.util.InstructionSyntaxUtils;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

public class InstructionUsageTableCell extends TableCell<Instruction, Instruction> {

    private final TextFlow textFlow;

    private InstructionUsageTableCell() {
        textFlow = new TextFlow();
        textFlow.getStyleClass().add("usage");
        textFlow.setMaxHeight(20);
        textFlow.setMinWidth(Region.USE_PREF_SIZE);
    }

    @Override
    protected void updateItem(Instruction instruction, boolean empty) {
        super.updateItem(instruction, empty);


        if (!empty && instruction != null) {
            textFlow.getChildren().clear();
            textFlow.getChildren().addAll(InstructionSyntaxUtils.getUsage(instruction));

            setPrefHeight(20);
            setMinWidth(Region.USE_PREF_SIZE);

            setText("");
            setGraphic(textFlow);
        }
    }

    public static Callback<TableColumn<Instruction, Instruction>, TableCell<Instruction, Instruction>> factory() {
        return (val) -> new InstructionUsageTableCell();
    }
}
