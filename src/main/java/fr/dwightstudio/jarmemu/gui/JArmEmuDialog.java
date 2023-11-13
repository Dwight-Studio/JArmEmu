package fr.dwightstudio.jarmemu.gui;

import atlantafx.base.layout.ModalBox;
import javafx.scene.Node;
import javafx.scene.control.Control;

public class JArmEmuDialog {

    private ModalBox box;

    public JArmEmuDialog(Node graphic, String title, String content, boolean closable, Control ... controls) {
        box = new ModalBox();


        box.addContent(null);
    }

    public ModalBox getModalBox() {
        return null;
    }
}
