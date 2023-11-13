package fr.dwightstudio.jarmemu.gui;

import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.fxmisc.richtext.CodeArea;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

public class EditorContextMenu extends ContextMenu {

    CodeArea codeArea;
    MenuItem cut;
    MenuItem copy;
    MenuItem paste;
    MenuItem delete;

    public EditorContextMenu(CodeArea codeArea) {
        this.codeArea = codeArea;

        copy = new MenuItem("Copy", new FontIcon(Material2OutlinedAL.CONTENT_COPY));
        cut = new MenuItem("Cut", new FontIcon(Material2OutlinedAL.CONTENT_CUT));
        paste = new MenuItem("Paste", new FontIcon(Material2OutlinedAL.CONTENT_PASTE));
        delete = new MenuItem("Delete", new FontIcon(Material2OutlinedAL.DELETE));

        copy.setOnAction(this::onCopy);
        cut.setOnAction(this::onCut);
        paste.setOnAction(this::onPaste);
        delete.setOnAction(this::onDelete);

        getItems().add(copy);
        getItems().add(cut);
        getItems().add(paste);
        getItems().add(delete);
    }

    public void onCut(ActionEvent event) {
        ClipboardContent content = new ClipboardContent();
        content.putString(codeArea.getSelectedText());
        Clipboard.getSystemClipboard().setContent(content);
        codeArea.replaceSelection("");
    }

    public void onCopy(ActionEvent event) {
        ClipboardContent content = new ClipboardContent();
        content.putString(codeArea.getSelectedText());
        Clipboard.getSystemClipboard().setContent(content);
    }

    public void onPaste(ActionEvent event) {
        codeArea.replaceSelection(Clipboard.getSystemClipboard().getString());
    }

    public void onDelete(ActionEvent event) {
        codeArea.replaceSelection("");
    }
}
