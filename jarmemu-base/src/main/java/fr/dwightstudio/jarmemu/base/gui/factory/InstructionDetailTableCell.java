package fr.dwightstudio.jarmemu.base.gui.factory;

import atlantafx.base.theme.Styles;
import fr.dwightstudio.jarmemu.base.asm.Instruction;
import fr.dwightstudio.jarmemu.base.gui.JArmEmuApplication;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2RoundAL;

public class InstructionDetailTableCell extends TableCell<Instruction, Instruction> {
    Button button;

    public InstructionDetailTableCell() {
        button = new Button();
        button.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT);
        button.setGraphic(new FontIcon(Material2RoundAL.INFO));
    }

    @Override
    protected void updateItem(Instruction instruction, boolean empty) {
        super.updateItem(instruction, empty);

        if (!empty && instruction != null) {
            button.setOnAction(event -> JArmEmuApplication.getDialogs().instructionDetail(instruction));

            setGraphic(button);
            setText("");
        }
    }

    public static Callback<TableColumn<Instruction, Instruction>, TableCell<Instruction, Instruction>> factory() {
        return (val) -> new InstructionDetailTableCell();
    }
}
