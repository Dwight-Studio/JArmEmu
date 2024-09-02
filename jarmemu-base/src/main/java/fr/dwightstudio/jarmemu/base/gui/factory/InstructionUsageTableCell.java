package fr.dwightstudio.jarmemu.base.gui.factory;

import fr.dwightstudio.jarmemu.base.asm.Instruction;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class InstructionUsageTableCell extends TableCell<Instruction, Instruction> {

    private InstructionUsageTableCell() {
        this.getStyleClass().add("data-address");
    }

    @Override
    protected void updateItem(Instruction instruction, boolean empty) {
        super.updateItem(instruction, empty);

        if (!empty && instruction != null) {
            setText("");
        }
    }

    public static Callback<TableColumn<Instruction, Instruction>, TableCell<Instruction, Instruction>> factory() {
        return (val) -> new InstructionUsageTableCell();
    }
}
