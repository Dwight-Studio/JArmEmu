package fr.dwightstudio.jarmemu.gui;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.fxmisc.richtext.CodeArea;

public class EditorContextMenu extends ContextMenu {
    MenuItem cut;
    MenuItem copy;
    MenuItem paste;

    public EditorContextMenu(CodeArea codeArea) {
        cut = new MenuItem("Cut");
        copy = new MenuItem("Copy");
        paste = new MenuItem("Paste");

        cut.setOnAction((actionEvent -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(codeArea.getSelectedText());
            Clipboard.getSystemClipboard().setContent(content);
            codeArea.replaceSelection("");
        }));

        copy.setOnAction((actionEvent -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(codeArea.getSelectedText());
            Clipboard.getSystemClipboard().setContent(content);
        }));

        paste.setOnAction((actionEvent -> {
            codeArea.replaceSelection(Clipboard.getSystemClipboard().getString());
        }));

        getItems().add(cut);
        getItems().add(copy);
        getItems().add(paste);
    }
}
