package fr.dwightstudio.jarmemu.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.fxmisc.richtext.CodeArea;

import java.net.URL;
import java.util.ResourceBundle;

public class JAREmuController implements Initializable {

    public EditorManager editorManager;

    @FXML
    protected CodeArea codeArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        editorManager.init(codeArea);
    }
}